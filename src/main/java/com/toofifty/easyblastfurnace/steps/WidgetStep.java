package com.toofifty.easyblastfurnace.steps;

import lombok.Getter;
import net.runelite.api.widgets.WidgetUtil;

public class WidgetStep extends MethodStep
{
	@Getter
	private final int packedWidgetId;

	public WidgetStep(String tooltip, int interfaceId, int childId)
	{
		super(tooltip);
		this.packedWidgetId = WidgetUtil.packComponentId(interfaceId, childId);
	}
}
