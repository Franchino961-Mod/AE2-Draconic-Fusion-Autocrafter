package com.ae2draconicfusion.autocrafter.fusion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FusionRoutingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FusionRoutingService.class);

    public record CatalystInfo(Ingredient ingredient, int count) {
    }

    private final FusionStructureScanner structureScanner;

    public FusionRoutingService(FusionStructureScanner structureScanner) {
        this.structureScanner = structureScanner;
    }

    public FusionRoutingResult routeItemStackToFusionStructure(ServerLevel level, BlockPos corePos,
            ItemStack sourceStack, int scanRange) {
        return routeItemStackToFusionStructure(level, corePos, sourceStack, scanRange, false, null);
    }

    public FusionRoutingResult routeItemStackToFusionStructure(ServerLevel level, BlockPos corePos,
            ItemStack sourceStack, int scanRange, boolean simulate) {
        return routeItemStackToFusionStructure(level, corePos, sourceStack, scanRange, simulate, null);
    }

    /**
     * Routes a single item stack to the Fusion Core or one of its injectors.
     *
     * @param catalystInfo optional catalyst info (ingredient and count)
     *                     pre-resolved;
     *                     when non-null, catalyst routing works accurately.
     */
    public FusionRoutingResult routeItemStackToFusionStructure(ServerLevel level, BlockPos corePos,
            ItemStack sourceStack, int scanRange, boolean simulate, @Nullable CatalystInfo catalystInfo) {
        if (sourceStack == null || sourceStack.isEmpty()) {
            LOGGER.debug("Routing aborted for core {}: source stack is null or empty", corePos);
            return FusionRoutingResult.TEMPORARY_FAILURE;
        }

        FusionStructureScanner.FusionStructureSnapshot snapshot = structureScanner.scan(level, corePos, scanRange);
        LOGGER.debug("Routing snapshot for core {}: validCore={}, coreBusy={}, injectors={}, fusionState={}",
                corePos, snapshot.validCore(), snapshot.coreBusy(), snapshot.injectors().size(),
                snapshot.fusionState());

        // B001: a busy core is valid but temporarily unavailable — return
        // TEMPORARY_FAILURE, not INVALID_CORE
        if (snapshot.coreBusy()) {
            LOGGER.debug("Routing deferred for core {}: core is busy ({})", corePos, snapshot.fusionState());
            return FusionRoutingResult.TEMPORARY_FAILURE;
        }
        if (!snapshot.validCore() || snapshot.injectors().isEmpty()) {
            FusionRoutingResult result = snapshot.validCore() ? FusionRoutingResult.NO_INJECTORS_FOUND
                    : FusionRoutingResult.INVALID_CORE;
            LOGGER.debug("Routing rejected for core {}: {}", corePos, result);
            return result;
        }

        List<Object> orderedInjectors = orderInjectorsDeterministically(snapshot.corePos(), snapshot.injectors());
        LOGGER.debug("Deterministic injector order resolved for core {} with {} injectors.", snapshot.corePos(),
                orderedInjectors.size());

        // B004: pass pre-resolved catalyst ingredient
        Ingredient catalystIngredient = catalystInfo != null ? catalystInfo.ingredient() : null;
        RecipeRoutingContext recipeContext = buildRecipeRoutingContext(snapshot.core(), orderedInjectors,
                catalystIngredient);
        if (recipeContext.recipeAware() && recipeContext.hasIncompatiblePrefilledInjectors()) {
            LOGGER.debug(
                    "Routing aborted for core {} because one or more prefilled injectors are incompatible with the active recipe.",
                    snapshot.corePos());
            return FusionRoutingResult.TARGET_SLOTS_OCCUPIED;
        }

        ItemStack remainingStack = sourceStack.copy();

        // B004 fix: catalyst routing is independent of recipeAware — it works whenever
        // catalystIngredient is known
        if (recipeContext.catalystIngredient() != null && recipeContext.catalystIngredient().test(sourceStack)) {
            if (!recipeContext.currentCatalyst().isEmpty()) {
                // Catalyst slot is occupied — check if it already contains the CORRECT catalyst
                if (recipeContext.catalystIngredient().test(recipeContext.currentCatalyst())) {
                    // Correct catalyst already present (e.g. non-consumable or post-craft delay).
                    // Treat as already placed — SUCCESS so AE2 doesn't block.
                    LOGGER.debug("Core {} already has matching catalyst {}; treating as already placed.",
                            snapshot.corePos(), recipeContext.currentCatalyst());
                    return FusionRoutingResult.SUCCESS;
                }
                // Different item in core slot — truly occupied by wrong item
                LOGGER.debug("Core {} has a different catalyst {}; refusing {}.",
                        snapshot.corePos(), recipeContext.currentCatalyst(), sourceStack);
                return FusionRoutingResult.TARGET_SLOTS_OCCUPIED;
            }

            int needed = catalystInfo != null ? catalystInfo.count() : 1;
            int toTake = Math.min(sourceStack.getCount(), needed);

            ItemStack catalystStack = sourceStack.copy();
            catalystStack.setCount(toTake);
            if (simulate || invokeSetCatalyst(snapshot.core(), catalystStack)) {
                remainingStack.shrink(toTake);
                LOGGER.info("[AE2DraconicFusion] Routed catalyst {} x{} directly into core {}.",
                        catalystStack.getItem(), toTake, snapshot.corePos());
                if (remainingStack.isEmpty()) {
                    return FusionRoutingResult.SUCCESS;
                }
            } else {
                LOGGER.info("[AE2DraconicFusion] Failed to set catalyst {} on core {}.", catalystStack,
                        snapshot.corePos());
                return FusionRoutingResult.TEMPORARY_FAILURE;
            }
        }

        List<ItemStack> originalStacks = new ArrayList<>(orderedInjectors.size());
        boolean placedSomething = false;

        for (Object injector : orderedInjectors) {
            Object originalStack = invokeNoArg(injector, "getInjectorStack");
            originalStacks.add(originalStack instanceof ItemStack itemStack ? itemStack.copy() : ItemStack.EMPTY);

            if (remainingStack.isEmpty()) {
                break;
            }

            Object currentStack = invokeNoArg(injector, "getInjectorStack");
            if (currentStack instanceof ItemStack currentItemStack && !currentItemStack.isEmpty()) {
                LOGGER.debug("Skipping injector {} because it already contains {}.", resolveInjectorPos(injector),
                        currentItemStack);
                continue;
            }

            ItemStack singleItem = remainingStack.copy();
            singleItem.setCount(1);

            if (recipeContext.recipeAware() && !recipeContext.consumeMatchingRemaining(singleItem)) {
                LOGGER.debug("Skipping injector {} because {} does not match remaining recipe ingredients.",
                        resolveInjectorPos(injector), singleItem);
                continue;
            }

            if (simulate || invokeSetStack(injector, singleItem)) {
                remainingStack.shrink(1);
                placedSomething = true;
                LOGGER.info("[AE2DraconicFusion] Assigned {} to injector {}. Remaining count: {}", singleItem,
                        resolveInjectorPos(injector), remainingStack.getCount());
            } else {
                recipeContext.restoreRemainingState();
                rollbackInjectors(orderedInjectors, originalStacks);
                LOGGER.info("[AE2DraconicFusion] Injector assignment failed at {}. Rollback executed.",
                        resolveInjectorPos(injector));
                return FusionRoutingResult.TEMPORARY_FAILURE;
            }
        }

        if (!placedSomething) {
            // No injector accepted the item. This is always retryable: slots may be
            // temporarily occupied
            // (e.g. craft finishing, injectors not yet cleared) or the recipe matching will
            // clear next tick.
            LOGGER.debug("No injector accepted {} for core {}; returning TEMPORARY_FAILURE to allow retry.",
                    sourceStack.getItem(), snapshot.corePos());
            return FusionRoutingResult.TEMPORARY_FAILURE;
        }

        if (!remainingStack.isEmpty()) {
            recipeContext.restoreRemainingState();
            if (!simulate) {
                rollbackInjectors(orderedInjectors, originalStacks);
            }
            LOGGER.debug("Not enough valid injectors for {} remaining items. Rollback executed.",
                    remainingStack.getCount());
            return FusionRoutingResult.INJECTORS_INSUFFICIENT;
        }

        return FusionRoutingResult.SUCCESS;
    }

    /**
     * B004: Pre-resolves the catalyst ingredient for a given pattern output by
     * scanning all registered
     * fusion crafting recipes in the recipe manager BEFORE any items are placed.
     * This breaks the chicken-and-egg dependency on {getActiveRecipe()} being
     * non-null.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public @Nullable CatalystInfo resolveCatalystForOutput(ServerLevel level, ItemStack patternOutput) {
        if (level == null || patternOutput == null || patternOutput.isEmpty()) {
            return null;
        }
        try {
            for (RecipeType<?> type : BuiltInRegistries.RECIPE_TYPE) {
                ResourceLocation typeId = BuiltInRegistries.RECIPE_TYPE.getKey(type);
                if (typeId == null)
                    continue;
                String typeStr = typeId.toString().toLowerCase();
                // Filter to DE fusion crafting recipe types only
                if (!typeStr.contains("fusion"))
                    continue;

                List<RecipeHolder<?>> holders = getRecipes(level, type);
                for (RecipeHolder<?> holder : holders) {
                    Object recipe = holder.value();
                    if (!matchesRecipeOutput(recipe, patternOutput, level))
                        continue;
                    CatalystInfo catalyst = resolveCatalystInfo(recipe);
                    if (catalyst != null && catalyst.ingredient() != null && !catalyst.ingredient().isEmpty()) {
                        LOGGER.debug("Pre-resolved catalyst for {} from fusion recipe type {}", patternOutput.getItem(),
                                typeId);
                        return catalyst;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to pre-resolve catalyst from recipe registry: {}", e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <I extends net.minecraft.world.item.crafting.RecipeInput, T extends net.minecraft.world.item.crafting.Recipe<I>> List<RecipeHolder<?>> getRecipes(
            ServerLevel level, RecipeType<?> type) {
        return (List<RecipeHolder<?>>) (Object) level.getRecipeManager().getAllRecipesFor((RecipeType<T>) type);
    }

    private boolean matchesRecipeOutput(Object recipe, ItemStack expected, ServerLevel level) {
        // Try 1.21+ API: getResultItem(HolderLookup.Provider)
        try {
            Object result = recipe.getClass()
                    .getMethod("getResultItem", net.minecraft.core.HolderLookup.Provider.class)
                    .invoke(recipe, level.registryAccess());
            if (result instanceof ItemStack s && !s.isEmpty() && ItemStack.isSameItem(s, expected))
                return true;
        } catch (Exception ignored) {
        }
        // Fallback: getResultItem() without args
        Object result = invokeNoArg(recipe, "getResultItem");
        if (result instanceof ItemStack s && !s.isEmpty() && ItemStack.isSameItem(s, expected))
            return true;
        // Fallback: getOutput()
        result = invokeNoArg(recipe, "getOutput");
        if (result instanceof ItemStack s && !s.isEmpty() && ItemStack.isSameItem(s, expected))
            return true;
        return false;
    }

    private List<Object> orderInjectorsDeterministically(BlockPos corePos, List<?> injectors) {
        List<Object> orderedInjectors = new ArrayList<>(injectors);
        orderedInjectors.sort(Comparator
                .comparingLong((Object injector) -> squaredDistance(corePos, resolveInjectorPos(injector)))
                .thenComparingInt(injector -> directionRank(corePos, resolveInjectorPos(injector)))
                .thenComparingInt(injector -> resolveInjectorPos(injector).getX())
                .thenComparingInt(injector -> resolveInjectorPos(injector).getY())
                .thenComparingInt(injector -> resolveInjectorPos(injector).getZ()));
        return orderedInjectors;
    }

    public boolean isCoreCrafting(Object core) {
        Object crafting = invokeNoArg(core, "isCrafting");
        return crafting instanceof Boolean b && b;
    }

    public void triggerStartCraft(Object core) {
        if (core == null)
            return;
        try {
            core.getClass().getMethod("startCraft").invoke(core);
            LOGGER.info("[AE2DraconicFusion] Crafting started automatically on core.");
        } catch (ReflectiveOperationException e) {
            LOGGER.error("[AE2DraconicFusion] CRITICAL: Failed to call startCraft on core via reflection: {}",
                    e.getMessage());
        }
    }

    public FusionStructureScanner getStructureScanner() {
        return structureScanner;
    }

    private BlockPos resolveInjectorPos(Object injector) {
        Object posObject = invokeNoArg(injector, "getBlockPos");
        return posObject instanceof BlockPos blockPos ? blockPos : BlockPos.ZERO;
    }

    private long squaredDistance(BlockPos corePos, BlockPos injectorPos) {
        long dx = injectorPos.getX() - corePos.getX();
        long dy = injectorPos.getY() - corePos.getY();
        long dz = injectorPos.getZ() - corePos.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private int directionRank(BlockPos corePos, BlockPos injectorPos) {
        int dx = injectorPos.getX() - corePos.getX();
        int dy = injectorPos.getY() - corePos.getY();
        int dz = injectorPos.getZ() - corePos.getZ();

        if (Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= Math.abs(dz)) {
            return dx >= 0 ? rank(Direction.EAST) : rank(Direction.WEST);
        }
        if (Math.abs(dz) >= Math.abs(dx) && Math.abs(dz) >= Math.abs(dy)) {
            return dz >= 0 ? rank(Direction.SOUTH) : rank(Direction.NORTH);
        }
        return dy >= 0 ? rank(Direction.UP) : rank(Direction.DOWN);
    }

    private int rank(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            case UP -> 4;
            case DOWN -> 5;
        };
    }

    private RecipeRoutingContext buildRecipeRoutingContext(Object core, List<Object> orderedInjectors,
            @Nullable Ingredient preResolvedCatalyst) {
        if (core == null) {
            return RecipeRoutingContext.disabled();
        }

        Object recipeHolder = invokeNoArg(core, "getActiveRecipe");
        if (recipeHolder == null) {
            ItemStack currentCatalyst = getCurrentCatalyst(core);
            // B004: if we pre-resolved the catalyst from the recipe registry, use it even
            // without an active recipe
            if (preResolvedCatalyst != null) {
                return RecipeRoutingContext.disabledWithKnownCatalyst(preResolvedCatalyst, currentCatalyst);
            }
            return RecipeRoutingContext.disabledWithCatalyst(currentCatalyst);
        }

        Object recipe = invokeNoArg(recipeHolder, "value");
        if (recipe == null) {
            return RecipeRoutingContext.disabledWithCatalyst(getCurrentCatalyst(core));
        }

        Object fusionIngredientsObject = invokeNoArg(recipe, "fusionIngredients");
        if (!(fusionIngredientsObject instanceof List<?> fusionIngredients)) {
            return RecipeRoutingContext.disabledWithCatalyst(getCurrentCatalyst(core));
        }

        List<Ingredient> remainingIngredients = new ArrayList<>();
        for (Object ingredientEntry : fusionIngredients) {
            Object ingredientObject = invokeNoArg(ingredientEntry, "get");
            if (ingredientObject instanceof Ingredient ingredient) {
                remainingIngredients.add(ingredient);
            }
        }

        boolean hasIncompatiblePrefilledInjectors = false;
        for (Object injector : orderedInjectors) {
            Object injectorStack = invokeNoArg(injector, "getInjectorStack");
            if (injectorStack instanceof ItemStack stack && !stack.isEmpty()) {
                if (!consumeFirstMatchingIngredient(remainingIngredients, stack)) {
                    hasIncompatiblePrefilledInjectors = true;
                }
            }
        }

        CatalystInfo info = resolveCatalystInfo(recipe);
        Ingredient catalystIngredient = info != null ? info.ingredient() : null;
        if (catalystIngredient != null) {
            LOGGER.debug("Resolved catalyst ingredient for active recipe on core {}.", resolveCorePos(core));
        }

        return RecipeRoutingContext.enabled(remainingIngredients, catalystIngredient, getCurrentCatalyst(core),
                hasIncompatiblePrefilledInjectors);
    }

    private CatalystInfo resolveCatalystInfo(Object recipe) {
        if (recipe == null) {
            return null;
        }

        // B004: try all known method names across DE versions
        String[] methodCandidates = new String[] {
                "getCatalyst", "catalyst", "getCatalystIngredient", "catalystIngredient",
                "getCatalystItem", "catalystItem", "getTier", "getRequiredCore"
        };
        for (String methodName : methodCandidates) {
            Object ingredientObj = invokeNoArg(recipe, methodName);
            Ingredient resolved = resolveIngredient(ingredientObj);
            if (resolved != null && !resolved.isEmpty()) {
                int count = resolveIngredientCount(ingredientObj);
                LOGGER.debug("Catalyst resolved via method '{}' on {} (count={})", methodName,
                        recipe.getClass().getSimpleName(), count);
                return new CatalystInfo(resolved, count);
            }
        }

        return null;
    }

    private int resolveIngredientCount(Object ingredientObj) {
        if (ingredientObj == null)
            return 1;

        // DE StackIngredient logic: extract count via reflection
        Object custom = invokeNoArg(ingredientObj, "getCustomIngredient");
        if (custom != null) {
            Object count = invokeNoArg(custom, "getCount");
            if (count instanceof Integer c)
                return c;
        }

        return 1;
    }

    private Ingredient resolveIngredient(Object candidate) {
        if (candidate == null) {
            return null;
        }
        if (candidate instanceof Ingredient ingredient) {
            return ingredient;
        }

        Object unwrapped = invokeNoArg(candidate, "get");
        if (unwrapped instanceof Ingredient ingredient) {
            return ingredient;
        }

        unwrapped = invokeNoArg(candidate, "ingredient");
        if (unwrapped instanceof Ingredient ingredient) {
            return ingredient;
        }

        unwrapped = invokeNoArg(candidate, "value");
        if (unwrapped instanceof Ingredient ingredient) {
            return ingredient;
        }

        return null;
    }

    private BlockPos resolveCorePos(Object core) {
        Object blockPos = invokeNoArg(core, "getBlockPos");
        return blockPos instanceof BlockPos pos ? pos : BlockPos.ZERO;
    }

    private ItemStack getCurrentCatalyst(Object core) {
        Object catalyst = invokeNoArg(core, "getCatalystStack");
        return catalyst instanceof ItemStack itemStack ? itemStack.copy() : ItemStack.EMPTY;
    }

    private boolean consumeFirstMatchingIngredient(List<Ingredient> remainingIngredients, ItemStack stack) {
        int index = selectBestMatchingIngredientIndex(remainingIngredients, stack);
        if (index >= 0) {
            remainingIngredients.remove(index);
            return true;
        }
        return false;
    }

    private int selectBestMatchingIngredientIndex(List<Ingredient> ingredients, ItemStack stack) {
        int bestIndex = -1;
        int bestScore = Integer.MAX_VALUE;

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (!ingredient.test(stack)) {
                continue;
            }

            int score = ingredient.getItems().length;
            if (score < bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    private Object invokeNoArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }

        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private boolean invokeSetStack(Object target, ItemStack stack) {
        if (target == null) {
            return false;
        }

        try {
            target.getClass().getMethod("setInjectorStack", ItemStack.class).invoke(target, stack);
            return true;
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("[AE2DraconicFusion] CRITICAL: Failed to call setInjectorStack on {} via reflection: {}",
                    target, exception.getMessage());
            return false;
        }
    }

    private boolean invokeSetCatalyst(Object core, ItemStack stack) {
        if (core == null) {
            return false;
        }

        try {
            core.getClass().getMethod("setCatalystStack", ItemStack.class).invoke(core, stack);
            return true;
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("[AE2DraconicFusion] CRITICAL: Failed to call setCatalystStack on {} via reflection: {}", core,
                    exception.getMessage());
            return false;
        }
    }

    private void rollbackInjectors(List<Object> injectors, List<ItemStack> originalStacks) {
        for (int index = 0; index < injectors.size() && index < originalStacks.size(); index++) {
            invokeSetStack(injectors.get(index), originalStacks.get(index));
        }
    }

    private static final class RecipeRoutingContext {
        private final boolean recipeAware;
        private final List<Ingredient> remainingIngredients;
        private final List<Ingredient> snapshotRemainingIngredients;
        private final Ingredient catalystIngredient;
        private final ItemStack currentCatalyst;
        private final boolean hasIncompatiblePrefilledInjectors;

        private RecipeRoutingContext(boolean recipeAware, List<Ingredient> remainingIngredients,
                Ingredient catalystIngredient, ItemStack currentCatalyst, boolean hasIncompatiblePrefilledInjectors) {
            this.recipeAware = recipeAware;
            this.remainingIngredients = new ArrayList<>(remainingIngredients);
            this.snapshotRemainingIngredients = new ArrayList<>(remainingIngredients);
            this.catalystIngredient = catalystIngredient;
            this.currentCatalyst = currentCatalyst == null ? ItemStack.EMPTY : currentCatalyst.copy();
            this.hasIncompatiblePrefilledInjectors = hasIncompatiblePrefilledInjectors;
        }

        static RecipeRoutingContext disabled() {
            return new RecipeRoutingContext(false, List.of(), null, ItemStack.EMPTY, false);
        }

        static RecipeRoutingContext disabledWithCatalyst(ItemStack currentCatalyst) {
            return new RecipeRoutingContext(false, List.of(), null, currentCatalyst, false);
        }

        /**
         * B004: recipe-unaware context but WITH a known catalyst ingredient.
         * Injector routing remains permissive (recipeAware=false) while catalyst
         * routing is active.
         */
        static RecipeRoutingContext disabledWithKnownCatalyst(Ingredient knownCatalyst, ItemStack currentCatalyst) {
            return new RecipeRoutingContext(false, List.of(), knownCatalyst, currentCatalyst, false);
        }

        static RecipeRoutingContext enabled(List<Ingredient> remainingIngredients, Ingredient catalystIngredient,
                ItemStack currentCatalyst, boolean hasIncompatiblePrefilledInjectors) {
            return new RecipeRoutingContext(true, remainingIngredients, catalystIngredient, currentCatalyst,
                    hasIncompatiblePrefilledInjectors);
        }

        boolean recipeAware() {
            return recipeAware;
        }

        Ingredient catalystIngredient() {
            return catalystIngredient;
        }

        ItemStack currentCatalyst() {
            return currentCatalyst;
        }

        boolean hasIncompatiblePrefilledInjectors() {
            return hasIncompatiblePrefilledInjectors;
        }

        boolean consumeMatchingRemaining(ItemStack stack) {
            if (!recipeAware) {
                return true;
            }

            int bestIndex = -1;
            int bestScore = Integer.MAX_VALUE;

            for (int i = 0; i < remainingIngredients.size(); i++) {
                Ingredient ingredient = remainingIngredients.get(i);
                if (!ingredient.test(stack)) {
                    continue;
                }

                int score = ingredient.getItems().length;
                if (score < bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }

            if (bestIndex >= 0) {
                remainingIngredients.remove(bestIndex);
                return true;
            }
            return false;
        }

        void restoreRemainingState() {
            if (!recipeAware) {
                return;
            }

            remainingIngredients.clear();
            remainingIngredients.addAll(snapshotRemainingIngredients);
        }
    }
}
