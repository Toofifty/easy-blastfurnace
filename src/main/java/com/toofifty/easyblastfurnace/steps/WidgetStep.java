package com.toofifty.easyblastfurnace.steps;

import lombok.Getter;

public class WidgetStep extends MethodStep
{
    @Getter
    private final int componentId;

    public WidgetStep(String tooltip, int componentId)
    {
        super(tooltip);
        this.componentId = componentId;
    }
}
