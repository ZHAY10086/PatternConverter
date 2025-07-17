package com.davenonymous.patternconverter.api.types;

import com.davenonymous.patternconverter.api.IUniversalPattern;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public interface IUniversalStonecutterPattern extends IUniversalPattern {
	RecipeHolder<StonecutterRecipe> stoneCutterRecipe();
	SingleRecipeInput createCuttingInput();
	UniversalItemIngredient input();
}
