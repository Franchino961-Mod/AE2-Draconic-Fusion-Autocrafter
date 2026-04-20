package com.ae2draconicfusion.autocrafter.ae2;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartModels;
import appeng.api.stacks.AEItemKey;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.parts.PartModel;
import appeng.parts.crafting.PatternProviderPart;
import com.ae2draconicfusion.autocrafter.Ae2DraconicFusionAutocrafterMod;
import com.ae2draconicfusion.autocrafter.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class DraconicPatternProviderPart extends PatternProviderPart {
    private static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(
            Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/pattern_provider_base");
    private static final PartModel MODELS_OFF = new PartModel(
            MODEL_BASE,
            ResourceLocation.fromNamespaceAndPath(Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/interface_off"));
    private static final PartModel MODELS_ON = new PartModel(
            MODEL_BASE,
            ResourceLocation.fromNamespaceAndPath(Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/interface_on"));
    private static final PartModel MODELS_HAS_CHANNEL = new PartModel(
            MODEL_BASE,
            ResourceLocation.fromNamespaceAndPath(Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/interface_has_channel"));
    private static boolean modelsRegistered;

    public DraconicPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

    public static synchronized void registerPartModels() {
        if (modelsRegistered) {
            return;
        }

        PartModels.registerModels(
                MODEL_BASE,
                ResourceLocation.fromNamespaceAndPath(Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/interface_off"),
                ResourceLocation.fromNamespaceAndPath(Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/interface_on"),
                ResourceLocation.fromNamespaceAndPath(Ae2DraconicFusionAutocrafterMod.MOD_ID, "part/interface_has_channel"));
        modelsRegistered = true;
    }

    @Override
    public IPartModel getStaticModels() {
        if (isActive() && isPowered()) {
            return MODELS_HAS_CHANNEL;
        }
        if (isPowered()) {
            return MODELS_ON;
        }
        return MODELS_OFF;
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new DraconicPatternProviderLogic(getMainNode(), this);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(new ItemStack(ModItems.ME_DRACONIC_PATTERN_PROVIDER_PANEL.get()));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(ModItems.ME_DRACONIC_PATTERN_PROVIDER_PANEL.get());
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public Component getCustomName() {
        return Component.translatable("gui.ae2_draconic_fusion_autocrafter.me_draconic_pattern_provider");
    }
}