package com.ae2draconicfusion.autocrafter.ae2;

import com.ae2draconicfusion.autocrafter.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import appeng.api.upgrades.IUpgradeableObject;

import java.util.Optional;

/**
 * Utility class for detecting Fusion Routing Cards on AE2 buses
 * and locating Draconic Evolution Fusion Crafting Cores in the world.
 * Uses class name comparison to avoid a compile-time dependency on DE.
 */
public final class FusionBusAccess {
    private static final String FUSION_CORE_CLASS = "com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore";

    private FusionBusAccess() {
    }

    /** Checks whether the given BlockEntity has a Fusion Routing Card upgrade installed. */
    public static boolean hasFusionRoutingCard(BlockEntity host) {
        if (!(host instanceof IUpgradeableObject upgradeableObject)) {
            return false;
        }

        return upgradeableObject.isUpgradedWith(ModItems.FUSION_ROUTING_CARD.get());
    }

    /** Finds a Fusion Crafting Core in any of the six adjacent positions. */
    public static Optional<BlockPos> resolveAdjacentFusionCore(ServerLevel level, BlockPos busPos) {
        if (level == null || busPos == null) {
            return Optional.empty();
        }

        for (var direction : net.minecraft.core.Direction.values()) {
            BlockPos candidatePos = busPos.relative(direction);
            BlockEntity blockEntity = level.getBlockEntity(candidatePos);
            if (blockEntity != null && FUSION_CORE_CLASS.equals(blockEntity.getClass().getName())) {
                return Optional.of(candidatePos);
            }
        }

        return Optional.empty();
    }

    /** Finds the nearest Fusion Crafting Core within a cubic range. */
    public static Optional<BlockPos> resolveFusionCoreInRange(ServerLevel level, BlockPos originPos, int range) {
        if (level == null || originPos == null || range < 0) {
            return Optional.empty();
        }

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos candidatePos = originPos.offset(new Vec3i(x, y, z));
                    BlockEntity blockEntity = level.getBlockEntity(candidatePos);
                    if (blockEntity != null && FUSION_CORE_CLASS.equals(blockEntity.getClass().getName())) {
                        return Optional.of(candidatePos);
                    }
                }
            }
        }

        return Optional.empty();
    }
}