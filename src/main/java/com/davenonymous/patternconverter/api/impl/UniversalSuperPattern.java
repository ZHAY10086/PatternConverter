package com.davenonymous.patternconverter.api.impl;

import com.davenonymous.patternconverter.api.*;
import com.davenonymous.patternconverter.api.types.IUniversalCraftingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalProcessingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalSmithingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalStonecutterPattern;
import com.davenonymous.patternconverter.api.wrapper.ItemTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalFluidIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

public class UniversalSuperPattern extends AbstractUniversalPattern implements IUniversalCraftingPattern, IUniversalProcessingPattern, IUniversalStonecutterPattern, IUniversalSmithingPattern {
	// Crafting values
	private int patternWidth = 0;
	private int patternHeight = 0;
	private Map<Integer, UniversalItemIngredient> shapedInputUniversalItems = new HashMap<>();
	private RecipeHolder<CraftingRecipe> craftingRecipe = null;

	// Processing values
	private long inputEnergy = 0;
	private long outputEnergy = 0;
	private List<FluidStack> inputFluids = new ArrayList<>();
	private List<FluidStack> outputFluids = new ArrayList<>();
	private List<UniversalItemIngredient> inputUniversalItems = new ArrayList<>();
	private List<UniversalFluidIngredient> inputUniversalFluids = new ArrayList<>();

	// Smithing values
	private RecipeHolder<SmithingRecipe> smithingRecipe = null;
	private UniversalItemIngredient template = UniversalItemIngredient.EMPTY;
	private UniversalItemIngredient base = UniversalItemIngredient.EMPTY;
	private UniversalItemIngredient addition = UniversalItemIngredient.EMPTY;

	// Stonecutter values
	private RecipeHolder<StonecutterRecipe> stonecutterRecipe = null;
	private UniversalItemIngredient stone = UniversalItemIngredient.EMPTY;

	public Optional<IUniversalPattern> guessRecipe(Level level) {
		if(outputIngredients().size() == 1 && inputEnergy == 0 && outputEnergy == 0 && outputFluids.isEmpty() && inputFluids.isEmpty()) {

			var smithingInput = this.createSmithingInput();
			for(RecipeHolder<SmithingRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)) {
				ItemStack resultStack = recipe.value().getResultItem(level.registryAccess());
				if(!ItemStack.isSameItemSameComponents(resultStack, this.getPrimaryOutputStack())) {
					continue;
				}

				if(!recipe.value().matches(smithingInput, level)) {
					continue;
				}

				var result = new UniversalSmithingPattern(
					shapedInputUniversalItems.getOrDefault(0, UniversalItemIngredient.EMPTY).primary(),
					shapedInputUniversalItems.getOrDefault(1, UniversalItemIngredient.EMPTY).primary(),
					shapedInputUniversalItems.getOrDefault(2, UniversalItemIngredient.EMPTY).primary()
				);
				result.setRecipe(recipe);
				result.addOutput(resultStack.copy());

				return Optional.of(result);
			}

			var cuttingInput = this.createCuttingInput();
			for(RecipeHolder<StonecutterRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.STONECUTTING)) {
				ItemStack resultStack = recipe.value().getResultItem(level.registryAccess());
				if(!ItemStack.isSameItemSameComponents(resultStack, this.getPrimaryOutputStack())) {
					continue;
				}

				if(!recipe.value().matches(cuttingInput, level)) {
					continue;
				}

				var result = new UniversalStonecutterPattern(cuttingInput.item());
				result.setRecipe(recipe);
				result.addOutput(resultStack.copy());
				return Optional.of(result);
			}

			var craftingInput = this.createCraftingInput();
			for(RecipeHolder<CraftingRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
				ItemStack resultStack = recipe.value().getResultItem(level.registryAccess());
				if(!ItemStack.isSameItemSameComponents(resultStack, this.getPrimaryOutputStack())) {
					continue;
				}

				if(!recipe.value().matches(craftingInput, level)) {
					continue;
				}

				var result = new UniversalCraftingPattern(craftingInput);
				result.setRecipe(recipe);
				result.addOutput(resultStack.copy());
				return Optional.of(result);
			}
		}

		return Optional.of(new UniversalProcessingPattern(this));
	}


	@Override
	public RecipeHolder<CraftingRecipe> craftingRecipe() {
		if (craftingRecipe == null) {

		}
		return craftingRecipe;
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

	public void setPatternHeight(int patternHeight) {
		this.patternHeight = patternHeight;
	}

	public void setPatternWidth(int patternWidth) {
		this.patternWidth = patternWidth;
	}

	@Override
	public void setInputStacks(List<ItemStack> stacks) {
		if(stacks.isEmpty()) {
			return;
		}

		for(int slot = 0; slot < stacks.size(); slot++) {
			var ingredient = stacks.get(slot);
			if(ingredient.isEmpty()) {
				continue;
			}
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
			if(ingredient.isEmpty()) {
				continue;
			}
			addInput(slot, ingredient);
		}
	}

	@Override
	public void setInputTags(List<ItemTagIngredient> tags) {
		if(tags.isEmpty()) {
			return;
		}

		for(int slot = 0; slot < tags.size(); slot++) {
			var tag = tags.get(slot);
			if(tag.isEmpty()) {
				continue;
			}
			addInput(slot, tag);
		}
	}

	@Override
	public void addInput(int slot, UniversalItemIngredient input) {
		if(input.isEmpty()) {
			return;
		}

		int col = slot % 3;
		int row = slot / 3;

		patternWidth = Math.max(patternWidth, col + 1);
		patternHeight = Math.max(patternHeight, row + 1);

		this.shapedInputUniversalItems.put(slot, input);
		this.hasInput = true;
	}

	@Override
	public List<ItemStack> getAllInputsAsItemStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		for(int col = 0; col < patternWidth; col++) {
			for(int row = 0; row < patternHeight; row++) {
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
		for(int col = 0; col < patternWidth; col++) {
			for(int row = 0; row < patternHeight; row++) {
				int slot = row * 3 + col;
				if(!this.shapedInputUniversalItems.containsKey(slot)) {
					stacks.add(UniversalItemIngredient.EMPTY);
					continue;
				}

				UniversalItemIngredient ingredient = this.shapedInputUniversalItems.get(slot);
				if (ingredient.isEmpty()) {
					stacks.add(UniversalItemIngredient.EMPTY);
					continue;
				}

				stacks.add(ingredient);
			}
		}

		return stacks;
	}

	@Override
	public CraftingInput createCraftingInput() {
		List<ItemStack> craftingStacks = new ArrayList<>();
		for(int slot = 0; slot < 9; slot++) {
			int col = slot % 3;
			int row = slot / 3;
			if(col >= patternWidth || row >= patternHeight) {
				continue;
			}

			if(!this.shapedInputUniversalItems.containsKey(slot)) {
				craftingStacks.add(ItemStack.EMPTY);
				continue;
			}

			UniversalItemIngredient ingredient = this.shapedInputUniversalItems.get(slot);
			if (ingredient.isEmpty()) {
				craftingStacks.add(ItemStack.EMPTY);
				continue;
			}
			craftingStacks.add(ingredient.primary());
		}

		return CraftingInput.of(patternWidth, patternHeight, craftingStacks);
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
		return new SmithingRecipeInput(
			shapedInputUniversalItems.getOrDefault(0, UniversalItemIngredient.EMPTY).primary(),
			shapedInputUniversalItems.getOrDefault(1, UniversalItemIngredient.EMPTY).primary(),
			shapedInputUniversalItems.getOrDefault(2, UniversalItemIngredient.EMPTY).primary()
		);
	}

	@Override
	public RecipeHolder<SmithingRecipe> smithingRecipe() {
		return smithingRecipe;
	}


	@Override
	public UniversalItemIngredient input() {
		return base;
	}

	@Override
	public SingleRecipeInput createCuttingInput() {
		return new SingleRecipeInput(shapedInputUniversalItems.getOrDefault(0, UniversalItemIngredient.EMPTY).primary());
	}

	@Override
	public RecipeHolder<StonecutterRecipe> stoneCutterRecipe() {
		return stonecutterRecipe;
	}
}
