package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.datacomponents.ConverterStyleDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PatternConverter.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ConverterStyleDataComponent>> CONVERTER_STYLE_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"style",
		builder -> builder
			.persistent(ConverterStyleDataComponent.CODEC)
			.networkSynchronized(ConverterStyleDataComponent.STREAM_CODEC)
	);

}
