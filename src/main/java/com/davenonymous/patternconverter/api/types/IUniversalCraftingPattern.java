package com.davenonymous.patternconverter.api.types;

import com.davenonymous.patternconverter.api.IUniversalPattern;
import com.davenonymous.patternconverter.api.wrapper.ItemTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Map;

public interface IUniversalCraftingPattern extends IUniversalPattern {
	RecipeHolder<CraftingRecipe> craftingRecipe();
	Map<Integer, UniversalItemIngredient> shapedInputIngredients();

	int patternWidth();
	int patternHeight();

	List<ItemStack> getAllInputsAsItemStacks();
	List<UniversalItemIngredient> getAs3by3();

	void setInputStacks(List<ItemStack> stacks);
	void setInputTags(List<ItemTagIngredient> tags);
	void setInputIngredients(List<Ingredient> ingredients);

	void addInput(int slot, UniversalItemIngredient ingredient);
	default void addInput(int slot, ItemStack stack) {
		addInput(slot, new UniversalItemIngredient(stack));
	}

	default void addInput(int slot, ItemTagIngredient tag) {
		addInput(slot, new UniversalItemIngredient(tag));
	}

	default void addInput(int slot, Ingredient ingredient) {
		addInput(slot, new UniversalItemIngredient(ingredient));
	}

	CraftingInput createCraftingInput();
}
