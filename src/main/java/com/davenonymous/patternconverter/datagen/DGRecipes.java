package com.davenonymous.patternconverter.datagen;


import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.api.plugin.IPatternConverter;
import com.davenonymous.patternconverter.datacomponents.ConverterStyleDataComponent;
import com.davenonymous.patternconverter.mods.ConverterStyle;
import com.davenonymous.patternconverter.mods.PluginLoader;
import com.davenonymous.patternconverter.setup.ModDataComponents;
import com.davenonymous.patternconverter.setup.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.concurrent.CompletableFuture;

public class DGRecipes extends RecipeProvider {
	public DGRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		PluginLoader.loadPlugins();

		ItemStack converterItem = new ItemStack(ModItems.CONVERTER_ITEM.get());
		PluginLoader.converterMap.keySet().forEach(modId -> {
			IPatternConverter patternConverter = PluginLoader.converterMap.get(modId);
			ItemStack recipeOutputStack = converterItem.copy();
			recipeOutputStack.set(ModDataComponents.CONVERTER_STYLE_COMPONENT.get(), new ConverterStyleDataComponent(ConverterStyle.byMod(modId)));

			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, recipeOutputStack)
				.pattern(" c ")
				.pattern("cpc")
				.pattern(" c ")
				.define('c', Items.CLAY)
				.define('p', patternConverter.patternItem())
				.unlockedBy("has_pattern", has(patternConverter.patternItem()))
				.save(recipeOutput.withConditions(new ModLoadedCondition(modId)), PatternConverter.resource("converter_" + modId));
		});
	}
}
