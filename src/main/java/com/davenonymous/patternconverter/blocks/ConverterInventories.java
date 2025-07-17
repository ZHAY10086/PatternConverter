package com.davenonymous.patternconverter.blocks;

import com.davenonymous.patternconverter.mods.PluginLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.BiConsumer;

public class ConverterInventories implements INBTSerializable<CompoundTag> {
	public final ConverterBlockEntity converter;
	private final BiConsumer<ItemStackHandler, Integer> onChangeHandler;

	public ItemStackHandler queueInventory;
	public ItemStackHandler doneInventory;
	public ItemStackHandler patternInventory;
	public ItemStackHandler resultInventory;

	public CombinedInvWrapper inputInventories;
	public CombinedInvWrapper outputInventories;
	public CombinedInvWrapper accessibleInventories;

	public ConverterInventories(ConverterBlockEntity converter, BiConsumer<ItemStackHandler, Integer> onChangeHandler) {
		this.converter = converter;
		this.onChangeHandler = onChangeHandler;

		this.queueInventory = createQueueInventory();
		this.doneInventory = createDoneInventory();
		this.patternInventory = createPatternInventory();
		this.resultInventory = createResultInventory();
		this.inputInventories = createInputInventory();
		this.outputInventories = createOutputInventory();
		this.accessibleInventories = createCombinedInventory();
	}

	private ItemStackHandler createResultInventory() {
		return new ItemStackHandler(6) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				var optPatternHandler = PluginLoader.getPatternConverter(stack, converter.getLevel());
				if(optPatternHandler.isEmpty()) {
					return false;
				}

				var patternHandler = optPatternHandler.get();
				if(!patternHandler.canRead()) {
					return false;
				}

				if(!patternHandler.isPattern(stack, converter.getLevel())) {
					return false;
				}

				if(patternHandler.isEmptyPattern(stack, converter.getLevel())) {
					return false;
				}

				return true;
			}
		};
	}

	private ItemStackHandler createQueueInventory() {
		return new ItemStackHandler(3) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return 1;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				var optPatternHandler = PluginLoader.getPatternConverter(stack, converter.getLevel());
				if(optPatternHandler.isEmpty()) {
					return false;
				}

				var patternHandler = optPatternHandler.get();
				if(!patternHandler.canRead()) {
					return false;
				}

				if(!patternHandler.isPattern(stack, converter.getLevel())) {
					return false;
				}

				if(patternHandler.isEmptyPattern(stack, converter.getLevel())) {
					return false;
				}

				return true;
			}
		};
	}

	private ItemStackHandler createDoneInventory() {
		return new ItemStackHandler(3) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				var optPatternHandler = PluginLoader.getPatternConverter(stack, converter.getLevel());
				if(optPatternHandler.isEmpty()) {
					return false;
				}

				var patternHandler = optPatternHandler.get();
				if(!patternHandler.isPattern(stack, converter.getLevel())) {
					return false;
				}

				if(!converter.clearPatterns && patternHandler.isEmptyPattern(stack, converter.getLevel())) {
					return false;
				}

				return true;
			}
		};
	}

	private ItemStackHandler createPatternInventory() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				var optPatternHandler = PluginLoader.getPatternConverter(stack, converter.getLevel());
				if(optPatternHandler.isEmpty()) {
					return false;
				}

				var patternHandler = optPatternHandler.get();
				if(!patternHandler.canWrite()) {
					return false;
				}

				if(!patternHandler.isPattern(stack, converter.getLevel())) {
					return false;
				}

				return patternHandler.isEmptyPattern(stack, converter.getLevel());
			}
		};
	}

	private CombinedInvWrapper createInputInventory() {
		return new CombinedInvWrapper(
			queueInventory,
			patternInventory
		) {
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}
		};
	}

	private CombinedInvWrapper createOutputInventory() {
		return new CombinedInvWrapper(
			doneInventory,
			resultInventory
		) {
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				return stack;
			}
		};
	}

	private CombinedInvWrapper createCombinedInventory() {
		return new CombinedInvWrapper(
			inputInventories,
			outputInventories
		);
	}


	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		tag.put("queue", queueInventory.serializeNBT(provider));
		tag.put("done", doneInventory.serializeNBT(provider));
		tag.put("pattern", patternInventory.serializeNBT(provider));
		tag.put("result", resultInventory.serializeNBT(provider));
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
		queueInventory.deserializeNBT(provider, compoundTag.getCompound("queue"));
		doneInventory.deserializeNBT(provider, compoundTag.getCompound("done"));
		patternInventory.deserializeNBT(provider, compoundTag.getCompound("pattern"));
		resultInventory.deserializeNBT(provider, compoundTag.getCompound("result"));
	}
}
