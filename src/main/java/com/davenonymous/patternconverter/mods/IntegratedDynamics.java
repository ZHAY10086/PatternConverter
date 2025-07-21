package com.davenonymous.patternconverter.mods;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.api.*;
import com.davenonymous.patternconverter.api.impl.UniversalSuperPattern;
import com.davenonymous.patternconverter.api.plugin.IPatternConverter;
import com.davenonymous.patternconverter.api.plugin.PatternConverterSupport;
import com.davenonymous.patternconverter.api.types.IUniversalCraftingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalProcessingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalSmithingPattern;
import com.davenonymous.patternconverter.api.types.IUniversalStonecutterPattern;
import com.davenonymous.patternconverter.api.wrapper.ItemTagIngredient;
import com.davenonymous.patternconverter.api.wrapper.TagMatchingMode;
import com.davenonymous.patternconverter.api.wrapper.UniversalItemIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.capability.recipehandler.*;
import org.cyclops.commoncapabilities.api.ingredient.IMixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;
import org.cyclops.integrateddynamics.Capabilities;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.IntegratedDynamicsAPI;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeRegistry;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.item.IVariableFacade;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandler;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHolder;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import org.cyclops.integrateddynamics.core.item.ValueTypeVariableFacade;

import java.util.*;

@PatternConverterSupport(modid = "integrateddynamics")
public class IntegratedDynamics implements IPatternConverter {
	@Override
	public Item patternItem() {
		return RegistryEntries.ITEM_VARIABLE.get();
	}

	@Override
	public Optional<ResourceKey<CreativeModeTab>> creativeTab() {
		ResourceLocation dynamicsTabId = ResourceLocation.fromNamespaceAndPath("integrateddynamics", "default");
		CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(dynamicsTabId);
		if (tab == null) {
			PatternConverter.LOGGER.warn("Integrated Dynamics creative mode tab not found: " + dynamicsTabId);
			return Optional.empty();
		}
		return BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab);
	}

	private IVariableFacade getVariableFacade(ItemStack stack, Level level) {
		IVariableFacadeHolder variableFacadeCap = stack.getCapability(Capabilities.VariableFacade.ITEM);
		if (variableFacadeCap == null) {
			return null;
		}
		return variableFacadeCap.getVariableFacade(ValueDeseralizationContext.of(level));
	}

	@Override
	public boolean isEmptyPattern(ItemStack stack, Level level) {
		IVariableFacade variableFacade = getVariableFacade(stack, level);
		if (variableFacade == null) {
			return true;
		}

		return variableFacade.getId() == -1;
	}

	@Override
	public IUniversalPattern readPattern(ItemStack stack, Level level) {
		IVariableFacade variableFacade = getVariableFacade(stack, level);
		if (variableFacade == null) {
			return null;
		}

		if(!(variableFacade instanceof ValueTypeVariableFacade<?> valueTypeVariableFacade)) {
			return null;
		}

		IValueType<?> outputType = valueTypeVariableFacade.getOutputType();
		if(!(outputType instanceof ValueObjectTypeRecipe)) {
			return null;
		}

		if(!(valueTypeVariableFacade.getValue() instanceof ValueObjectTypeRecipe.ValueRecipe valueRecipe)) {
			return null;
		}

		Optional<IRecipeDefinition> optRecipe = valueRecipe.getRawValue();
		if(optRecipe.isEmpty()) {
			return null;
		}

		IRecipeDefinition recipe = optRecipe.get();
		var result = new UniversalSuperPattern();
		IMixedIngredients output = recipe.getOutput();
		for(var component : output.getComponents()) {
			for(var componentEntry : output.getInstances(component)) {
				if(componentEntry instanceof ItemStack outputStack) {
					result.addOutput(outputStack);
				} else if(componentEntry instanceof FluidStack outputFluid) {
					result.addOutput(outputFluid);
				} else if(component.getName().toString().equals("minecraft:energy") && componentEntry instanceof Long energyAmount) {
					result.addOutputEnergy(energyAmount);
				}
			}
		}

		int slot = 0;
		for(var component : recipe.getInputComponents()) {
			for(IPrototypedIngredientAlternatives<?, ?> componentPrototype : recipe.getInputs(component)) {
				if(componentPrototype instanceof PrototypedIngredientAlternativesItemStackTag itemStackTagComponentPrototype) {
					List<String> tags = itemStackTagComponentPrototype.getKeys();
					TagMatchingMode mode = TagMatchingMode.ANY;
					if(itemStackTagComponentPrototype.getMatchCondition() == tags.size()) {
						mode = TagMatchingMode.ALL;
					} else if(itemStackTagComponentPrototype.getMatchCondition() <= 0) {
						mode = TagMatchingMode.NONE;
					}
					ItemTagIngredient itemTagIngredient = new ItemTagIngredient(mode, tags);
					if(!itemStackTagComponentPrototype.getAlternatives().isEmpty()) {
						var firstAlternative = itemStackTagComponentPrototype.getAlternatives().stream().findFirst();
						if(firstAlternative.isPresent()) {
							var representativeItem = firstAlternative.get().getPrototype();
							if(representativeItem instanceof ItemStack representativeStack) {
								itemTagIngredient.setRepresentativeItem(representativeStack);
							}
						}
					}
					result.addInput(slot++, itemTagIngredient);
					continue;
				}

				var firstAlternative = componentPrototype.getAlternatives().stream().findFirst();
				if(firstAlternative.isEmpty()) {
					continue;
				}

				var componentEntry = firstAlternative.get().getPrototype();
				switch(componentEntry) {
					case ItemStack inputStack -> result.addInput(slot++, inputStack);
					case FluidStack inputFluid -> result.addInput(inputFluid);
					case Long energyAmount when component.getName().toString().equals("minecraft:energy") -> result.addInputEnergy(energyAmount);
					default -> {
						PatternConverter.LOGGER.warn("Unknown input component type: " + componentEntry.getClass().getName() + " with value: " + componentEntry);
						return null;
					}
				}
			}
		}

		return result.guessRecipe(level).orElse(null);
	}

	@Override
	public ItemStack writePattern(IUniversalProcessingPattern pattern, Level level) {
		Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<Boolean>> inputsReusable = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<?>> outputs = new HashMap<>();

		if(pattern.inputEnergy() > 0) {
			var inputList = inputs.computeIfAbsent(IngredientComponent.ENERGY, k -> new ArrayList<>());
			PrototypedIngredient energy = new PrototypedIngredient(IngredientComponent.ENERGY, pattern.inputEnergy(), true);
			PrototypedIngredientAlternativesList energyAlternatives = new PrototypedIngredientAlternativesList(List.of(energy));
			inputList.add(energyAlternatives);
		}

		if(pattern.inputFluids().size() == 1) {
			FluidStack inputFluid = pattern.inputFluids().getFirst();
			if (!inputFluid.isEmpty()) {
				List<IPrototypedIngredientAlternatives<?, ?>> inputList = inputs.computeIfAbsent(IngredientComponent.FLUIDSTACK, k -> new ArrayList<>());
				PrototypedIngredientAlternativesList fluidAlternatives =
					new PrototypedIngredientAlternativesList(List.of(new PrototypedIngredient(IngredientComponent.FLUIDSTACK, inputFluid, 0)));
				inputList.add(fluidAlternatives);
			}
		}

		if(pattern.inputFluids().size() > 1) {
			PatternConverter.LOGGER.warn("Multiple fluid inputs are not supported in Integrated Dynamics patterns. Only the first one will be used.");
		}

		List<IPrototypedIngredientAlternatives<?, ?>> inputList = inputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
		for(UniversalItemIngredient itemIngredient : pattern.inputIngredients()) {
			if(itemIngredient.isTagIngredient()) {
				ItemTagIngredient inputTag = itemIngredient.tagIngredient();
				var tagAlternatives = new PrototypedIngredientAlternativesItemStackTag(inputTag.tags, getMatchCondition(inputTag.mode, inputTag.tags), inputTag.amount);
				inputList.add(tagAlternatives);
			} else {
				var itemAlternatives = new PrototypedIngredientAlternativesList(List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, itemIngredient.primary(), 1)));
				inputList.add(itemAlternatives);
			}
		}

		if(pattern.outputEnergy() > 0) {
			List<Long> outputList = (List<Long>) outputs.computeIfAbsent(IngredientComponent.ENERGY, k -> new ArrayList<>());
			outputList.add(pattern.outputEnergy());
		}

		for(UniversalItemIngredient outputItemIngredient : pattern.outputIngredients()) {
			if(outputItemIngredient.isTagIngredient()) {
				ItemTagIngredient outputTag = outputItemIngredient.tagIngredient();
				var tagAlternatives = new PrototypedIngredientAlternativesItemStackTag(outputTag.tags, getMatchCondition(outputTag.mode, outputTag.tags), outputTag.amount);
				List<IPrototypedIngredientAlternatives<?, ?>> outputList = (List<IPrototypedIngredientAlternatives<?, ?>>) outputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
				outputList.add(tagAlternatives);
			} else {
				List<ItemStack> outputList = (List<ItemStack>) outputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
				outputList.add(outputItemIngredient.primary());
			}
		}

		if(!pattern.outputFluids().isEmpty()) {
			List<FluidStack> outputList = (List<FluidStack>) outputs.computeIfAbsent(IngredientComponent.FLUIDSTACK, k -> new ArrayList<>());
			for (FluidStack outputFluid : pattern.outputFluids()) {
				if (!outputFluid.isEmpty()) {
					outputList.add(outputFluid);
				}
			}
		}

		var recipeDefinition = new RecipeDefinition(inputs, inputsReusable, new MixedIngredients(outputs));
		return writePattern(recipeDefinition, level);
	}

	@Override
	public ItemStack writePattern(IUniversalCraftingPattern pattern, Level level) {
		Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<Boolean>> inputsReusable = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<?>> outputs = new HashMap<>();

		List<IPrototypedIngredientAlternatives<?, ?>> inputList = inputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
		List<UniversalItemIngredient> inputIngredients = new LinkedList<>(pattern.getAs3by3());
		while(!inputIngredients.isEmpty()) {
			UniversalItemIngredient lastIngredient = inputIngredients.removeLast();
			if(!lastIngredient.isEmpty()) {
				inputIngredients.add(lastIngredient);
				break;
			}
		}
		for(UniversalItemIngredient itemIngredient : inputIngredients) {
			if(itemIngredient.isEmpty()) {
				var itemAlternatives = new PrototypedIngredientAlternativesList(List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, ItemStack.EMPTY, 1)));
				inputList.add(itemAlternatives);
			} else if(itemIngredient.isTagIngredient()) {
				ItemTagIngredient inputTag = itemIngredient.tagIngredient();
				var tagAlternatives = new PrototypedIngredientAlternativesItemStackTag(inputTag.tags, getMatchCondition(inputTag.mode, inputTag.tags), inputTag.amount);
				inputList.add(tagAlternatives);
			} else {
				var itemAlternatives = new PrototypedIngredientAlternativesList(List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, itemIngredient.primary(), 1)));
				inputList.add(itemAlternatives);
			}
		}

		List<UniversalItemIngredient> outputIngredients = new LinkedList<>(pattern.outputIngredients());
		while(!outputIngredients.isEmpty()) {
			UniversalItemIngredient lastIngredient = outputIngredients.removeLast();
			if(!lastIngredient.isEmpty()) {
				outputIngredients.add(lastIngredient);
				break;
			}
		}

		for(UniversalItemIngredient outputItemIngredient : outputIngredients) {
			if(outputItemIngredient.isTagIngredient()) {
				ItemTagIngredient outputTag = outputItemIngredient.tagIngredient();
				var tagAlternatives = new PrototypedIngredientAlternativesItemStackTag(outputTag.tags, getMatchCondition(outputTag.mode, outputTag.tags), outputTag.amount);
				List<IPrototypedIngredientAlternatives<?, ?>> outputList = (List<IPrototypedIngredientAlternatives<?, ?>>) outputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
				outputList.add(tagAlternatives);
			} else {
				List<ItemStack> outputList = (List<ItemStack>) outputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
				outputList.add(outputItemIngredient.primary());
			}
		}

		var recipeDefinition = new RecipeDefinition(inputs, inputsReusable, new MixedIngredients(outputs));
		return writePattern(recipeDefinition, level);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public ItemStack writePattern(IUniversalStonecutterPattern pattern, Level level) {
		Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<Boolean>> inputsReusable = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<?>> outputs = new HashMap<>();

		List<IPrototypedIngredientAlternatives<?, ?>> inputList = inputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
		PrototypedIngredientAlternativesList itemAlternatives = new PrototypedIngredientAlternativesList(
			List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, pattern.input().primary(), 0))
		);
		inputList.add(itemAlternatives);

		List<ItemStack> outputList = (List<ItemStack>) outputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
		outputList.add(pattern.getPrimaryOutputStack());

		var recipeDefinition = new RecipeDefinition(inputs, inputsReusable, new MixedIngredients(outputs));
		return writePattern(recipeDefinition, level);
	}

	@Override
	public ItemStack writePattern(IUniversalSmithingPattern pattern, Level level) {
		Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<Boolean>> inputsReusable = new HashMap<>();
		Map<IngredientComponent<?, ?>, List<?>> outputs = new HashMap<>();

		List<IPrototypedIngredientAlternatives<?, ?>> inputList = inputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
		var templateAlternatives = new PrototypedIngredientAlternativesList(
			List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, pattern.getTemplate().primary(), 1))
		);
		inputList.add(templateAlternatives);

		var baseAlternatives = new PrototypedIngredientAlternativesList(
			List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, pattern.getBase().primary(), 1))
		);
		inputList.add(baseAlternatives);

		var additionAlternatives = new PrototypedIngredientAlternativesList(
			List.of(new PrototypedIngredient(IngredientComponent.ITEMSTACK, pattern.getAddition().primary(), 1))
		);
		inputList.add(additionAlternatives);

		List<ItemStack> outputList = (List<ItemStack>) outputs.computeIfAbsent(IngredientComponent.ITEMSTACK, k -> new ArrayList<>());
		outputList.add(pattern.getPrimaryOutputStack());

		var recipeDefinition = new RecipeDefinition(inputs, inputsReusable, new MixedIngredients(outputs));
		return writePattern(recipeDefinition, level);
	}

	private int getMatchCondition(TagMatchingMode mode, List<String> tags) {
		if (mode == TagMatchingMode.NONE) {
			return 0;
		} else if (mode == TagMatchingMode.ANY) {
			return 1;
		} else if (mode == TagMatchingMode.ALL) {
			return tags.size();
		}
		return -1; // Should not happen
	}

	private ItemStack writePattern(IRecipeDefinition recipeDefinition, Level level) {
		IVariableFacadeHandlerRegistry handlerRegistry = IntegratedDynamicsAPI.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class);
		IValueTypeRegistry valueTypeRegistry = (IValueTypeRegistry) handlerRegistry.getHandler(ResourceLocation.fromNamespaceAndPath("integrateddynamics", "valuetype"));
		if(valueTypeRegistry == null) {
			PatternConverter.LOGGER.warn("Integrated Dynamics ValueTypeRegistry not found. Cannot write pattern.");
			return ItemStack.EMPTY;
		}

		ValueObjectTypeRecipe valueObjectTypeRecipe = (ValueObjectTypeRecipe) valueTypeRegistry.getValueType(ResourceLocation.fromNamespaceAndPath("integrateddynamics", "recipe"));

		ValueObjectTypeRecipe.ValueRecipe valueRecipe = ValueObjectTypeRecipe.ValueRecipe.of(recipeDefinition);
		IVariableFacade variableFacade = new ValueTypeVariableFacade<>(true, valueObjectTypeRecipe, valueRecipe);
		//noinspection unchecked,rawtypes
		return handlerRegistry.writeVariableFacadeItem(new ItemStack(this.patternItem()), variableFacade, (IVariableFacadeHandler) valueTypeRegistry);
	}
}
