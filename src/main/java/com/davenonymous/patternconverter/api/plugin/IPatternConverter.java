package com.davenonymous.patternconverter.api.plugin;

import com.davenonymous.patternconverter.api.*;
import com.davenonymous.patternconverter.api.types.IUniversalCraftingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalProcessingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalSmithingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalStonecutterPattern;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IPatternConverter {
	Item patternItem();

	default Optional<ResourceKey<CreativeModeTab>> creativeTab() {
		return Optional.empty();
	}

	default boolean canRead() {
		return true;
	}

	default boolean canWrite() {
		return true;
	}

	default boolean isPattern(ItemStack stack, Level level) {
		if (stack.isEmpty()) {
			return false;
		}

		return stack.getItem() == patternItem();
	}

	boolean isEmptyPattern(ItemStack stack, Level level);

	IUniversalPattern readPattern(ItemStack stack, Level level);

	default ItemStack writePattern(IUniversalPattern pattern, Level level) {
		if (pattern instanceof IUniversalCraftingPattern craftingPattern) {
			return writePattern(craftingPattern, level);
		} else if (pattern instanceof IUniversalProcessingPattern processingPattern) {
			return writePattern(processingPattern, level);
		} else if (pattern instanceof IUniversalStonecutterPattern stonecutterPattern) {
			return writePattern(stonecutterPattern, level);
		} else if (pattern instanceof IUniversalSmithingPattern smithingPattern) {
			return writePattern(smithingPattern, level);
		}

		return ItemStack.EMPTY;
	}

	ItemStack writePattern(IUniversalProcessingPattern pattern, Level level);

	ItemStack writePattern(IUniversalCraftingPattern pattern, Level level);

	ItemStack writePattern(IUniversalStonecutterPattern pattern, Level level);

	ItemStack writePattern(IUniversalSmithingPattern pattern, Level level);
}
