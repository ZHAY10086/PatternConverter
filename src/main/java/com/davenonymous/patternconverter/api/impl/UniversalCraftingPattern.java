package com.davenonymous.patternconverter.api.impl;

import com.davenonymous.patternconverter.api.AbstractUniversalPattern;
import com.davenonymous.patternconverter.api.types.IUniversalCraftingPattern;
import com.davenonymous.patternconverter.api.wrapper.TagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniversalCraftingPattern extends AbstractUniversalPattern implements IUniversalCraftingPattern {
	private int patternWidth = 3;
	private int patternHeight = 3;
	private Map<Integer, UniversalItemIngredient> shapedInputUniversalItems = new HashMap<>();
	private RecipeHolder<CraftingRecipe> craftingRecipe = null;

	public UniversalCraftingPattern() {
	}

	public UniversalCraftingPattern(CraftingInput craftingInput) {
		this.patternWidth = craftingInput.width();
		this.patternHeight = craftingInput.height();
		this.setInputStacks(craftingInput.items());

	}

	@Override
	public RecipeHolder<CraftingRecipe> craftingRecipe() {
		return craftingRecipe;
	}

	public void guessRecipe(Level level) {
		var craftingInput = this.createCraftingInput();
		for(RecipeHolder<CraftingRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
			ItemStack resultStack = recipe.value().getResultItem(level.registryAccess());
			if(resultStack.isEmpty()) {
				continue;
			}

			if(!ItemStack.isSameItemSameComponents(resultStack, this.getPrimaryOutputStack())) {
				continue;
			}

			if(!recipe.value().matches(craftingInput, level)) {
				continue;
			}

			this.craftingRecipe = recipe;
			break;
		}
	}

	@Override
	public Map<Integer, UniversalItemIngredient> shapedInputIngredients() {
		return shapedInputUniversalItems;
	}

	@Override
	public int patternWidth() {
		return patternWidth;
	}

	@Override
	public int patternHeight() {
		return patternHeight;
	}

	public UniversalCraftingPattern setRecipe(RecipeHolder<CraftingRecipe> craftingRecipe) {
		this.craftingRecipe = craftingRecipe;
		return this;
	}

	public UniversalCraftingPattern setPatternHeight(int patternHeight) {
		this.patternHeight = patternHeight;
		return this;
	}

	public UniversalCraftingPattern setPatternWidth(int patternWidth) {
		this.patternWidth = patternWidth;
		return this;
	}

	@Override
	public void setInputStacks(List<ItemStack> stacks) {
		if(stacks.isEmpty()) {
			return;
		}

		for(int slot = 0; slot < stacks.size(); slot++) {
			var ingredient = stacks.get(slot);
			addInput(slot, ingredient);
		}
	}

	@Override
	public void setInputIngredients(List<Ingredient> ingredients) {
		if(ingredients.isEmpty()) {
			return;
		}

		for(int slot = 0; slot < ingredients.size(); slot++) {
			var ingredient = ingredients.get(slot);
			addInput(slot, ingredient);
		}
	}

	@Override
	public void setInputTags(List<TagIngredient> tags) {
		if(tags.isEmpty()) {
			return;
		}

		for(int slot = 0; slot < tags.size(); slot++) {
			var tag = tags.get(slot);
			addInput(slot, tag);
		}
	}

	@Override
	public void addInput(int slot, UniversalItemIngredient input) {
		if(input.isEmpty()) {
			return;
		}

		this.shapedInputUniversalItems.put(slot, input);
		this.hasInput = true;
	}

	@Override
	public List<ItemStack> getAllInputsAsItemStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		for(int row = 0; row < patternHeight; row++) {
			for(int col = 0; col < patternWidth; col++) {
				int slot = row * patternWidth + col;
				if(!this.shapedInputUniversalItems.containsKey(slot)) {
					stacks.add(ItemStack.EMPTY);
					continue;
				}

				UniversalItemIngredient ingredient = this.shapedInputUniversalItems.get(slot);
				if (ingredient.isEmpty()) {
					stacks.add(ItemStack.EMPTY);
					continue;
				}

				stacks.add(ingredient.primary());
			}
		}

		return stacks;
	}

	@Override
	public List<UniversalItemIngredient> getAs3by3() {
		List<UniversalItemIngredient> stacks = new ArrayList<>();
		for(int slot = 0; slot < 9; slot++) {
			int col = slot % 3;
			int row = slot / 3;
			int transformedSlot = slot;
			if(patternWidth < 3) {
				transformedSlot = slot - row;
			}

			if(col >= patternWidth || row >= patternHeight) {
				stacks.add(UniversalItemIngredient.EMPTY);
				continue;
			}

			if(!this.shapedInputUniversalItems.containsKey(transformedSlot)) {
				stacks.add(UniversalItemIngredient.EMPTY);
				continue;
			}

			UniversalItemIngredient ingredient = this.shapedInputUniversalItems.get(transformedSlot);
			if (ingredient.isEmpty()) {
				stacks.add(UniversalItemIngredient.EMPTY);
				continue;
			}

			stacks.add(ingredient);
		}

		return stacks;
	}

	@Override
	public CraftingInput createCraftingInput() {
		return CraftingInput.of(patternWidth, patternHeight, this.getAllInputsAsItemStacks());
	}
}
