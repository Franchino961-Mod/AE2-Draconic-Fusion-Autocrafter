package com.ae2draconicfusion.autocrafter.registry;

import com.ae2draconicfusion.autocrafter.Ae2DraconicFusionAutocrafterMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            Ae2DraconicFusionAutocrafterMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ae2_draconic_fusion_autocrafter"))
                    .icon(() -> new ItemStack(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get());
                        output.accept(ModItems.ME_DRACONIC_PATTERN_PROVIDER_PANEL.get());
                        output.accept(ModItems.FUSION_ROUTING_CARD.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}