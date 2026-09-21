package com.ae2draconicfusion.autocrafter.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
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

/**
 * Custom crafting logic for the ME Draconic Pattern Provider.
 * Extends AE2's PatternProviderLogic to route pattern items into
 * Draconic Evolution Fusion Crafting structures (Core + Injectors)
 * using a two-phase approach: simulation first, then execution.
 */
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

        // 2. Prepare the list of items to route
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

        // 3. Pre-resolve catalyst info (ingredient + count) using actual inputs and expected primary output
        ItemStack primaryOutput = ItemStack.EMPTY;
        GenericStack aePrimaryOutput = pattern.getPrimaryOutput();
        if (aePrimaryOutput != null && aePrimaryOutput.what() instanceof AEItemKey itemKey) {
            primaryOutput = itemKey.toStack(1); // Usually we only need the item definition for matching
        }
        @Nullable FusionRoutingService.CatalystInfo catalystInfo = resolvePatternCatalyst(serverLevel, stacksToRoute, primaryOutput);

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

        // 5. EXECUTION: Actually place the items.
        // We track how many have been successfully placed so we can roll back cleanly
        // if one fails, returning false so AE2 keeps the items in the network.
        for (int i = 0; i < stacksToRoute.size(); i++) {
            ItemStack stack = stacksToRoute.get(i);
            FusionRoutingResult realResult = routingService.routeItemStackToFusionStructure(
                serverLevel, corePos, stack, DEFAULT_SCAN_RANGE, false, catalystInfo);
            if (realResult != FusionRoutingResult.SUCCESS) {
                LOGGER.error(
                    "Atomic delivery failed at execution phase on item {} of {}! Rolling back {} already-placed items.",
                    i + 1, stacksToRoute.size(), i);
                // Roll back all items placed in this session so nothing is voided
                routingService.rollbackDelivery(serverLevel, corePos, stacksToRoute.subList(0, i),
                    DEFAULT_SCAN_RANGE, catalystInfo);
                return false;
            }
            LOGGER.debug("Atomic route: {} -> SUCCESS ({}/{})", stack.getItem(), i + 1, stacksToRoute.size());
        }

        // 6. AUTO-START: All items are in, trigger the craft!
        routingService.triggerStartCraft(snapshot.core());

        return true;
    }

    @Nullable
    private FusionRoutingService.CatalystInfo resolvePatternCatalyst(ServerLevel level, List<ItemStack> patternInputs, ItemStack primaryOutput) {
        try {
            return routingService.resolveCatalystForPattern(level, patternInputs, primaryOutput);
        } catch (Exception e) {
            LOGGER.debug("Failed to resolve pattern catalyst: {}", e.getMessage());
            return null;
        }
    }
}