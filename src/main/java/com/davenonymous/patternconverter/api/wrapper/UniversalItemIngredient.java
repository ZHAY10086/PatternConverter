package com.davenonymous.patternconverter.api.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class UniversalItemIngredient {
	public static final UniversalItemIngredient EMPTY = new UniversalItemIngredient(Ingredient.EMPTY);

	ItemStack stack = ItemStack.EMPTY;
	Ingredient ingredient = Ingredient.EMPTY;
	ItemTagIngredient itemTagIngredient = ItemTagIngredient.EMPTY;

	public UniversalItemIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public UniversalItemIngredient(ItemStack stack) {
		this.stack = stack;
	}

	public UniversalItemIngredient(ItemTagIngredient itemTagIngredient) {
		this.itemTagIngredient = itemTagIngredient;
	}

	public boolean isEmpty() {
		return this == EMPTY || stack.isEmpty() && ingredient.isEmpty() && itemTagIngredient.isEmpty();
	}

	public boolean isStack() {
		return !stack.isEmpty();
	}

	public boolean isIngredient() {
		return !ingredient.isEmpty();
	}

	public boolean isTagIngredient() {
		return !itemTagIngredient.isEmpty();
	}

	public Ingredient ingredient() {
		return ingredient;
	}

	public ItemStack stack() {
		return stack;
	}

	public ItemTagIngredient tagIngredient() {
		return itemTagIngredient;
	}

	public ItemStack primary() {
		if(!stack.isEmpty()) {
			return stack.copy();
		} else if(!ingredient.isEmpty()) {
			return ingredient.getItems()[0].copy();
		} else if(!itemTagIngredient.isEmpty()) {
			return itemTagIngredient.representativeItem.copy();
		}

		return ItemStack.EMPTY;
	}
}
