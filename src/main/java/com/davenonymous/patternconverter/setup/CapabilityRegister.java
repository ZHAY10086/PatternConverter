package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.blocks.ConverterBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = PatternConverter.MODID)
public class CapabilityRegister {

	@SubscribeEvent
	public static void onCapabilityRegister(RegisterCapabilitiesEvent event) {

		event.registerBlock(
			Capabilities.ItemHandler.BLOCK,
			ConverterBlockEntity::getCapability,
			ModBlocks.CONVERTER_BLOCK.get()
		);
	}

}
