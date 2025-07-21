package com.davenonymous.patternconverter.mods;

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
import com.davenonymous.patternconverter.api.wrapper.FluidTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.ItemTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalFluidIngredient;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import com.refinedmods.refinedstorage.api.autocrafting.Ingredient;
import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
import com.refinedmods.refinedstorage.common.autocrafting.CraftingPatternState;
import com.refinedmods.refinedstorage.common.autocrafting.ProcessingPatternState;
import com.refinedmods.refinedstorage.common.autocrafting.SmithingTablePatternState;
import com.refinedmods.refinedstorage.common.autocrafting.StonecutterPatternState;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.content.DataComponents;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PatternConverterSupport(modid = "refinedstorage")
public class RefinedStorage implements IPatternConverter {
	@Override
	public Item patternItem() {
		return Items.INSTANCE.getPattern();
	}

	@Override
	public Optional<net.minecraft.resources.ResourceKey<CreativeModeTab>> creativeTab() {
		ResourceLocation rfTabId = RefinedStorageApi.INSTANCE.getCreativeModeTabId();
		CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(rfTabId);
		if (tab == null) {
			PatternConverter.LOGGER.warn("Refined Storage creative mode tab not found: " + rfTabId);
			return Optional.empty();
		}
		return BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab);
	}

	@Override
	public boolean isEmptyPattern(ItemStack stack, Level level) {
		if(!(stack.getItem() instanceof PatternProviderItem patternItem)) {
			return false;
		}

		var pattern = patternItem.getPattern(stack, level);
		var output = patternItem.getOutput(stack, level);

		return pattern.isEmpty() && output.isEmpty();
	}

	public UniversalCraftingPattern readCraftingPattern(ItemStack stack, List<Ingredient> inputs, Level level) {
		CraftingPatternState craftingState = stack.get(DataComponents.INSTANCE.getCraftingPatternState());
		if (craftingState == null) {
			return null;
		}

		CraftingInput craftingInput = craftingState.input().input();

		var result = new UniversalCraftingPattern(craftingInput);
		result.setAllItemsFuzzy(craftingState.fuzzyMode());
		result.setPatternHeight(craftingInput.height());
		result.setPatternWidth(craftingInput.width());

		return result;
	}

	public UniversalSmithingPattern readSmithingPattern(ItemStack stack, List<Ingredient> inputs, Level level) {
		SmithingTablePatternState smithingState = stack.get(DataComponents.INSTANCE.getSmithingTablePatternState());
		if (smithingState == null) {
			return null;
		}

		var result = new UniversalSmithingPattern(
			smithingState.template().toItemStack(),
			smithingState.base().toItemStack(),
			smithingState.addition().toItemStack()
		);
		return result;
	}

	public UniversalStonecutterPattern readStonecutterPattern(ItemStack stack, List<Ingredient> inputs, Level level) {
		StonecutterPatternState stonecutterState = stack.get(DataComponents.INSTANCE.getStonecutterPatternState());
		if (stonecutterState == null) {
			return null;
		}

		var result = new UniversalStonecutterPattern(stonecutterState.input().toItemStack());
		return result;
	}

	public UniversalProcessingPattern readProcessingPattern(ItemStack stack, List<Ingredient> inputs, Level level) {
		ProcessingPatternState processingState = stack.get(DataComponents.INSTANCE.getProcessingPatternState());
		if (processingState == null) {
			return null;
		}

		var result = new UniversalProcessingPattern();
		for(var processingIngredient : processingState.ingredients().stream().filter(Optional::isPresent).map(Optional::get).toList()) {
			ResourceKey resourceKey = processingIngredient.input().resource();
			long amount = processingIngredient.input().amount();
			if(resourceKey instanceof ItemResource itemResource) {
				ItemStack itemStack = itemResource.toItemStack(amount);
				if(processingIngredient.allowedAlternativeIds().isEmpty()) {
					result.addInput(itemStack);
					continue;
				}
				ItemTagIngredient tagIngredient = new ItemTagIngredient(itemStack, processingIngredient.allowedAlternativeIds().stream().map(ResourceLocation::toString).toList());
				tagIngredient.setAmount(amount);
				result.addInput(tagIngredient);
			} else if(resourceKey instanceof FluidResource fluidResource) {
				FluidStack fluidStack = new FluidStack(fluidResource.fluid(), (int)amount);
				if(processingIngredient.allowedAlternativeIds().isEmpty()) {
					result.addInput(fluidStack);
					continue;
				}

				FluidTagIngredient tagIngredient = new FluidTagIngredient(fluidStack, processingIngredient.allowedAlternativeIds().stream().map(ResourceLocation::toString).toList());
				tagIngredient.setAmount(amount);
				result.addInput(tagIngredient);
			}
		}

		return result;
	}

	@Override
	public IUniversalPattern readPattern(ItemStack stack, Level level) {
		if(!(stack.getItem() instanceof PatternProviderItem patternItem)) {
			return null;
		}

		Optional<Pattern> optPattern = patternItem.getPattern(stack, level);
		Optional<ItemStack> optOutput = patternItem.getOutput(stack, level);
		if (optPattern.isEmpty() || optOutput.isEmpty()) {
			return null;
		}

		Pattern pattern = optPattern.get();
		List<Ingredient> inputs = pattern.layout().ingredients();
		List<ResourceAmount> outputs = pattern.layout().outputs();

		IUniversalPattern result = readCraftingPattern(stack, inputs, level);
		if(result == null) {
			result = readSmithingPattern(stack, inputs, level);
		}
		if(result == null) {
			result = readStonecutterPattern(stack, inputs, level);
		}
		if(result == null) {
			result = readProcessingPattern(stack, inputs, level);
		}

		if(result == null) {
			PatternConverter.LOGGER.warn("Could not read pattern from stack: " + stack);
			return null;
		}

		int outputIndex = 0;
		for(ResourceAmount output : outputs) {
			ResourceKey resourceKey = output.resource();
			long amount = output.amount();
			if(resourceKey instanceof ItemResource itemResource) {
				ItemStack itemStack = itemResource.toItemStack(amount);
				result.addOutput(itemStack);
			} else if(result instanceof IUniversalProcessingPattern processingResult && resourceKey instanceof FluidResource fluidResource) {
				FluidStack fluidStack = new FluidStack(fluidResource.fluid(), (int)amount);
				processingResult.addOutput(fluidStack);
			} else {
				PatternConverter.LOGGER.warn("Unknown resource type for output {}: {}", outputIndex, resourceKey);
			}

			outputIndex++;
		}

		if(result instanceof UniversalCraftingPattern craftingPattern) {
			craftingPattern.guessRecipe(level);
		} else if(result instanceof UniversalStonecutterPattern stonecutterPattern) {
			stonecutterPattern.guessRecipe(level);
		} else if(result instanceof UniversalSmithingPattern smithingPattern) {
			smithingPattern.guessRecipe(level);
		}

		return result;
	}

	@Override
	public ItemStack writePattern(IUniversalProcessingPattern pattern, Level level) {
		ItemStack unencodedPattern = PatternGridBlockEntity.createPatternStack(PatternType.PROCESSING);

		List<Optional<ProcessingPatternState.ProcessingIngredient>> ingredients = new ArrayList<>();
		for(UniversalItemIngredient input : pattern.inputIngredients()) {
			if(input.isStack()) {
				ItemStack stack = input.stack();
				var resourceAmount = new ResourceAmount(new ItemResource(stack.getItem()), stack.getCount());
				ingredients.add(Optional.of(new ProcessingPatternState.ProcessingIngredient(resourceAmount, List.of())));
			} else if(input.isIngredient()) {
				var stack = input.ingredient().getItems()[0];
				var resourceAmount = new ResourceAmount(new ItemResource(stack.getItem()), stack.getCount());
				ingredients.add(Optional.of(new ProcessingPatternState.ProcessingIngredient(resourceAmount, List.of())));
			} else if(input.isTagIngredient()) {
				var tagIngredient = input.tagIngredient();
				var resourceAmount = new ResourceAmount(new ItemResource(input.primary().getItem()), tagIngredient.amount);
				ingredients.add(Optional.of(new ProcessingPatternState.ProcessingIngredient(resourceAmount, tagIngredient.tags.stream().map(ResourceLocation::parse).toList())));
			} else {
				ingredients.add(Optional.empty());
			}
		}

		for(UniversalFluidIngredient input : pattern.inputUniversalFluids()) {
			if(input.isStack()) {
				var stack = input.stack();
				var resourceAmount = new ResourceAmount(new FluidResource(stack.getFluid()), stack.getAmount());
				ingredients.add(Optional.of(new ProcessingPatternState.ProcessingIngredient(resourceAmount, List.of())));
			} else if(input.isTagIngredient()) {
				var tagIngredient = input.tagIngredient();
				var resourceAmount = new ResourceAmount(new FluidResource(input.primary().getFluid()), tagIngredient.amount);
				ingredients.add(Optional.of(new ProcessingPatternState.ProcessingIngredient(resourceAmount, tagIngredient.tags.stream().map(ResourceLocation::parse).toList())));
			} else {
				ingredients.add(Optional.empty());
			}
		}

		List<Optional<ResourceAmount>> outputs = new ArrayList<>();
		for(UniversalItemIngredient output : pattern.outputIngredients()) {
			var outputItem = output.primary();
			if(outputItem.isEmpty()) {
				continue;
			}
			outputs.add(Optional.of(new ResourceAmount(new ItemResource(outputItem.getItem()), outputItem.getCount())));
		}

		for(FluidStack outputFluid : pattern.outputFluids()) {
			if(outputFluid.isEmpty()) {
				continue;
			}
			outputs.add(Optional.of(new ResourceAmount(new FluidResource(outputFluid.getFluid()), outputFluid.getAmount())));
		}

		ProcessingPatternState patternState = new ProcessingPatternState(ingredients, outputs);
		unencodedPattern.set(DataComponents.INSTANCE.getProcessingPatternState(), patternState);
		return unencodedPattern.copy();
	}

	@Override
	public ItemStack writePattern(IUniversalCraftingPattern pattern, Level level) {
		ItemStack unencodedPattern = PatternGridBlockEntity.createPatternStack(PatternType.CRAFTING);
		CraftingPatternState state = new CraftingPatternState(pattern.areAllItemsFuzzy(), new CraftingInput.Positioned(pattern.createCraftingInput(), 0, 0));
		unencodedPattern.set(DataComponents.INSTANCE.getCraftingPatternState(), state);
		return unencodedPattern.copy();
	}

	@Override
	public ItemStack writePattern(IUniversalStonecutterPattern pattern, Level level) {
		ItemStack unencodedPattern = PatternGridBlockEntity.createPatternStack(PatternType.STONECUTTER);
		StonecutterPatternState state = new StonecutterPatternState(
			ItemResource.ofItemStack(pattern.input().primary()),
			ItemResource.ofItemStack(pattern.getPrimaryOutputStack())
		);
		unencodedPattern.set(DataComponents.INSTANCE.getStonecutterPatternState(), state);
		return unencodedPattern.copy();
	}

	@Override
	public ItemStack writePattern(IUniversalSmithingPattern pattern, Level level) {
		ItemStack unencodedPattern = PatternGridBlockEntity.createPatternStack(PatternType.SMITHING_TABLE);
		SmithingTablePatternState state = new SmithingTablePatternState(
			ItemResource.ofItemStack(pattern.getTemplate().primary()),
			ItemResource.ofItemStack(pattern.getBase().primary()),
			ItemResource.ofItemStack(pattern.getAddition().primary())
		);
		unencodedPattern.set(DataComponents.INSTANCE.getSmithingTablePatternState(), state);
		return unencodedPattern.copy();
	}
}
