package com.davenonymous.patternconverter.networking;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class Register {
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToServer(
			SetClearPatterns.TYPE,
			SetClearPatterns.STREAM_CODEC,
			SetClearPatterns::handle
		);

		registrar.playToServer(
			SetRedstoneMode.TYPE,
			SetRedstoneMode.STREAM_CODEC,
			SetRedstoneMode::handle
		);
	}
}
