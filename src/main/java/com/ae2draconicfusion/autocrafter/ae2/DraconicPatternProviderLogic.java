package com.ae2draconicfusion.autocrafter.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.ae2draconicfusion.autocrafter.fusion.FusionRoutingResult;
import com.ae2draconicfusion.autocrafter.fusion.FusionRoutingService;
import com.ae2draconicfusion.autocrafter.fusion.FusionStructureScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DraconicPatternProviderLogic extends PatternProviderLogic {
    private static final int DEFAULT_SCAN_RANGE = 8;
    private static final Logger LOGGER = LoggerFactory.getLogger(DraconicPatternProviderLogic.class);

    private final PatternProviderLogicHost host;
    private final FusionRoutingService routingService = new FusionRoutingService(new FusionStructureScanner());

    public DraconicPatternProviderLogic(appeng.api.networking.IManagedGridNode mainNode, PatternProviderLogicHost host) {
        super(mainNode, host);
        this.host = host;
    }

    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] inputs) {
        var hostBlockEntity = host.getBlockEntity();
        if (!(hostBlockEntity.getLevel() instanceof ServerLevel serverLevel)) {
            return false;
        }

        BlockPos providerPos = hostBlockEntity.getBlockPos();
        BlockPos corePos = FusionBusAccess.resolveFusionCoreInRange(serverLevel, providerPos, DEFAULT_SCAN_RANGE).orElse(null);
        if (corePos == null) {
            LOGGER.debug("pushPattern aborted at {}: no fusion core within range {}.", providerPos, DEFAULT_SCAN_RANGE);
            return false;
        }

        LOGGER.debug("pushPattern invoked at {} for pattern {}.", providerPos, pattern.getDefinition());

        // B004: pre-resolve catalyst once from the recipe registry BEFORE the item loop.
        // This breaks the chicken-and-egg dependency where getActiveRecipe() is null until items are placed.
        @Nullable Ingredient preResolvedCatalyst = resolvePatternCatalyst(serverLevel, pattern);
        LOGGER.debug("Pre-resolved catalyst for pattern {}: {}", pattern.getDefinition(),
            preResolvedCatalyst != null ? "found" : "not found");

        boolean routedAny = false;
        // B002: track temporary failures separately so we don't abort the whole push on the first TEMPORARY_FAILURE
        boolean hadTemporaryFailure = false;

        if (inputs != null) {
            for (KeyCounter counter : inputs) {
                if (counter == null || counter.isEmpty()) {
                    continue;
                }

                for (var entry : counter) {
                    AEKey key = entry.getKey();
                    long amount = entry.getLongValue();
                    if (!(key instanceof AEItemKey itemKey) || amount <= 0) {
                        continue;
                    }

                    ItemStack stack = itemKey.toStack((int) Math.min(amount, itemKey.getMaxStackSize()));
                    FusionRoutingResult result = routingService.routeItemStackToFusionStructure(
                        serverLevel, corePos, stack, DEFAULT_SCAN_RANGE, false, preResolvedCatalyst);
                    LOGGER.debug("Routed {} x{} -> {}", stack.getItem(), stack.getCount(), result);

                    if (result == FusionRoutingResult.SUCCESS) {
                        routedAny = true;
                    } else if (result == FusionRoutingResult.TEMPORARY_FAILURE
                            || result == FusionRoutingResult.TARGET_SLOTS_OCCUPIED) {
                        // B002/second-push fix: temporary unavailability (core busy, slots momentarily full
                        // post-craft, or catalyst already placed) — continue with remaining items and retry later.
                        hadTemporaryFailure = true;
                    } else {
                        // Structural failure: INVALID_CORE, NO_INJECTORS_FOUND, INJECTORS_INSUFFICIENT
                        LOGGER.debug("pushPattern: structural failure {} for {} at {}; aborting push.",
                            result, stack.getItem(), providerPos);
                        return false;
                    }
                }
            }
        }

        // Return true only if at least one item was routed and no temporary failures remain unresolved.
        // If hadTemporaryFailure && !routedAny: return false so AE2 reschedules the push.
        // If hadTemporaryFailure && routedAny: return true (partial success; AE2 will push remaining next tick).
        return routedAny;
    }

    /**
     * B004: Resolves the catalyst ingredient for this pattern by looking up the fusion crafting recipe
     * that matches the pattern's expected output. Called once per pushPattern invocation.
     */
    @Nullable
    private Ingredient resolvePatternCatalyst(ServerLevel level, IPatternDetails pattern) {
        try {
            GenericStack primaryOutput = pattern.getPrimaryOutput();
            if (primaryOutput == null) return null;
            AEKey outputKey = primaryOutput.what();
            if (!(outputKey instanceof AEItemKey itemKey)) return null;
            ItemStack outputStack = itemKey.toStack(1);
            return routingService.resolveCatalystForOutput(level, outputStack);
        } catch (Exception e) {
            LOGGER.debug("Failed to resolve pattern catalyst: {}", e.getMessage());
            return null;
        }
    }
}