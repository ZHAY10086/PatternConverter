package com.davenonymous.patternconverter.lib.gui.event;


import com.davenonymous.patternconverter.lib.gui.widgets.Widget;

public interface IWidgetListener<T extends IEvent> {
	WidgetEventResult call(T event, Widget widget);
}
