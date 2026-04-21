package com.ae2draconicfusion.autocrafter.ae2;

import appeng.block.crafting.PatternProviderBlock;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import com.ae2draconicfusion.autocrafter.registry.ModItems;

public final class DraconicPatternProviderBlock extends PatternProviderBlock {
    public void bindBlockEntityType(BlockEntityType<PatternProviderBlockEntity> blockEntityType) {
        setBlockEntity(PatternProviderBlockEntity.class, blockEntityType, null, null);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get());
    }
}