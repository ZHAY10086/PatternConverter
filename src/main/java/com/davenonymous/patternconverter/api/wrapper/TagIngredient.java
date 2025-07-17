package com.davenonymous.patternconverter.api.wrapper;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagIngredient {
	public enum Mode {
		NONE,
		ANY,
		ALL
	}

	public ItemStack representativeItem = ItemStack.EMPTY;
	public List<String> tags = new ArrayList<>();
	public Mode mode;
	public long amount = 1;

	public TagIngredient(ItemStack stack, List<String> alternativeTags) {
		this.mode = Mode.ANY;
		this.representativeItem = stack;
		this.tags.addAll(alternativeTags);
	}

	public TagIngredient(ItemStack stack, List<String> alternativeTags, Mode mode) {
		this.mode = mode;
		this.representativeItem = stack;
		this.tags.addAll(alternativeTags);
	}

	public TagIngredient(Mode mode, List<String> tags) {
		this.mode = mode;
		this.tags = tags;
	}

	public TagIngredient(Mode mode, String... tags) {
		this.mode = mode;
		this.tags.addAll(Arrays.asList(tags));
	}

	public TagIngredient(String... tags) {
		this(Mode.ANY, tags);
	}

	public TagIngredient setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public TagIngredient setMode(Mode mode) {
		this.mode = mode;
		return this;
	}

	public TagIngredient setRepresentativeItem(ItemStack representativeItem) {
		this.representativeItem = representativeItem;
		return this;
	}

	public boolean isEmpty() {
		return representativeItem.isEmpty() || amount <= 0 || tags.isEmpty();
	}

	public static TagIngredient EMPTY = new TagIngredient();
}
