package com.ae2draconicfusion.autocrafter.ae2;

import appeng.api.networking.IManagedGridNode;
import appeng.api.stacks.AEItemKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.ae2draconicfusion.autocrafter.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class DraconicPatternProviderBlockEntity extends PatternProviderBlockEntity {
    public DraconicPatternProviderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        IManagedGridNode node = getMainNode();
        PatternProviderLogicHost host = this;
        return new DraconicPatternProviderLogic(node, host);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(new ItemStack(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get()));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get());
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public Component getCustomName() {
        return Component.translatable("gui.ae2_draconic_fusion_autocrafter.me_draconic_pattern_provider");
    }

    @Override
    public Component getName() {
        return Component.translatable("block.ae2_draconic_fusion_autocrafter.me_draconic_pattern_provider");
    }

    @Override
    public Component getDisplayName() {
        return getCustomName();
    }
}