package com.ae2draconicfusion.autocrafter.fusion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;

/**
 * Scans the world around a Fusion Crafting Core to build a snapshot
 * of the structure (core state, connected injectors, validity).
 */
public final class FusionStructureScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FusionStructureScanner.class);

    public FusionStructureSnapshot scan(ServerLevel level, BlockPos corePos, int range) {
        if (level == null || corePos == null || range < 0) {
            LOGGER.warn("Scanner failed null check: level={}, corePos={}, range={}", level, corePos, range);
            return FusionStructureSnapshot.invalid(corePos);
        }

        BlockEntity blockEntity = level.getBlockEntity(corePos);
        LOGGER.debug("BlockEntity at {}: {} (IsFusionCore={})",
                corePos, blockEntity == null ? "null" : blockEntity.getClass().getSimpleName(),
                blockEntity instanceof TileFusionCraftingCore);

        if (!(blockEntity instanceof TileFusionCraftingCore core)) {
            LOGGER.debug("Scanner rejected core at {}: not a TileFusionCraftingCore (actual: {})", corePos,
                blockEntity == null ? "null" : blockEntity.getClass().getName());
            return FusionStructureSnapshot.invalid(corePos);
        }

        core.updateInjectors();

        boolean crafting = core.isCrafting();

        Object fusionState = core.getFusionState();
        LOGGER.debug("Core at {} is in state: {} (isCrafting={})", corePos, fusionState, crafting);

        if (crafting) {
            // Core is actively crafting/charging — structurally valid but temporarily busy
            return FusionStructureSnapshot.busy(corePos, core, fusionState);
        }

        List<IFusionInjector> rawInjectors = core.getInjectors();
        if (rawInjectors == null || rawInjectors.isEmpty()) {
            LOGGER.debug("Scanner found core at {} with no injectors", corePos);
            return FusionStructureSnapshot.noInjectors(corePos, core, fusionState);
        }

        boolean allInjectorsValid = true;
        int validCount = 0;
        for (IFusionInjector injector : rawInjectors) {
            if (injector == null || !injector.validate()) {
                allInjectorsValid = false;
                LOGGER.debug("Injector {} is invalid",
                    injector == null ? "N/A" : injector.getClass().getSimpleName());
            } else {
                validCount++;
            }
        }

        if (!allInjectorsValid) {
            LOGGER.debug("Scanner rejected core at {}: {} valid injectors out of {}", corePos, validCount, rawInjectors.size());
            return FusionStructureSnapshot.invalidInjectors(corePos, core, rawInjectors, fusionState);
        }

        LOGGER.debug("Scanner accepted core at {} with {} valid injectors", corePos, rawInjectors.size());
        return FusionStructureSnapshot.valid(corePos, core, new ArrayList<>(rawInjectors), fusionState);
    }

    /**
     * @param validCore  true if a TileFusionCraftingCore is found at corePos with valid injectors.
     * @param coreBusy   true if the core is CRAFTING or CHARGING (temporarily unavailable, but structurally valid).
     *                   When coreBusy is true, validCore is also true.
     */
    public record FusionStructureSnapshot(boolean validCore, boolean coreBusy, BlockPos corePos, TileFusionCraftingCore core, List<IFusionInjector> injectors, Object fusionState) {
        public static FusionStructureSnapshot invalid(BlockPos corePos) {
            return new FusionStructureSnapshot(false, false, corePos, null, List.of(), null);
        }

        // Busy core is structurally valid — validCore=true so callers
        // can distinguish it from a missing core.
        public static FusionStructureSnapshot busy(BlockPos corePos, TileFusionCraftingCore core, Object fusionState) {
            return new FusionStructureSnapshot(true, true, corePos, core, List.of(), fusionState);
        }

        public static FusionStructureSnapshot noInjectors(BlockPos corePos, TileFusionCraftingCore core, Object fusionState) {
            return new FusionStructureSnapshot(true, false, corePos, core, List.of(), fusionState);
        }

        public static FusionStructureSnapshot invalidInjectors(BlockPos corePos, TileFusionCraftingCore core, List<IFusionInjector> injectors, Object fusionState) {
            return new FusionStructureSnapshot(true, false, corePos, core, List.copyOf(injectors), fusionState);
        }

        public static FusionStructureSnapshot valid(BlockPos corePos, TileFusionCraftingCore core, List<IFusionInjector> injectors, Object fusionState) {
            return new FusionStructureSnapshot(true, false, corePos, core, List.copyOf(injectors), fusionState);
        }
    }
}
