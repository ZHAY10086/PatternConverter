package com.davenonymous.patternconverter.api;

import com.davenonymous.patternconverter.api.wrapper.TagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AbstractUniversalPattern implements IUniversalPattern {
	protected List<UniversalItemIngredient> outputIngredients = new ArrayList<>();
	protected boolean isGenerallyFuzzy = false;
	protected boolean hasOutput = false;
	protected boolean hasInput = false;

	public AbstractUniversalPattern setGenerallyFuzzy(boolean generallyFuzzy) {
		isGenerallyFuzzy = generallyFuzzy;
		return this;
	}

	public boolean isGenerallyFuzzy() {
		return isGenerallyFuzzy;
	}

	@Override
	public boolean isValid() {
		return hasInput && hasOutput;
	}

	@Override
	public List<UniversalItemIngredient> outputIngredients() {
		return outputIngredients;
	}

	@Override
	public void addOutput(UniversalItemIngredient output) {
		if(output.isEmpty()) {
			return;
		}
		this.outputIngredients.add(output);
		this.hasOutput = true;
	}

	@Override
	public void addOutput(Ingredient output) {
		if(output.isEmpty()) {
			return;
		}

		addOutput(new UniversalItemIngredient(output));
	}

	@Override
	public void addOutput(ItemStack output) {
		if(output.isEmpty()) {
			return;
		}

		addOutput(new UniversalItemIngredient(output));
	}

	@Override
	public void addOutput(TagIngredient output) {
		if(output.isEmpty()) {
			return;
		}
		addOutput(new UniversalItemIngredient(output));
	}
}
