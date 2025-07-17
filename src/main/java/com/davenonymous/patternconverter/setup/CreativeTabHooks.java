package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.api.plugin.IPatternConverter;
import com.davenonymous.patternconverter.datacomponents.ConverterStyleDataComponent;
import com.davenonymous.patternconverter.mods.ConverterStyle;
import com.davenonymous.patternconverter.mods.PluginLoader;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber
public class CreativeTabHooks {
	@SubscribeEvent // on the mod event bus
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		ItemStack converterItem = new ItemStack(ModItems.CONVERTER_ITEM.get());
		for(var converterEntry : PluginLoader.converterMap.entrySet()) {
			String modId = converterEntry.getKey();
			IPatternConverter converter = converterEntry.getValue();
			if(converter.creativeTab().isEmpty()) {
				continue;
			}

			if(event.getTabKey() != converter.creativeTab().get()) {
				continue;
			}
			ItemStack recipeOutputStack = converterItem.copy();
			recipeOutputStack.set(ModDataComponents.CONVERTER_STYLE_COMPONENT.get(), new ConverterStyleDataComponent(ConverterStyle.byMod(modId)));
			event.accept(recipeOutputStack);
		}
	}
}
