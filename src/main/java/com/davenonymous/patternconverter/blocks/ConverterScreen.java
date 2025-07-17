package com.davenonymous.patternconverter.blocks;


import com.davenonymous.patternconverter.gui.WidgetPatternMode;
import com.davenonymous.patternconverter.lib.gui.GUI;
import com.davenonymous.patternconverter.lib.gui.WidgetContainerScreen;
import com.davenonymous.patternconverter.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.patternconverter.lib.gui.event.MouseClickEvent;
import com.davenonymous.patternconverter.lib.gui.event.ValueChangedEvent;
import com.davenonymous.patternconverter.lib.gui.event.WidgetEventResult;
import com.davenonymous.patternconverter.lib.gui.widgets.WidgetItemStack;
import com.davenonymous.patternconverter.lib.gui.widgets.WidgetProgressArrow;
import com.davenonymous.patternconverter.lib.gui.widgets.WidgetRedstoneMode;
import com.davenonymous.patternconverter.networking.SetClearPatterns;
import com.davenonymous.patternconverter.networking.SetRedstoneMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ConverterScreen extends WidgetContainerScreen<ConverterContainer> {
	public ConverterScreen(ConverterContainer container, Inventory inv, Component name) {
		super(container, inv, name);
	}
	private List<Item> allSoilsSorted;

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, ConverterContainer.WIDTH, ConverterContainer.HEIGHT);
		gui.setContainer(this.menu);

		ConverterBlockEntity converter = this.menu.getBlockEntity();

		var redstoneModeToggle = new WidgetRedstoneMode(converter.getRedstoneMode());
		redstoneModeToggle.setPosition(ConverterContainer.WIDTH - 20, 2);
		redstoneModeToggle.addListener(
			ValueChangedEvent.class, (event, widget) -> {
			PacketDistributor.sendToServer(new SetRedstoneMode(converter.getBlockPos(), redstoneModeToggle.getValue()));
			return WidgetEventResult.HANDLED;
		});
		gui.add(redstoneModeToggle);

		var clearPatternsToggle = new WidgetPatternMode(converter.clearPatterns);
		clearPatternsToggle.setPosition(ConverterContainer.WIDTH - 36, 5);
		clearPatternsToggle.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				PacketDistributor.sendToServer(new SetClearPatterns(converter.getBlockPos(), clearPatternsToggle.getValue()));
				return WidgetEventResult.HANDLED;
			});
		gui.add(clearPatternsToggle);

		var progressArrow = new WidgetProgressArrow();
		progressArrow.setValue(100D);
		progressArrow.setPosition(88, 30);
		gui.add(progressArrow);

		gui.addListener(
			GuiDataUpdatedEvent.class, (event, widget) -> {
			boolean isActive = converter.getRedstoneMode().resolve(converter.getLevel(), converter.getBlockPos());
			redstoneModeToggle.updateToolTips();
			redstoneModeToggle.addTooltipLine(
				isActive
				? Component.translatable("patternconverter.redstone_mode.redstone_active").withStyle(ChatFormatting.DARK_GREEN)
				: Component.translatable("patternconverter.redstone_mode.redstone_inactive").withStyle(ChatFormatting.RED)
			);
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.fireDataUpdateEvent();

		return gui;
	}
}
