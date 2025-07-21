package com.davenonymous.patternconverter.api.wrapper;

import net.neoforged.neoforge.fluids.FluidStack;

public class UniversalFluidIngredient {
	public static final UniversalFluidIngredient EMPTY = new UniversalFluidIngredient(FluidStack.EMPTY);

	FluidStack stack = FluidStack.EMPTY;
	FluidTagIngredient fluidTagIngredient = FluidTagIngredient.EMPTY;

	public UniversalFluidIngredient(FluidStack stack) {
		this.stack = stack;
	}

	public UniversalFluidIngredient(FluidTagIngredient fluidTagIngredient) {
		this.fluidTagIngredient = fluidTagIngredient;
	}

	public boolean isEmpty() {
		return this == EMPTY || stack.isEmpty() && fluidTagIngredient.isEmpty();
	}

	public boolean isStack() {
		return !stack.isEmpty();
	}

	public boolean isTagIngredient() {
		return !fluidTagIngredient.isEmpty();
	}


	public FluidStack stack() {
		return stack;
	}

	public FluidTagIngredient tagIngredient() {
		return fluidTagIngredient;
	}

	public FluidStack primary() {
		if(!stack.isEmpty()) {
			return stack.copy();
		} else if(!fluidTagIngredient.isEmpty()) {
			return fluidTagIngredient.representativeFluid.copy();
		}

		return FluidStack.EMPTY;
	}
}
