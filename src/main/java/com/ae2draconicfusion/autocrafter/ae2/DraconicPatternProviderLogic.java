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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
            return false;
        }

        // 1. Scan the structure. If it's busy, abort the whole push immediately.
        // This prevents items from being stuck half-way in.
        var snapshot = routingService.getStructureScanner().scan(serverLevel, corePos, DEFAULT_SCAN_RANGE);
        if (!snapshot.validCore() || routingService.isCoreCrafting(snapshot.core())) {
            return false;
        }

        LOGGER.debug("pushPattern starting atomic delivery at {} for pattern {}.", providerPos, pattern.getDefinition());

        // 2. Pre-resolve catalyst info (ingredient + count)
        @Nullable FusionRoutingService.CatalystInfo catalystInfo = resolvePatternCatalyst(serverLevel, pattern);

        // 3. Prepare the list of items to route
        List<ItemStack> stacksToRoute = new ArrayList<>();
        if (inputs != null) {
            for (KeyCounter counter : inputs) {
                for (var entry : counter) {
                    if (entry.getKey() instanceof AEItemKey itemKey && entry.getLongValue() > 0) {
                        stacksToRoute.add(itemKey.toStack((int) entry.getLongValue()));
                    }
                }
            }
        }

        if (stacksToRoute.isEmpty()) {
            return false;
        }

        // 4. SIMULATION: Verify that ALL items would fit
        // We use a simulation pass to ensure we don't start putting items if we can't finish.
        for (ItemStack stack : stacksToRoute) {
            FusionRoutingResult simResult = routingService.routeItemStackToFusionStructure(
                serverLevel, corePos, stack, DEFAULT_SCAN_RANGE, true, catalystInfo);
            if (simResult != FusionRoutingResult.SUCCESS) {
                LOGGER.debug("pushPattern atomic delivery simulated failure: {} for item {}.", simResult, stack);
                return false;
            }
        }

        // 5. MODULATION: Actually place the items
        for (ItemStack stack : stacksToRoute) {
            FusionRoutingResult realResult = routingService.routeItemStackToFusionStructure(
                serverLevel, corePos, stack, DEFAULT_SCAN_RANGE, false, catalystInfo);
            if (realResult != FusionRoutingResult.SUCCESS) {
                // This should not happen if simulation passed, unless the world changed in the same tick.
                LOGGER.error("[AE2DraconicFusion] Atomic delivery failed at modulation phase! State might be inconsistent.");
                return true; // Return true because some items WERE consumed
            }
            LOGGER.info("[AE2DraconicFusion] Atomic Route: {} -> SUCCESS", stack.getItem());
        }


        // 6. AUTO-START: All items are in, trigger the craft!
        routingService.triggerStartCraft(snapshot.core());

        return true;
    }

    /**
     * B004: Resolves the catalyst ingredient for this pattern by looking up the fusion crafting recipe
     * that matches the pattern's expected output. Called once per pushPattern invocation.
     */
    @Nullable
    private FusionRoutingService.CatalystInfo resolvePatternCatalyst(ServerLevel level, IPatternDetails pattern) {
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