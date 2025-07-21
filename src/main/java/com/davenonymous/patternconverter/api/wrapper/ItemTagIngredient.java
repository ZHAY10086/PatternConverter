package com.davenonymous.patternconverter.api.wrapper;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemTagIngredient {

	public ItemStack representativeItem = ItemStack.EMPTY;
	public List<String> tags = new ArrayList<>();
	public TagMatchingMode mode;
	public long amount = 1;

	public ItemTagIngredient(ItemStack stack, List<String> alternativeTags) {
		this.mode = TagMatchingMode.ANY;
		this.representativeItem = stack;
		this.tags.addAll(alternativeTags);
	}

	public ItemTagIngredient(ItemStack stack, List<String> alternativeTags, TagMatchingMode mode) {
		this.mode = mode;
		this.representativeItem = stack;
		this.tags.addAll(alternativeTags);
	}

	public ItemTagIngredient(TagMatchingMode mode, List<String> tags) {
		this.mode = mode;
		this.tags = tags;
	}

	public ItemTagIngredient(TagMatchingMode mode, String... tags) {
		this.mode = mode;
		this.tags.addAll(Arrays.asList(tags));
	}

	public ItemTagIngredient(String... tags) {
		this(TagMatchingMode.ANY, tags);
	}

	public ItemTagIngredient setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public ItemTagIngredient setMode(TagMatchingMode mode) {
		this.mode = mode;
		return this;
	}

	public ItemTagIngredient setRepresentativeItem(ItemStack representativeItem) {
		this.representativeItem = representativeItem;
		return this;
	}

	public boolean isEmpty() {
		return representativeItem.isEmpty() || amount <= 0 || tags.isEmpty();
	}

	public static ItemTagIngredient EMPTY = new ItemTagIngredient();
}
