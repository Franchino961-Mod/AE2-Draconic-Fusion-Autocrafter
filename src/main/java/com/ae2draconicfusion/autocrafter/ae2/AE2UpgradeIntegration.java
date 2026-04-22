package com.ae2draconicfusion.autocrafter.ae2;

import appeng.api.upgrades.Upgrades;
import com.ae2draconicfusion.autocrafter.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers the Fusion Routing Card as an AE2 upgrade card
 * compatible with the Export Bus. Registration is idempotent
 * and safe to call multiple times.
 */
public final class AE2UpgradeIntegration {
    private static final Logger LOGGER = LoggerFactory.getLogger(AE2UpgradeIntegration.class);
    private static final ResourceLocation AE2_EXPORT_BUS_ID = ResourceLocation.fromNamespaceAndPath("ae2", "export_bus");

    private static boolean upgradeRegistered;

    private AE2UpgradeIntegration() {
    }

    public static synchronized void registerUpgrades() {
        if (upgradeRegistered) {
            return;
        }

        Item exportBusItem = BuiltInRegistries.ITEM.getOptional(AE2_EXPORT_BUS_ID).orElse(Items.AIR);
        if (exportBusItem == Items.AIR) {
            LOGGER.warn("AE2 export bus item was not found. Fusion Routing Card upgrade registration skipped.");
            return;
        }

        Upgrades.add(ModItems.FUSION_ROUTING_CARD.get(), exportBusItem, 1);
        upgradeRegistered = true;
        LOGGER.info("Registered Fusion Routing Card as AE2 upgrade for export bus.");
    }
}
