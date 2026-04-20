package com.ae2draconicfusion.autocrafter.registry;

import com.ae2draconicfusion.autocrafter.Ae2DraconicFusionAutocrafterMod;
import com.ae2draconicfusion.autocrafter.ae2.DraconicPatternProviderBlock;
import com.ae2draconicfusion.autocrafter.ae2.DraconicPatternProviderBlockEntity;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.atomic.AtomicReference;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Ae2DraconicFusionAutocrafterMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,
            Ae2DraconicFusionAutocrafterMod.MOD_ID);

    public static final DeferredBlock<DraconicPatternProviderBlock> ME_DRACONIC_PATTERN_PROVIDER = BLOCKS.register(
            "me_draconic_pattern_provider",
            DraconicPatternProviderBlock::new);

            public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PatternProviderBlockEntity>> ME_DRACONIC_PATTERN_PROVIDER_BE = BLOCK_ENTITY_TYPES
                .register("me_draconic_pattern_provider", ModBlocks::createPatternProviderBlockEntityType);

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public static void bindBlockEntities() {
        ME_DRACONIC_PATTERN_PROVIDER.get().bindBlockEntityType(ME_DRACONIC_PATTERN_PROVIDER_BE.get());
    }

    private static BlockEntityType<PatternProviderBlockEntity> createPatternProviderBlockEntityType() {
        AtomicReference<BlockEntityType<PatternProviderBlockEntity>> typeRef = new AtomicReference<>();
        @SuppressWarnings("unchecked")
        BlockEntityType<PatternProviderBlockEntity> type = (BlockEntityType<PatternProviderBlockEntity>) (BlockEntityType<?>) BlockEntityType.Builder
                .of((pos, state) -> new DraconicPatternProviderBlockEntity(typeRef.get(), pos, state), ME_DRACONIC_PATTERN_PROVIDER.get())
                .build(null);
        typeRef.set(type);
        return type;
    }
}