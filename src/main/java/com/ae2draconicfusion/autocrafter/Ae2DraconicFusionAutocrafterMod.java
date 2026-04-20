package com.ae2draconicfusion.autocrafter;

import com.ae2draconicfusion.autocrafter.ae2.AE2UpgradeIntegration;
import com.ae2draconicfusion.autocrafter.ae2.DraconicPatternProviderPart;
import com.ae2draconicfusion.autocrafter.registry.ModBlocks;
import com.ae2draconicfusion.autocrafter.registry.ModCreativeTabs;
import com.ae2draconicfusion.autocrafter.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Ae2DraconicFusionAutocrafterMod.MOD_ID)
public final class Ae2DraconicFusionAutocrafterMod {
    public static final String MOD_ID = "ae2_draconic_fusion_autocrafter";
    private static final Logger LOGGER = LoggerFactory.getLogger(Ae2DraconicFusionAutocrafterMod.class);

    public Ae2DraconicFusionAutocrafterMod(IEventBus modEventBus) {
        DraconicPatternProviderPart.registerPartModels();
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onBuildCreativeModeTabContents);
        LOGGER.info("Starting {}", MOD_ID);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AE2UpgradeIntegration.registerUpgrades();
            ModBlocks.bindBlockEntities();
        });
    }

    private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.FUSION_ROUTING_CARD.get());
            event.accept(ModItems.ME_DRACONIC_PATTERN_PROVIDER_PANEL.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.ME_DRACONIC_PATTERN_PROVIDER.get());
        }
    }
}
