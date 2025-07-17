package com.davenonymous.patternconverter.mods;

import com.davenonymous.patternconverter.PatternConverter;

import com.davenonymous.patternconverter.api.plugin.IPatternConverter;
import com.davenonymous.patternconverter.api.plugin.PatternConverterSupport;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.*;

public class PluginLoader {
	public static List<IPatternConverter> converters = new ArrayList<>();
	public static Map<String, IPatternConverter> converterMap = new HashMap<>();

	public static ConverterStyle getPatternConverterStyle(ItemStack stack, Level level) {
		for (var converterEntry : converterMap.entrySet()) {
			IPatternConverter converter = converterEntry.getValue();
			String modId = converterEntry.getKey();
			if (converter.isPattern(stack, level)) {
				return ConverterStyle.byMod(modId);
			}
		}
		return ConverterStyle.DEFAULT;
	}

	public static Optional<IPatternConverter> getPatternConverter(ItemStack stack, Level level) {
		if (stack.isEmpty()) {
			return Optional.empty();
		}

		for (IPatternConverter converter : converters) {
			if (converter.isPattern(stack, level)) {
				return Optional.of(converter);
			}
		}

		return Optional.empty();
	}

	public static void loadPlugins() {
		converters.clear();

		for (ModFileScanData scanData : ModList.get().getAllScanData()) {
			scanData.getAnnotatedBy(PatternConverterSupport.class, ElementType.TYPE).forEach(annotationData -> {
				Object modId = annotationData.annotationData().get("modid");
				if(!(modId instanceof String modid)) {
					return;
				}

				if(!ModList.get().isLoaded(modid)) {
					PatternConverter.LOGGER.debug("Skipping Pattern Converter support for mod '{}'. It is not loaded.", modid);
					return;
				}

				try {
					Class<?> clazz = Class.forName(annotationData.clazz().getClassName());
					IPatternConverter converter = (IPatternConverter) clazz.getDeclaredConstructor().newInstance();
					converters.add(converter);
					converterMap.put(modid, converter);

					PatternConverter.LOGGER.info("Loaded @PatternConverterSupport class '{}' for mod '{}'.",annotationData.clazz().getClassName(), modid);
				} catch (Exception e) {
					PatternConverter.LOGGER.error("Failed to instantiate @PatternConverterSupport class '{}': {}", annotationData.clazz().getClassName(), e);
				}
			});
		}

		if(converters.size() <= 1) {
			PatternConverter.LOGGER.error("No pattern converters found. Disabling functionality.");
		} else {
			PatternConverter.LOGGER.info("Loaded " + converters.size() + " pattern converters.");
		}

	}
}
