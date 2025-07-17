package com.davenonymous.patternconverter.datacomponents;

import com.davenonymous.patternconverter.mods.ConverterStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConverterStyleDataComponent(ConverterStyle style) {
	public static final Codec<ConverterStyleDataComponent> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ConverterStyle.CODEC.fieldOf("style").forGetter(ConverterStyleDataComponent::style)
		).apply(instance, ConverterStyleDataComponent::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConverterStyleDataComponent> STREAM_CODEC = StreamCodec.composite(
		ConverterStyle.STREAM_CODEC, ConverterStyleDataComponent::style,
		ConverterStyleDataComponent::new
	);
}
