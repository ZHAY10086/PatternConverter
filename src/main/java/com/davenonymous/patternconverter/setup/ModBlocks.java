package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.blocks.ConverterBlock;
import com.davenonymous.patternconverter.blocks.ConverterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PatternConverter.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PatternConverter.MODID);
	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, PatternConverter.MODID);

	public static final DeferredBlock<Block> CONVERTER_BLOCK = BLOCKS.register(
		"converter",
		() -> new ConverterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
	);

	public static final Supplier<BlockEntityType<ConverterBlockEntity>> CONVERTER_ENTITY = BLOCK_ENTITIES.register(
		"converter",
		() -> BlockEntityType.Builder.of(ConverterBlockEntity::new, CONVERTER_BLOCK.get())
			.build(null)
	);

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ConverterBlock>> CONVERTER_BLOCK_TYPE = BLOCK_TYPES.register(
		"converter",
		() -> BlockBehaviour.simpleCodec(ConverterBlock::new)
	);

}
