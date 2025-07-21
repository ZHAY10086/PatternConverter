package com.davenonymous.patternconverter.api.impl;

import com.davenonymous.patternconverter.api.AbstractUniversalPattern;
import com.davenonymous.patternconverter.api.types.IUniversalProcessingPattern;
import com.davenonymous.patternconverter.api.wrapper.UniversalFluidIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class UniversalProcessingPattern extends AbstractUniversalPattern implements IUniversalProcessingPattern {
	private long inputEnergy = 0;
	private long outputEnergy = 0;
	private List<FluidStack> inputFluids = new ArrayList<>();
	private List<FluidStack> outputFluids = new ArrayList<>();
	private List<UniversalItemIngredient> inputUniversalItems = new ArrayList<>();
	private List<UniversalFluidIngredient> inputUniversalFluids = new ArrayList<>();

	public UniversalProcessingPattern() {
	}

	public UniversalProcessingPattern(UniversalSuperPattern superPattern) {
		this.inputEnergy = superPattern.inputEnergy();
		this.outputEnergy = superPattern.outputEnergy();
		this.inputFluids.addAll(superPattern.inputFluids());
		this.outputFluids.addAll(superPattern.outputFluids());
		this.inputUniversalItems.addAll(superPattern.shapedInputIngredients().values().stream().filter(Predicate.not(UniversalItemIngredient::isEmpty)).toList());
		this.inputUniversalFluids.addAll(superPattern.inputUniversalFluids().stream().filter(Predicate.not(UniversalFluidIngredient::isEmpty)).toList());
		this.outputIngredients.addAll(superPattern.outputIngredients());
	}

	@Override
	public long inputEnergy() {
		return inputEnergy;
	}

	@Override
	public List<FluidStack> inputFluids() {
		return inputFluids;
	}

	@Override
	public long outputEnergy() {
		return outputEnergy;
	}

	@Override
	public List<FluidStack> outputFluids() {
		return outputFluids;
	}

	@Override
	public List<UniversalItemIngredient> inputIngredients() {
		return inputUniversalItems;
	}

	@Override
	public List<UniversalFluidIngredient> inputUniversalFluids() {
		return inputUniversalFluids;
	}

	@Override
	public void addInput(FluidStack fluid) {
		inputFluids.add(fluid.copy());
	}

	@Override
	public void addOutput(FluidStack fluid) {
		outputFluids.add(fluid.copy());
	}

	@Override
	public void addInputEnergy(long energy) {
		inputEnergy += energy;
	}

	@Override
	public void addOutputEnergy(long energy) {
		outputEnergy += energy;
	}

	@Override
	public void addInput(UniversalItemIngredient ingredient) {
		inputUniversalItems.add(ingredient);
	}

	@Override
	public void addInput(UniversalFluidIngredient ingredient) {
		inputUniversalFluids.add(ingredient);
	}

}
