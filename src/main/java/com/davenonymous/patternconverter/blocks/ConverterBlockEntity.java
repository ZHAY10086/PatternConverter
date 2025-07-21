package com.davenonymous.patternconverter.blocks;


import com.davenonymous.patternconverter.lib.BaseBlockEntity;
import com.davenonymous.patternconverter.lib.gui.RedstoneMode;
import com.davenonymous.patternconverter.mods.PluginLoader;
import com.davenonymous.patternconverter.setup.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ConverterBlockEntity extends BaseBlockEntity {
	public final ConverterInventories inventories;

	private RedstoneMode redstoneMode = RedstoneMode.STOP_ON_POWER;
	boolean clearPatterns = false;

	public ConverterBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.CONVERTER_ENTITY.get(), pos, blockState);
		this.inventories = new ConverterInventories(this, this::onInventoryChange);
	}

	private void onInventoryChange(ItemStackHandler handler, int slot) {
		setChanged();

		if(handler == inventories.patternInventory) {
			var patternStack = handler.getStackInSlot(0);
			if(patternStack.isEmpty()) {
				return;
			}

			var level = getLevel();
			if(level == null) {
				return; // Not loaded yet
			}

			var style = PluginLoader.getPatternConverterStyle(patternStack, level);
			if(getBlockState().getValue(ConverterBlock.STYLE) != style) {
				var newState = getBlockState().setValue(ConverterBlock.STYLE, style);
				level.setBlockAndUpdate(getBlockPos(), newState);
			}
		}

		notifyClients(false);
	}

	public ConverterBlockEntity setRedstoneMode(RedstoneMode redstoneMode) {
		this.redstoneMode = redstoneMode;
		setChanged();
		notifyClients(false);
		return this;
	}

	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	public void tick() {
		if(!this.getRedstoneMode().resolve(this.getLevel(), this.getBlockPos())) {
			return;
		}

		ItemStack blankPatterns = inventories.patternInventory.getStackInSlot(0);
		if(blankPatterns.isEmpty()) {
			return;
		}

		var optWritePatternHandler = PluginLoader.getPatternConverter(blankPatterns, this.getLevel());
		if(optWritePatternHandler.isEmpty()) {
			return;
		}

		var writePatternHandler = optWritePatternHandler.get();
		if(!writePatternHandler.canWrite()) {
			return;
		}

		for(int queueSlot = 0; queueSlot < inventories.queueInventory.getSlots(); queueSlot++) {
			ItemStack queueStack = inventories.queueInventory.getStackInSlot(queueSlot);
			if(queueStack.isEmpty()) {
				continue;
			}

			var optPatternHandler = PluginLoader.getPatternConverter(queueStack, this.getLevel());
			if(optPatternHandler.isEmpty()) {
				continue; // No pattern handler for this item
			}

			var patternHandler = optPatternHandler.get();
			if(!patternHandler.canRead()) {
				continue; // The pattern handler cannot read patterns
			}

			var universalPattern = patternHandler.readPattern(queueStack, this.getLevel());
			if(universalPattern == null) {
				continue; // Unable to read pattern
			}

			ItemStack resultStack = writePatternHandler.writePattern(universalPattern, this.getLevel());
			if(resultStack == null || resultStack.isEmpty()) {
				continue; // No result to write
			}

			var originalRecipeSim = inventories.queueInventory.extractItem(queueSlot, 1, true);
			if(originalRecipeSim.isEmpty()) {
				continue; // No recipe to convert
			}

			if(clearPatterns) {
				originalRecipeSim = new ItemStack(patternHandler.patternItem());
			}

			var insertedSim = ItemHandlerHelper.insertItemStacked(inventories.doneInventory, originalRecipeSim, true);
			if(!insertedSim.isEmpty()) {
				continue; // Not enough space in the done inventory
			}
			var insertedResultSim = ItemHandlerHelper.insertItem(inventories.resultInventory, resultStack, true);
			if(!insertedResultSim.isEmpty()) {
				continue; // Not enough space in the result inventory
			}
			var removedPattern = inventories.patternInventory.extractItem(0, 1, false);
			if(removedPattern.isEmpty() || removedPattern.getCount() != 1) {
				continue; // No pattern to write to
			}

			var originalRecipe = inventories.queueInventory.extractItem(queueSlot, 1, false);
			if(clearPatterns) {
				originalRecipe = new ItemStack(patternHandler.patternItem());
			}
			ItemHandlerHelper.insertItemStacked(inventories.doneInventory, originalRecipe, false);
			ItemHandlerHelper.insertItem(inventories.resultInventory, resultStack, false);
		}

	}


	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		super.onDataPacket(net, pkt, lookupProvider);

		if(level == null) {
			return;
		}

		if(level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
			if(Minecraft.getInstance().screen instanceof ConverterScreen converterScreen) {
				converterScreen.fireDataUpdateEvent();
			}
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		inventories.deserializeNBT(registries, tag.getCompound("inventories"));
		redstoneMode = RedstoneMode.byId(tag.getInt("redstoneMode"));
		if(tag.contains("clearPatterns")) {
			this.clearPatterns = tag.getBoolean("clearPatterns");
		} else {
			this.clearPatterns = false;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put("inventories", inventories.serializeNBT(registries));
		tag.putInt("redstoneMode", redstoneMode.getId());
		if(clearPatterns) {
			tag.putBoolean("clearPatterns", true);
		}
	}

	public boolean shouldClearPatterns() {
		return clearPatterns;
	}

	public ConverterBlockEntity setClearPatterns(boolean clearPatterns) {
		this.clearPatterns = clearPatterns;
		setChanged();
		notifyClients(false);
		return this;
	}

	public static IItemHandler getCapability(Level level, BlockPos pos, BlockState state, BlockEntity entity, Direction side) {
		if(!(entity instanceof ConverterBlockEntity converter)) {
			return null;
		}

		if(side == null) {
			// If the side is null, we return the accessible inventories
			return converter.inventories.accessibleInventories;
		}

		switch(side) {
			case UP -> {
				return converter.inventories.inputInventories;
			}
			case DOWN -> {
				return converter.inventories.outputInventories;
			}
			default -> {
				return converter.inventories.accessibleInventories;
			}
		}
	}
}
