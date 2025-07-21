package com.davenonymous.patternconverter.api.types;

import com.davenonymous.patternconverter.api.IUniversalPattern;
import com.davenonymous.patternconverter.api.wrapper.FluidTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.ItemTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalFluidIngredient;
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
	List<UniversalFluidIngredient> inputUniversalFluids();

	void addInput(FluidStack fluid);
	void addOutput(FluidStack fluid);

	void addInputEnergy(long energy);
	void addOutputEnergy(long energy);

	void addInput(UniversalItemIngredient ingredient);
	void addInput(UniversalFluidIngredient ingredient);

	default void addInput(ItemStack stack) {
		if(stack.isEmpty()) {
			return;
		}

		addInput(new UniversalItemIngredient(stack));
	}

	default void addInput(ItemTagIngredient tag) {
		if(tag.isEmpty()) {
			return;
		}

		addInput(new UniversalItemIngredient(tag));
	}

	default void addInput(FluidTagIngredient tag) {
		if(tag.isEmpty()) {
			return;
		}

		addInput(new UniversalFluidIngredient(tag));
	}

	default void addInput(Ingredient ingredient) {
		if(ingredient.isEmpty()) {
			return;
		}

		addInput(new UniversalItemIngredient(ingredient));
	}
}
