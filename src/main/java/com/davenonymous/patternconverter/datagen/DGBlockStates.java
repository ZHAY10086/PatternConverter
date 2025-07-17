package com.davenonymous.patternconverter.datagen;


import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.blocks.ConverterBlock;
import com.davenonymous.patternconverter.mods.ConverterStyle;
import com.davenonymous.patternconverter.setup.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class DGBlockStates extends BlockStateProvider {
	private final ExistingFileHelper exFileHelper;

	public DGBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, PatternConverter.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		ownDirectionalBlock(ModBlocks.CONVERTER_BLOCK.get(), blockState -> {
			ConverterStyle style = blockState.getValue(ConverterBlock.STYLE);
			return new ModelFile.ExistingModelFile(style.modelLocation(), this.exFileHelper);
		});

		// simpleBlockItem(ModBlocks.CONVERTER_BLOCK.get(), new ModelFile.ExistingModelFile(ConverterStyle.DEFAULT.modelLocation(), this.exFileHelper));

	}

	public void ownDirectionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
		this.getVariantBuilder(block).forAllStates((state) -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			var builder = ConfiguredModel.builder().modelFile(modelFunc.apply(state));
			if(dir == Direction.DOWN) {
				builder = builder.rotationX(90);
			} else if(dir == Direction.UP) {
				builder = builder.rotationX(270);
			} else {
				builder = builder.rotationY(((int)dir.toYRot() + 180) % 360);
			}
			return builder.build();
		});
	}
}
