package com.davenonymous.patternconverter.lib.gui.widgets;


import com.davenonymous.patternconverter.lib.gui.event.MouseClickEvent;
import com.davenonymous.patternconverter.lib.gui.event.MouseReleasedEvent;
import com.davenonymous.patternconverter.lib.gui.event.WidgetEventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;


public class WidgetGhostSlot extends WidgetItemStack {
	public WidgetGhostSlot(ItemStack stack, boolean drawSlot) {
		super(stack, drawSlot);

		this.addListener(
			MouseClickEvent.class, (event, widget) -> {
				if (!widget.enabled) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				ItemStack playerStack = Minecraft.getInstance().player.getInventory().getSelected().copy();
				this.setValue(playerStack);
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);

		this.addListener(
			MouseReleasedEvent.class, ((event, widget) -> {
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

	}
	public WidgetGhostSlot(ItemStack stack) {
		this(stack, true);
	}
}
