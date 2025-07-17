package com.davenonymous.patternconverter.setup;


import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.blocks.ConverterContainer;
import com.davenonymous.patternconverter.blocks.ConverterScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = PatternConverter.MODID)
public class ModContainers {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, PatternConverter.MODID);

	public static final Supplier<MenuType<ConverterContainer>> CONVERTER_CONTAINER = CONTAINERS.register(
		"converter", resourceLocation -> IMenuTypeExtension.create(
			(i, inventory, registryFriendlyByteBuf) -> new ConverterContainer(i, registryFriendlyByteBuf.readBlockPos(), inventory, inventory.player)
		)
	);

	@SubscribeEvent
	public static void attachScreens(RegisterMenuScreensEvent event) {
		event.register(CONVERTER_CONTAINER.get(), ConverterScreen::new);
	}
}
