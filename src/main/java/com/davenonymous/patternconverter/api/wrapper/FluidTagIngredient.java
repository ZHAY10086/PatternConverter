package com.davenonymous.patternconverter.api.wrapper;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluidTagIngredient {

	public FluidStack representativeFluid = FluidStack.EMPTY;
	public List<String> tags = new ArrayList<>();
	public TagMatchingMode mode;
	public long amount = 1;

	public FluidTagIngredient(FluidStack stack, List<String> alternativeTags) {
		this.mode = TagMatchingMode.ANY;
		this.representativeFluid = stack;
		this.tags.addAll(alternativeTags);
	}

	public FluidTagIngredient(FluidStack stack, List<String> alternativeTags, TagMatchingMode mode) {
		this.mode = mode;
		this.representativeFluid = stack;
		this.tags.addAll(alternativeTags);
	}

	public FluidTagIngredient(TagMatchingMode mode, List<String> tags) {
		this.mode = mode;
		this.tags = tags;
	}

	public FluidTagIngredient(TagMatchingMode mode, String... tags) {
		this.mode = mode;
		this.tags.addAll(Arrays.asList(tags));
	}

	public FluidTagIngredient(String... tags) {
		this(TagMatchingMode.ANY, tags);
	}

	public FluidTagIngredient setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public FluidTagIngredient setMode(TagMatchingMode mode) {
		this.mode = mode;
		return this;
	}

	public FluidTagIngredient setRepresentativeItem(FluidStack representativeItem) {
		this.representativeFluid = representativeItem;
		return this;
	}

	public boolean isEmpty() {
		return representativeFluid.isEmpty() || amount <= 0 || tags.isEmpty();
	}

	public static FluidTagIngredient EMPTY = new FluidTagIngredient();
}
