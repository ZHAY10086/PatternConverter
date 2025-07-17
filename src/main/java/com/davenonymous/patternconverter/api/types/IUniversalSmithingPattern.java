package com.davenonymous.patternconverter.api.types;

import com.davenonymous.patternconverter.api.IUniversalPattern;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

public interface IUniversalSmithingPattern extends IUniversalPattern {
	RecipeHolder<SmithingRecipe> smithingRecipe();
	SmithingRecipeInput createSmithingInput();
	UniversalItemIngredient getTemplate();
	UniversalItemIngredient getBase();
	UniversalItemIngredient getAddition();
}
