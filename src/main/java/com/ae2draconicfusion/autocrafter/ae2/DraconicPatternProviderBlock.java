package com.ae2draconicfusion.autocrafter.ae2;

import appeng.block.crafting.PatternProviderBlock;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class DraconicPatternProviderBlock extends PatternProviderBlock {
    public void bindBlockEntityType(BlockEntityType<PatternProviderBlockEntity> blockEntityType) {
        setBlockEntity(PatternProviderBlockEntity.class, blockEntityType, null, null);
    }
}