package com.ae2draconicfusion.autocrafter;

import com.ae2draconicfusion.autocrafter.ae2.AE2UpgradeIntegration;
import com.ae2draconicfusion.autocrafter.ae2.DraconicPatternProviderPart;
import appeng.blockentity.AEBaseBlockEntity;
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

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Main entry point for the AE2 Draconic Fusion Autocrafter mod.
 * Registers all blocks, items, creative tabs, and AE2 integrations
 * during mod loading on NeoForge.
 */
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
        modEventBus.addListener(this::onRegisterCapabilities);
        modEventBus.addListener(this::onRegisterPartCapabilities);
        LOGGER.info("Starting {}", MOD_ID);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AE2UpgradeIntegration.registerUpgrades();
            ModBlocks.bindBlockEntities();

            // Register BlockEntities for Jade/Waila and AE2 visual representation
            AEBaseBlockEntity.registerBlockEntityItem(ModBlocks.ME_DRACONIC_PATTERN_PROVIDER_BE.get(),
                    ModItems.ME_DRACONIC_PATTERN_PROVIDER.get());
        });
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        // Fix for "provider loses connection to network on server restart"
        // AE2 normally registers capabilities by looking at AEBlockEntities subclasses.
        // We must manually register IInWorldGridNodeHost so AE2 connects it upon chunk load.
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlocks.ME_DRACONIC_PATTERN_PROVIDER_BE.get(),
                (be, context) -> (IInWorldGridNodeHost) be
        );

        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                ModBlocks.ME_DRACONIC_PATTERN_PROVIDER_BE.get(),
                (be, context) -> be.getLogic().getReturnInv()
        );
    }

    private void onRegisterPartCapabilities(RegisterPartCapabilitiesEvent event) {
        event.register(
                AECapabilities.GENERIC_INTERNAL_INV,
                (part, side) -> part.getLogic().getReturnInv(),
                DraconicPatternProviderPart.class
        );
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
