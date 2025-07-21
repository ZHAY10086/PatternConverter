package com.davenonymous.patternconverter.api;

import com.davenonymous.patternconverter.api.wrapper.ItemTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AbstractUniversalPattern implements IUniversalPattern {
	protected List<UniversalItemIngredient> outputIngredients = new ArrayList<>();
	protected boolean allItemsFuzzy = false;
	protected boolean allFluidsFuzzy = false;
	protected boolean hasOutput = false;
	protected boolean hasInput = false;

	public AbstractUniversalPattern setAllItemsFuzzy(boolean allItemsFuzzy) {
		this.allItemsFuzzy = allItemsFuzzy;
		return this;
	}

	public boolean areAllItemsFuzzy() {
		return allItemsFuzzy;
	}

	public AbstractUniversalPattern setAllFluidsFuzzy(boolean allFluidsFuzzy) {
		this.allFluidsFuzzy = allFluidsFuzzy;
		return this;
	}

	public boolean areAllFluidsFuzzy() {
		return allFluidsFuzzy;
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
	public void addOutput(ItemTagIngredient output) {
		if(output.isEmpty()) {
			return;
		}
		addOutput(new UniversalItemIngredient(output));
	}
}
