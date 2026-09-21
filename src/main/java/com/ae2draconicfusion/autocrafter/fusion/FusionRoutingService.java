package com.ae2draconicfusion.autocrafter.fusion;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.StackIngredient;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Core routing engine that distributes items from AE2 patterns into
 * Draconic Evolution Fusion Crafting structures.
 * Handles catalyst placement, injector assignment, recipe matching,
 * deterministic ordering, simulation, and rollback.
 */
public final class FusionRoutingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FusionRoutingService.class);

    public record CatalystInfo(Ingredient ingredient, int count) {}

    private final FusionStructureScanner structureScanner;

    public FusionRoutingService(FusionStructureScanner structureScanner) {
        this.structureScanner = structureScanner;
    }

    public FusionRoutingResult routeItemStackToFusionStructure(ServerLevel level, BlockPos corePos,
            ItemStack sourceStack, int scanRange, boolean simulate, @Nullable CatalystInfo catalystInfo) {
        
        if (sourceStack == null || sourceStack.isEmpty()) {
            return FusionRoutingResult.TEMPORARY_FAILURE;
        }

        FusionStructureScanner.FusionStructureSnapshot snapshot = structureScanner.scan(level, corePos, scanRange);
        if (snapshot.coreBusy()) {
            return FusionRoutingResult.TEMPORARY_FAILURE;
        }
        if (!snapshot.validCore() || snapshot.injectors().isEmpty()) {
            return snapshot.validCore() ? FusionRoutingResult.NO_INJECTORS_FOUND : FusionRoutingResult.INVALID_CORE;
        }

        List<IFusionInjector> orderedInjectors = orderInjectorsDeterministically(snapshot.corePos(), snapshot.injectors());

        ItemStack remainingStack = sourceStack.copy();

        // If we have catalyst info and it matches the current item, process it for the core first
        if (catalystInfo != null && catalystInfo.ingredient().test(sourceStack)) {
            ItemStack currentCatalyst = snapshot.core().getCatalystStack();
            if (!currentCatalyst.isEmpty()) {
                if (catalystInfo.ingredient().test(currentCatalyst)) {
                    // Correct catalyst already present
                    return FusionRoutingResult.SUCCESS;
                }
                // Different item in core slot
                return FusionRoutingResult.TARGET_SLOTS_OCCUPIED;
            }

            int toTake = Math.min(sourceStack.getCount(), catalystInfo.count());
            ItemStack catalystStack = sourceStack.copy();
            catalystStack.setCount(toTake);
            
            if (simulate) {
                remainingStack.shrink(toTake);
                if (remainingStack.isEmpty()) {
                    return FusionRoutingResult.SUCCESS;
                }
            } else {
                snapshot.core().setCatalystStack(catalystStack);
                remainingStack.shrink(toTake);
                if (remainingStack.isEmpty()) {
                    return FusionRoutingResult.SUCCESS;
                }
            }
        }

        List<ItemStack> originalStacks = new ArrayList<>(orderedInjectors.size());
        for (IFusionInjector injector : orderedInjectors) {
            originalStacks.add(injector.getInjectorStack().copy());
        }

        boolean placedSomething = false;

        for (IFusionInjector injector : orderedInjectors) {
            if (remainingStack.isEmpty()) {
                break;
            }

            ItemStack currentStack = injector.getInjectorStack();
            if (!currentStack.isEmpty()) {
                continue;
            }

            ItemStack singleItem = remainingStack.copy();
            singleItem.setCount(1);

            if (simulate) {
                remainingStack.shrink(1);
                placedSomething = true;
            } else {
                injector.setInjectorStack(singleItem);
                remainingStack.shrink(1);
                placedSomething = true;
            }
        }

        if (!placedSomething) {
            return FusionRoutingResult.TEMPORARY_FAILURE;
        }

        if (!remainingStack.isEmpty()) {
            if (!simulate) {
                rollbackInjectors(orderedInjectors, originalStacks);
            }
            return FusionRoutingResult.INJECTORS_INSUFFICIENT;
        }

        return FusionRoutingResult.SUCCESS;
    }

    /**
     * Resolves the catalyst ingredient by matching both the pattern inputs and outputs
     * against registered Fusion recipes.
     */
    @Nullable
    public CatalystInfo resolveCatalystForPattern(ServerLevel level, List<ItemStack> patternInputs, ItemStack primaryOutput) {
        if (level == null || primaryOutput == null || primaryOutput.isEmpty() || patternInputs.isEmpty()) return null;

        try {
            var recipeManager = level.getRecipeManager();
            List<RecipeHolder<IFusionRecipe>> allRecipes = recipeManager.getAllRecipesFor(DraconicAPI.FUSION_RECIPE_TYPE.get());
            
            List<IFusionRecipe> candidates = new ArrayList<>();
            for (RecipeHolder<IFusionRecipe> holder : allRecipes) {
                IFusionRecipe recipe = holder.value();
                ItemStack recipeOutput = recipe.getResultItem(level.registryAccess());
                
                // Match output first
                if (recipeOutput.isEmpty() || !ItemStack.isSameItem(recipeOutput, primaryOutput)) {
                    continue;
                }
                
                // Check if all inputs from recipe are available in pattern inputs
                if (isRecipeCompatibleWithInputs(recipe, patternInputs)) {
                    candidates.add(recipe);
                }
            }
            
            if (candidates.isEmpty()) {
                LOGGER.debug("No fusion recipe found matching pattern inputs and output {}", primaryOutput);
                return null;
            }
            
            IFusionRecipe bestMatch = candidates.get(0);
            if (candidates.size() > 1) {
                // If multiple candidates, try to match exact components
                for (IFusionRecipe candidate : candidates) {
                    ItemStack candidateOutput = candidate.getResultItem(level.registryAccess());
                    if (ItemStack.isSameItemSameComponents(candidateOutput, primaryOutput)) {
                        bestMatch = candidate;
                        break;
                    }
                }
            }
            
            Ingredient catalyst = bestMatch.getCatalyst();
            int count = 1;
            if (catalyst.getCustomIngredient() instanceof StackIngredient stackIngredient) {
                count = stackIngredient.getCount();
            }
            return new CatalystInfo(catalyst, count);
            
        } catch (Exception e) {
            LOGGER.error("Error resolving catalyst for pattern: {}", e.getMessage(), e);
            return null;
        }
    }
    
    private boolean isRecipeCompatibleWithInputs(IFusionRecipe recipe, List<ItemStack> patternInputs) {
        List<ItemStack> availableInputs = new ArrayList<>();
        for (ItemStack input : patternInputs) {
            availableInputs.add(input.copy());
        }
        
        // 1. Check Catalyst
        Ingredient catalyst = recipe.getCatalyst();
        int catCount = 1;
        if (catalyst.getCustomIngredient() instanceof StackIngredient si) {
            catCount = si.getCount();
        }
        
        boolean foundCatalyst = false;
        for (ItemStack input : availableInputs) {
            if (catalyst.test(input) && input.getCount() >= catCount) {
                input.shrink(catCount);
                foundCatalyst = true;
                break;
            }
        }
        if (!foundCatalyst) return false;
        
        // 2. Check remaining ingredients
        for (Ingredient ing : recipe.getIngredients()) {
            boolean foundIng = false;
            for (ItemStack input : availableInputs) {
                if (input.isEmpty()) continue;
                if (ing.test(input)) {
                    input.shrink(1);
                    foundIng = true;
                    break;
                }
            }
            if (!foundIng) return false;
        }
        
        return true;
    }
    
    private List<IFusionInjector> orderInjectorsDeterministically(BlockPos corePos, List<IFusionInjector> injectors) {
        List<IFusionInjector> orderedInjectors = new ArrayList<>(injectors);
        orderedInjectors.sort(Comparator
                .comparingLong((IFusionInjector injector) -> squaredDistance(corePos, ((net.minecraft.world.level.block.entity.BlockEntity)injector).getBlockPos()))
                .thenComparingInt(injector -> directionRank(corePos, ((net.minecraft.world.level.block.entity.BlockEntity)injector).getBlockPos()))
                .thenComparingInt(injector -> ((net.minecraft.world.level.block.entity.BlockEntity)injector).getBlockPos().getX())
                .thenComparingInt(injector -> ((net.minecraft.world.level.block.entity.BlockEntity)injector).getBlockPos().getY())
                .thenComparingInt(injector -> ((net.minecraft.world.level.block.entity.BlockEntity)injector).getBlockPos().getZ()));
        return orderedInjectors;
    }

    public boolean isCoreCrafting(TileFusionCraftingCore core) {
        return core != null && core.isCrafting();
    }

    public void triggerStartCraft(TileFusionCraftingCore core) {
        if (core != null) {
            core.startCraft();
            LOGGER.debug("Crafting started automatically on core.");
        }
    }

    public FusionStructureScanner getStructureScanner() {
        return structureScanner;
    }

    public void rollbackDelivery(ServerLevel level, BlockPos corePos, List<ItemStack> placedStacks,
            int scanRange, @Nullable CatalystInfo catalystInfo) {
        
        if (placedStacks.isEmpty() && catalystInfo == null) {
            return;
        }

        FusionStructureScanner.FusionStructureSnapshot snapshot = structureScanner.scan(level, corePos, scanRange);
        if (!snapshot.validCore() || snapshot.core() == null) {
            return;
        }

        if (catalystInfo != null) {
            ItemStack currentCatalyst = snapshot.core().getCatalystStack();
            if (!currentCatalyst.isEmpty() && catalystInfo.ingredient().test(currentCatalyst)) {
                snapshot.core().setCatalystStack(ItemStack.EMPTY);
            }
        }

        if (placedStacks.isEmpty()) {
            return;
        }

        List<IFusionInjector> orderedInjectors = orderInjectorsDeterministically(snapshot.corePos(), snapshot.injectors());
        for (ItemStack placed : placedStacks) {
            for (IFusionInjector injector : orderedInjectors) {
                ItemStack currentStack = injector.getInjectorStack();
                if (!currentStack.isEmpty() && !placed.isEmpty() && ItemStack.isSameItem(currentStack, placed)) {
                    injector.setInjectorStack(ItemStack.EMPTY);
                    break;
                }
            }
        }
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

    private void rollbackInjectors(List<IFusionInjector> injectors, List<ItemStack> originalStacks) {
        for (int index = 0; index < injectors.size() && index < originalStacks.size(); index++) {
            injectors.get(index).setInjectorStack(originalStacks.get(index));
        }
    }
}
