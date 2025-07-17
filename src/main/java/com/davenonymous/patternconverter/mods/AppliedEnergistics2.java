package com.davenonymous.patternconverter.mods;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.ids.AEComponents;
import appeng.api.ids.AECreativeTabIds;
import appeng.api.ids.AEItemIds;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.api.*;
import com.davenonymous.patternconverter.api.impl.UniversalCraftingPattern;
import com.davenonymous.patternconverter.api.impl.UniversalProcessingPattern;
import com.davenonymous.patternconverter.api.impl.UniversalSmithingPattern;
import com.davenonymous.patternconverter.api.impl.UniversalStonecutterPattern;
import com.davenonymous.patternconverter.api.plugin.IPatternConverter;
import com.davenonymous.patternconverter.api.plugin.PatternConverterSupport;
import com.davenonymous.patternconverter.api.types.IUniversalCraftingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalProcessingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalSmithingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalStonecutterPattern;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PatternConverterSupport(modid = "ae2")
public class AppliedEnergistics2 implements IPatternConverter {

	@Override
	public Optional<ResourceKey<CreativeModeTab>> creativeTab() {
		return Optional.of(AECreativeTabIds.MAIN);
	}

	@Override
	public Item patternItem() {
		return BuiltInRegistries.ITEM.get(AEItemIds.BLANK_PATTERN);
	}

	@Override
	public boolean isPattern(ItemStack stack, Level level) {
		Item item = stack.getItem();
		if(item.builtInRegistryHolder().getKey().location().equals(AEItemIds.BLANK_PATTERN)) {
			return true;
		}

		return PatternDetailsHelper.isEncodedPattern(stack);
	}

	@Override
	public boolean isEmptyPattern(ItemStack stack, Level level) {
		IPatternDetails pattern = PatternDetailsHelper.decodePattern(stack, level);
		if(pattern == null) {
			return true;
		}

		if(pattern.getOutputs().isEmpty() || pattern.getInputs().length == 0) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IUniversalPattern readPattern(ItemStack stack, Level level) {
		IPatternDetails pattern = PatternDetailsHelper.decodePattern(stack, level);
		if(pattern == null) {
			return null;
		}

		var craftingPattern = pattern.getDefinition().get(AEComponents.ENCODED_CRAFTING_PATTERN);
		if(craftingPattern != null) {
			var result = new UniversalCraftingPattern();
			if(craftingPattern.canSubstitute() || craftingPattern.canSubstituteFluids()) {
				result.setGenerallyFuzzy(true);
			}

			for(int slot = 0; slot < craftingPattern.inputs().size(); slot++) {
				ItemStack itemStack = craftingPattern.inputs().get(slot);
				if(itemStack.isEmpty()) {
					continue;
				}

				result.addInput(slot, itemStack.copy());
			}
			result.addOutput(craftingPattern.result().copy());

			Optional<RecipeHolder<?>> recipe = level.getRecipeManager().byKey(craftingPattern.recipeId());
			if(recipe.isPresent() && recipe.get().value() instanceof CraftingRecipe) {
				result.setRecipe((RecipeHolder<CraftingRecipe>) recipe.get());
			}

			return result;
		}

		var smithingPattern = pattern.getDefinition().get(AEComponents.ENCODED_SMITHING_TABLE_PATTERN);
		if(smithingPattern != null) {
			var result = new UniversalSmithingPattern(
				smithingPattern.template().copy(),
				smithingPattern.base().copy(),
				smithingPattern.addition().copy()
			);
			if(smithingPattern.canSubstitute()) {
				result.setGenerallyFuzzy(true);
			}

			Optional<RecipeHolder<?>> recipe = level.getRecipeManager().byKey(smithingPattern.recipeId());
			if(recipe.isPresent() && recipe.get().value() instanceof SmithingRecipe) {
				result.setRecipe((RecipeHolder<SmithingRecipe>) recipe.get());
			}

			result.addOutput(smithingPattern.resultItem());
			return result;
		}

		var stonecuttingPattern = pattern.getDefinition().get(AEComponents.ENCODED_STONECUTTING_PATTERN);
		if(stonecuttingPattern != null) {
			var result = new UniversalStonecutterPattern(stonecuttingPattern.input());
			if(stonecuttingPattern.canSubstitute()) {
				result.setGenerallyFuzzy(true);
			}

			Optional<RecipeHolder<?>> recipe = level.getRecipeManager().byKey(stonecuttingPattern.recipeId());
			if(recipe.isPresent() && recipe.get().value() instanceof StonecutterRecipe) {
				result.setRecipe((RecipeHolder<StonecutterRecipe>) recipe.get());
			}

			result.addOutput(stonecuttingPattern.output());
			return result;
		}

		var processingPattern = pattern.getDefinition().get(AEComponents.ENCODED_PROCESSING_PATTERN);
		if(processingPattern != null) {
			var result = new UniversalProcessingPattern();
			for(var input : processingPattern.sparseInputs()) {
				if(input == null || input.amount() <= 0) {
					continue;
				}

				var what = input.what();
				if(what instanceof AEItemKey aeItemKey) {
					ItemStack itemStack = aeItemKey.getReadOnlyStack();
					result.addInput(itemStack.copy());
				} else if(what instanceof AEFluidKey aeFluidKey) {
					FluidStack fluidStack = aeFluidKey.toStack((int) input.amount());
					result.addInput(fluidStack.copy());
				}

			}

			for(var output : processingPattern.sparseOutputs()) {
				if(output == null || output.amount() <= 0) {
					continue;
				}

				var what = output.what();
				if(what instanceof AEItemKey aeItemKey) {
					ItemStack itemStack = aeItemKey.getReadOnlyStack();
					result.addOutput(itemStack.copy());
				} else if(what instanceof AEFluidKey aeFluidKey) {
					FluidStack fluidStack = aeFluidKey.toStack((int) output.amount());
					result.addOutput(fluidStack.copy());
				} else {
					PatternConverter.LOGGER.warn("AE2 pattern output is neither item nor fluid: {}", what);
				}
			}

			return result;
		}

		return null;
	}

	@Override
	public ItemStack writePattern(IUniversalProcessingPattern pattern, Level level) {
		List<GenericStack> outputStacks = new ArrayList<>();
		List<GenericStack> inputStacks = new ArrayList<>();
		for(var itemIngredient : pattern.inputIngredients()) {
			var genericStack = GenericStack.fromItemStack(itemIngredient.primary());
			inputStacks.add(genericStack);
		}

		for(FluidStack inputFluid : pattern.inputFluids()) {
			inputStacks.add(GenericStack.fromFluidStack(inputFluid));
		}

		for(var itemIngredient : pattern.outputIngredients()) {
			outputStacks.add(GenericStack.fromItemStack(itemIngredient.primary()));
		}

		for(FluidStack outputFluid : pattern.outputFluids()) {
			outputStacks.add(GenericStack.fromFluidStack(outputFluid));
		}

		if(inputStacks.isEmpty() || outputStacks.isEmpty()) {
			PatternConverter.LOGGER.warn("Cannot write processing pattern, no input or output defined. Returning empty pattern.");
			return ItemStack.EMPTY;
		}

		return PatternDetailsHelper.encodeProcessingPattern(inputStacks, outputStacks);
	}

	@Override
	public ItemStack writePattern(IUniversalCraftingPattern pattern, Level level) {
		if (pattern.craftingRecipe() == null) {
			PatternConverter.LOGGER.warn("Cannot write crafting pattern, no recipe defined. Returning empty pattern.");
			return ItemStack.EMPTY;
		}

		return PatternDetailsHelper.encodeCraftingPattern(
			pattern.craftingRecipe(), pattern.getAs3by3().stream().map(UniversalItemIngredient::primary).toArray(ItemStack[]::new),
			pattern.getPrimaryOutputStack(),
			pattern.isGenerallyFuzzy(), pattern.isGenerallyFuzzy());
	}

	@Override
	public ItemStack writePattern(IUniversalStonecutterPattern pattern, Level level) {
		if (pattern.stoneCutterRecipe() == null) {
			PatternConverter.LOGGER.warn("Cannot write stonecutter pattern, no recipe defined. Returning empty pattern.");
			return ItemStack.EMPTY;
		}
		return PatternDetailsHelper.encodeStonecuttingPattern(
			pattern.stoneCutterRecipe(), AEItemKey.of(pattern.input().primary()),
			AEItemKey.of(pattern.getPrimaryOutputStack()),
			pattern.isGenerallyFuzzy());
	}

	@Override
	public ItemStack writePattern(IUniversalSmithingPattern pattern, Level level) {
		if (pattern.smithingRecipe() == null) {
			PatternConverter.LOGGER.warn("Cannot write smithing pattern, no recipe defined. Returning empty pattern.");
			return ItemStack.EMPTY;
		}
		return PatternDetailsHelper.encodeSmithingTablePattern(
			pattern.smithingRecipe(),
			AEItemKey.of(pattern.getTemplate().primary()),
			AEItemKey.of(pattern.getBase().primary()),
			AEItemKey.of(pattern.getAddition().primary()),
			AEItemKey.of(pattern.getPrimaryOutputStack()),
			pattern.isGenerallyFuzzy());
	}
}
