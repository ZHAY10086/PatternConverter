package com.davenonymous.patternconverter.api.impl;

import com.davenonymous.patternconverter.api.AbstractUniversalPattern;
import com.davenonymous.patternconverter.api.types.IUniversalSmithingPattern;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class UniversalSmithingPattern extends AbstractUniversalPattern implements IUniversalSmithingPattern {
	private RecipeHolder<SmithingRecipe> recipe = null;
	private UniversalItemIngredient template = UniversalItemIngredient.EMPTY;
	private UniversalItemIngredient base = UniversalItemIngredient.EMPTY;
	private UniversalItemIngredient addition = UniversalItemIngredient.EMPTY;

	public UniversalSmithingPattern(ItemStack templateItem, ItemStack baseItem, ItemStack additionItem) {
		this(
			new UniversalItemIngredient(templateItem),
			new UniversalItemIngredient(baseItem),
			new UniversalItemIngredient(additionItem)
		);
	}

	public UniversalSmithingPattern(UniversalItemIngredient template, UniversalItemIngredient base, UniversalItemIngredient addition) {
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	public void guessRecipe(Level level) {
		var smithingInput = this.createSmithingInput();
		for(RecipeHolder<SmithingRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)) {
			ItemStack resultStack = recipe.value().getResultItem(level.registryAccess());
			if(!ItemStack.isSameItemSameComponents(resultStack, this.getPrimaryOutputStack())) {
				continue;
			}

			if(!recipe.value().matches(smithingInput, level)) {
				continue;
			}

			this.setRecipe(recipe);
			break;
		}
	}

	public UniversalSmithingPattern setRecipe(RecipeHolder<SmithingRecipe> recipe) {
		this.recipe = recipe;
		return this;
	}

	@Override
	public UniversalItemIngredient getAddition() {
		return addition;
	}

	@Override
	public UniversalItemIngredient getBase() {
		return base;
	}

	@Override
	public UniversalItemIngredient getTemplate() {
		return template;
	}

	@Override
	public SmithingRecipeInput createSmithingInput() {
		return new SmithingRecipeInput(template.primary(), base.primary(), addition.primary());
	}

	@Override
	public RecipeHolder<SmithingRecipe> smithingRecipe() {
		return recipe;
	}

}
