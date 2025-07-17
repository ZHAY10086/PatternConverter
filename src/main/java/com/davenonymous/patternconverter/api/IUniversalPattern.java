package com.davenonymous.patternconverter.api;

import com.davenonymous.patternconverter.api.wrapper.TagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface IUniversalPattern {
	List<UniversalItemIngredient> outputIngredients();

	default void addOutput(ItemStack output) {
		if(output.isEmpty()) {
			return;
		}

		addOutput(new UniversalItemIngredient(output));
	}

	default void addOutput(TagIngredient output) {
		if(output.isEmpty()) {
			return;
		}

		addOutput(new UniversalItemIngredient(output));
	}
	default void addOutput(Ingredient output) {
		if(output.isEmpty()) {
			return;
		}

		addOutput(new UniversalItemIngredient(output));
	}

	void addOutput(UniversalItemIngredient output);

	default boolean isValid() {
		return !outputIngredients().isEmpty();
	}

	default boolean isGenerallyFuzzy() {
		return false;
	}

	default ItemStack getPrimaryOutputStack() {
		if(outputIngredients().isEmpty()) {
			return ItemStack.EMPTY;
		}

		return outputIngredients().getFirst().primary();
	}

}
