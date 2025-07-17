package com.davenonymous.patternconverter.networking;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.blocks.ConverterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetClearPatterns(BlockPos pos, boolean clearPatterns) implements CustomPacketPayload {
	public static final Type<SetClearPatterns> TYPE = new Type<>(PatternConverter.resource("set_clear_patterns"));

	public static final StreamCodec<FriendlyByteBuf, SetClearPatterns> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetClearPatterns::pos,
		ByteBufCodecs.BOOL, SetClearPatterns::clearPatterns,
		SetClearPatterns::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SetClearPatterns message, IPayloadContext context) {
		var level = context.player().level();
		ConverterBlockEntity converter = (ConverterBlockEntity) level.getBlockEntity(message.pos);
		if(converter != null) {
			converter.setClearPatterns(message.clearPatterns);
		}
	}
}
