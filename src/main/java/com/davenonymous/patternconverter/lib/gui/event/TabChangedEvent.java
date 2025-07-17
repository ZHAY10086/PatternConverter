package com.davenonymous.patternconverter.lib.gui.event;

import com.davenonymous.patternconverter.lib.gui.widgets.WidgetPanel;

public class TabChangedEvent extends ValueChangedEvent<WidgetPanel> {
	public TabChangedEvent(WidgetPanel oldValue, WidgetPanel newValue) {
		super(oldValue, newValue);
	}
}
