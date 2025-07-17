package com.davenonymous.patternconverter.api.types;

import com.davenonymous.patternconverter.api.IUniversalPattern;
import com.davenonymous.patternconverter.api.wrapper.TagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public interface IUniversalProcessingPattern extends IUniversalPattern {
	long inputEnergy();
	long outputEnergy();

	List<FluidStack> inputFluids();
	List<FluidStack> outputFluids();

	List<UniversalItemIngredient> inputIngredients();

	void addInput(FluidStack fluid);
	void addOutput(FluidStack fluid);

	void addInputEnergy(long energy);
	void addOutputEnergy(long energy);

	void addInput(UniversalItemIngredient ingredient);
	default void addInput(ItemStack stack) {
		if(stack.isEmpty()) {
			return;
		}

		addInput(new UniversalItemIngredient(stack));
	}

	default void addInput(TagIngredient tag) {
		if(tag.isEmpty()) {
			return;
		}

		addInput(new UniversalItemIngredient(tag));
	}

	default void addInput(Ingredient ingredient) {
		if(ingredient.isEmpty()) {
			return;
		}

		addInput(new UniversalItemIngredient(ingredient));
	}
}
