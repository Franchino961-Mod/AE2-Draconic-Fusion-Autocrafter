package com.ae2draconicfusion.autocrafter.registry;

import com.ae2draconicfusion.autocrafter.Ae2DraconicFusionAutocrafterMod;
import com.ae2draconicfusion.autocrafter.ae2.DraconicPatternProviderPart;
import appeng.api.upgrades.Upgrades;
import appeng.items.parts.PartItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registry for all mod items: block items, parts, and upgrade cards. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Ae2DraconicFusionAutocrafterMod.MOD_ID);

    public static final DeferredItem<Item> ME_DRACONIC_PATTERN_PROVIDER = ITEMS.register("me_draconic_pattern_provider",
            () -> new BlockItem(ModBlocks.ME_DRACONIC_PATTERN_PROVIDER.get(), new Item.Properties()));

    public static final DeferredItem<Item> ME_DRACONIC_PATTERN_PROVIDER_PANEL = ITEMS.register("me_draconic_pattern_provider_panel",
            () -> new PartItem<>(new Item.Properties(), DraconicPatternProviderPart.class, DraconicPatternProviderPart::new));

    public static final DeferredItem<Item> FUSION_ROUTING_CARD = ITEMS.register("fusion_routing_card",
            () -> Upgrades.createUpgradeCardItem(new Item.Properties().stacksTo(1))); // No recipe yet

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
