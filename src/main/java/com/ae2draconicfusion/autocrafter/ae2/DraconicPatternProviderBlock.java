package com.ae2draconicfusion.autocrafter.ae2;

import appeng.block.crafting.PatternProviderBlock;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.ae2draconicfusion.autocrafter.registry.ModItems;
import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Custom AE2 Pattern Provider block for Draconic Evolution Fusion Crafting.
 * Extends the standard AE2 PatternProviderBlock with pick-block support
 * that returns the correct mod item instead of the vanilla AE2 provider.
 */
public final class DraconicPatternProviderBlock extends PatternProviderBlock {
    public void bindBlockEntityType(BlockEntityType<PatternProviderBlockEntity> blockEntityType) {
        setBlockEntity(PatternProviderBlockEntity.class, blockEntityType, null, null);
    }

    @Override
    public ItemStack getCloneItemStack(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new ItemStack(Objects.requireNonNull(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get()));
    }
}