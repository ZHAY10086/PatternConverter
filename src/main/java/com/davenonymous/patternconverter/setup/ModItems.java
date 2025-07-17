package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PatternConverter.MODID);

	public static final DeferredItem<BlockItem> CONVERTER_ITEM = ITEMS
		.register("converter", () -> new BlockItem(ModBlocks.CONVERTER_BLOCK.get(), new BlockItem.Properties()));
}
