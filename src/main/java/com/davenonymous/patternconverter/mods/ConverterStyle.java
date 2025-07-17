package com.davenonymous.patternconverter.mods;

import com.davenonymous.patternconverter.PatternConverter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum ConverterStyle implements StringRepresentable {
	AE2(0, "ae2", "converter_ae2"),
	REFINED_STORAGE(1, "refinedstorage", "converter_refined_storage"),
	INTEGRATED_DYNAMICS(2, "integrateddynamics", "converter_integrated_dynamics"),;

	private final int id;
	private final String key;
	private final ResourceLocation modelLocation;

	ConverterStyle(int id, String key, String modelName) {
		this.id = id;
		this.key = key;
		this.modelLocation = PatternConverter.resource("block/" + modelName);
	}

	public static final ConverterStyle DEFAULT = AE2;

	public static final IntFunction<ConverterStyle> BY_ID =
		ByIdMap.continuous(
			ConverterStyle::getId,
			ConverterStyle.values(),
			ByIdMap.OutOfBoundsStrategy.ZERO
		);

	public static final EnumCodec<ConverterStyle> CODEC = StringRepresentable.fromEnum(ConverterStyle::values);

	public static final StreamCodec<ByteBuf, ConverterStyle> STREAM_CODEC =
		ByteBufCodecs.idMapper(ConverterStyle.BY_ID, ConverterStyle::getId);

	public static ConverterStyle byId(int id) {
		return BY_ID.apply(id);
	}

	public static ConverterStyle byMod(String name) {
		for (ConverterStyle style : ConverterStyle.values()) {
			if (style.key.equals(name)) {
				return style;
			}
		}

		return DEFAULT;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String getSerializedName() {
		return this.key;
	}

	public ResourceLocation modelLocation() {
		return modelLocation;
	}
}
