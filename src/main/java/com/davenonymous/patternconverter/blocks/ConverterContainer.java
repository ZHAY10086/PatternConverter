package com.davenonymous.patternconverter.blocks;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.lib.gui.WidgetBlockEntityContainer;
import com.davenonymous.patternconverter.setup.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ConverterContainer extends WidgetBlockEntityContainer<ConverterBlockEntity> {
	public static int WIDTH = 176;
	public static int HEIGHT = 158;

	public static ResourceLocation SLOTGROUP_PATTERN = PatternConverter.resource("pattern");
	public static ResourceLocation SLOTGROUP_QUEUE = PatternConverter.resource("queue");
	public static ResourceLocation SLOTGROUP_DONE = PatternConverter.resource("done");
	public static ResourceLocation SLOTGROUP_RESULT = PatternConverter.resource("result");


	private final ConverterBlockEntity converter;

	public ConverterContainer(int id, BlockPos pos, Inventory inv, @NotNull Player player) {
		super(ModContainers.CONVERTER_CONTAINER.get(), id, pos, inv, player);
		HEIGHT = 158; // Height of the GUI, including player inventory

		this.layoutPlayerInventorySlots(8, HEIGHT - 84);

		int yOffset = 20;
		converter = (ConverterBlockEntity) player.getCommandSenderWorld().getBlockEntity(pos);

		if(converter != null) {
			int x = WIDTH - 8 - (18 * 3) + 2;

			this.addSlotRange(SLOTGROUP_QUEUE, converter.inventories.queueInventory, 0, 8, yOffset + 0, 3, 18);
			this.addSlotRange(SLOTGROUP_DONE, converter.inventories.doneInventory, 0, 8, yOffset + 20, 3, 18);
			this.addSlotRange(SLOTGROUP_PATTERN, converter.inventories.patternInventory, 0, 66, yOffset + 10, 1, 0);
			this.addSlotBox(SLOTGROUP_RESULT, converter.inventories.resultInventory, 0, x, yOffset + 1, 3, 18, 2, 18);
		}

		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_QUEUE, true);
		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_PATTERN, true);

		this.allowSlotGroupMovement(SLOTGROUP_DONE, SLOTGROUP_PLAYER, false);
		this.allowSlotGroupMovement(SLOTGROUP_RESULT, SLOTGROUP_PLAYER, false);

		this.getSlotsForGroup(SLOTGROUP_DONE).forEach(slot -> slot.setMaxStackSize(1).setBlockManualInsert(true));
		this.getSlotsForGroup(SLOTGROUP_RESULT).forEach(slot -> slot.setMaxStackSize(1).setBlockManualInsert(true));
		this.getSlotsForGroup(SLOTGROUP_QUEUE).forEach(slot -> slot.setMaxStackSize(1));
	}
}
