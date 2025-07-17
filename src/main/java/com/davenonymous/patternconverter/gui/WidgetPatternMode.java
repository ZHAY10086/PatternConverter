package com.davenonymous.patternconverter.gui;

import com.davenonymous.patternconverter.lib.gui.Icons;
import com.davenonymous.patternconverter.lib.gui.event.ValueChangedEvent;
import com.davenonymous.patternconverter.lib.gui.event.WidgetEventResult;
import com.davenonymous.patternconverter.lib.gui.widgets.WidgetIconSelect;
import net.minecraft.network.chat.Component;


public class WidgetPatternMode extends WidgetIconSelect<Boolean> {

	public WidgetPatternMode() {
		this(false);
	}

	public WidgetPatternMode(boolean initial) {
		super();
		this.setSize(16, 12);
		this.setTextureSize(16, 12);

		this.addChoiceWithSprite(true, new IconData(Icons.clearPatterns, 16, 12));
		this.addChoiceWithSprite(false, new IconData(Icons.keepPatterns, 16, 12));
		this.setValue(initial);
		updateToolTips();

		this.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				updateToolTips();
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
	}

	public void updateToolTips() {
		String translationKey = this.getValue() ? "patternconverter.pattern_mode.clear_original" : "patternconverter.pattern_mode.keep_original";
		this.setTooltipLines(Component.translatable(translationKey));
	}
}
