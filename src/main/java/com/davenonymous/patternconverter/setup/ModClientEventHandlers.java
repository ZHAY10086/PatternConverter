package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = PatternConverter.MODID, value = Dist.CLIENT)
public class ModClientEventHandlers {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		PatternConverter.CONTAINER.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}
}
