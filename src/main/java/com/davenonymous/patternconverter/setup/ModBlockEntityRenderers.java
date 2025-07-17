package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.blocks.ConverterItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = PatternConverter.MODID)
public class ModBlockEntityRenderers {

	static class ConverterItemExtension implements IClientItemExtensions {
		public final ConverterItemRenderer converterItemRenderer = new ConverterItemRenderer();

		@Override
		public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return converterItemRenderer;
		}
	}

	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new ConverterItemExtension(), ModItems.CONVERTER_ITEM.get());
	}
}
