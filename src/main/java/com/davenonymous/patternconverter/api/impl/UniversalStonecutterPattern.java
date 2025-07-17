package com.davenonymous.patternconverter.api.impl;

import com.davenonymous.patternconverter.api.AbstractUniversalPattern;
import com.davenonymous.patternconverter.api.types.IUniversalStonecutterPattern;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;

public class UniversalStonecutterPattern extends AbstractUniversalPattern implements IUniversalStonecutterPattern {
	private RecipeHolder<StonecutterRecipe> recipe = null;
	private UniversalItemIngredient base = UniversalItemIngredient.EMPTY;

	public UniversalStonecutterPattern(UniversalItemIngredient base) {
		this.base = base;
	}

	public UniversalStonecutterPattern(ItemStack base) {
		this(new UniversalItemIngredient(base));
	}

	public void guessRecipe(Level level) {
		var cuttingInput = this.createCuttingInput();
		for(RecipeHolder<StonecutterRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.STONECUTTING)) {
			ItemStack resultStack = recipe.value().getResultItem(level.registryAccess());
			if(!ItemStack.isSameItemSameComponents(resultStack, this.getPrimaryOutputStack())) {
				continue;
			}

			if(!recipe.value().matches(cuttingInput, level)) {
				continue;
			}

			this.recipe = recipe;
		}
	}

	public UniversalStonecutterPattern setRecipe(RecipeHolder<StonecutterRecipe> recipe) {
		this.recipe = recipe;
		return this;
	}

	@Override
	public UniversalItemIngredient input() {
		return base;
	}

	@Override
	public SingleRecipeInput createCuttingInput() {
		return new SingleRecipeInput(base.primary());
	}

	@Override
	public RecipeHolder<StonecutterRecipe> stoneCutterRecipe() {
		return recipe;
	}
}
