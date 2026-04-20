package com.ae2draconicfusion.autocrafter.fusion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class FusionStructureScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FusionStructureScanner.class);
    private static final String FUSION_CORE_CLASS = "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore";

    public FusionStructureSnapshot scan(ServerLevel level, BlockPos corePos, int range) {
        if (level == null || corePos == null || range < 0) {
            LOGGER.warn("Scanner failed null check: level={}, corePos={}, range={}", level, corePos, range);
            return FusionStructureSnapshot.invalid(corePos);
        }

        BlockEntity blockEntity = level.getBlockEntity(corePos);
        LOGGER.debug("BlockEntity at {}: {} (IsFusionCore={})",
                corePos, blockEntity == null ? "null" : blockEntity.getClass().getSimpleName(),
                blockEntity != null && FUSION_CORE_CLASS.equals(blockEntity.getClass().getName()));

        if (blockEntity == null || !FUSION_CORE_CLASS.equals(blockEntity.getClass().getName())) {
            LOGGER.debug("Scanner rejected core at {}: not a TileFusionCraftingCore (actual: {})", corePos,
                blockEntity == null ? "null" : blockEntity.getClass().getName());
            return FusionStructureSnapshot.invalid(corePos);
        }

        invokeNoArg(blockEntity, "updateInjectors");

        Object fusionState = invokeNoArg(blockEntity, "getFusionState");
        // Diagnostic INFO log: always visible so we can see the state after craft completion
        LOGGER.info("[AE2DraconicFusion] Core at {} fusionState={} class={}",
            corePos, fusionState, fusionState == null ? "null" : fusionState.getClass().getSimpleName());

        if (isBusyFusionState(fusionState)) {
            // B001: core is busy (CRAFTING/CHARGING) but physically valid — use busy() which keeps validCore=true
            LOGGER.debug("Scanner: core at {} is temporarily busy: {}", corePos, fusionState);
            return FusionStructureSnapshot.busy(corePos, blockEntity, fusionState);
        }

        Object injectorsObject = invokeNoArg(blockEntity, "getInjectors");
        if (!(injectorsObject instanceof List<?>)) {
            LOGGER.debug("Scanner rejected core at {}: getInjectors() did not return a List", corePos);
            return FusionStructureSnapshot.invalid(corePos);
        }

        List<?> rawInjectors = (List<?>) injectorsObject;
        if (rawInjectors.isEmpty()) {
            LOGGER.debug("Scanner found core at {} with no injectors", corePos);
            return FusionStructureSnapshot.noInjectors(corePos, blockEntity, fusionState);
        }

        boolean allInjectorsValid = true;
        int validCount = 0;
        for (Object injector : rawInjectors) {
            if (injector == null || !invokeValidate(injector)) {
                allInjectorsValid = false;
                LOGGER.debug("Injector {} is invalid (null={}, validate={})",
                    injector == null ? "N/A" : injector.getClass().getSimpleName(),
                    injector == null, injector != null && !invokeValidate(injector));
            } else {
                validCount++;
            }
        }

        if (!allInjectorsValid) {
            LOGGER.debug("Scanner rejected core at {}: {} valid injectors out of {}", corePos, validCount, rawInjectors.size());
            return FusionStructureSnapshot.invalidInjectors(corePos, blockEntity, rawInjectors, fusionState);
        }

        LOGGER.debug("Scanner accepted core at {} with {} valid injectors", corePos, rawInjectors.size());
        return FusionStructureSnapshot.valid(corePos, blockEntity, new ArrayList<>(rawInjectors), fusionState);
    }

    private boolean isBusyFusionState(Object fusionState) {
        if (fusionState == null) {
            // If getFusionState() returned null (reflection failed or unknown), assume NOT busy.
            return false;
        }

        String stateName = fusionState.toString().toUpperCase();

        // Allowlist approach: only states we KNOW are idle/ready get a clear path.
        // Everything else (CRAFTING, CHARGING, DONE, COMPLETE, WAITING, SETUP, etc.) is treated as busy
        // so items are never placed during a transient post-craft state.
        return !stateName.equals("IDLE") && !stateName.equals("INACTIVE") && !stateName.equals("START");
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

    private boolean invokeValidate(Object injector) {
        if (injector == null) {
            return false;
        }

        try {
            Object result = injector.getClass().getMethod("validate").invoke(injector);
            return result instanceof Boolean && (Boolean) result;
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    /**
     * @param validCore  true if a TileFusionCraftingCore is found at corePos with valid injectors.
     * @param coreBusy   true if the core is CRAFTING or CHARGING (temporarily unavailable, but structurally valid).
     *                   When coreBusy is true, validCore is also true.
     */
    public record FusionStructureSnapshot(boolean validCore, boolean coreBusy, BlockPos corePos, Object core, List<?> injectors, Object fusionState) {
        public static FusionStructureSnapshot invalid(BlockPos corePos) {
            return new FusionStructureSnapshot(false, false, corePos, null, List.of(), null);
        }

        // B001 fix: busy core is structurally valid — validCore=true so callers can distinguish it from a missing core.
        public static FusionStructureSnapshot busy(BlockPos corePos, Object core, Object fusionState) {
            return new FusionStructureSnapshot(true, true, corePos, core, List.of(), fusionState);
        }

        public static FusionStructureSnapshot noInjectors(BlockPos corePos, Object core, Object fusionState) {
            return new FusionStructureSnapshot(true, false, corePos, core, List.of(), fusionState);
        }

        public static FusionStructureSnapshot invalidInjectors(BlockPos corePos, Object core, List<?> injectors, Object fusionState) {
            return new FusionStructureSnapshot(true, false, corePos, core, List.copyOf(injectors), fusionState);
        }

        public static FusionStructureSnapshot valid(BlockPos corePos, Object core, List<?> injectors, Object fusionState) {
            return new FusionStructureSnapshot(true, false, corePos, core, List.copyOf(injectors), fusionState);
        }
    }
}
