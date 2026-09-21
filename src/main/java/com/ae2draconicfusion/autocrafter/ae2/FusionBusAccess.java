package com.ae2draconicfusion.autocrafter.ae2;

import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Optional;

/**
 * Utility class for locating Draconic Evolution Fusion Crafting Cores in the world.
 */
public final class FusionBusAccess {

    private FusionBusAccess() {
    }

    /** Finds a Fusion Crafting Core in any of the six adjacent positions. */
    public static Optional<BlockPos> resolveAdjacentFusionCore(ServerLevel level, BlockPos busPos) {
        if (level == null || busPos == null) {
            return Optional.empty();
        }

        for (var direction : net.minecraft.core.Direction.values()) {
            BlockPos candidatePos = busPos.relative(direction);
            BlockEntity blockEntity = level.getBlockEntity(candidatePos);
            if (blockEntity instanceof TileFusionCraftingCore) {
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
                    if (blockEntity instanceof TileFusionCraftingCore) {
                        return Optional.of(candidatePos);
                    }
                }
            }
        }

        return Optional.empty();
    }
}