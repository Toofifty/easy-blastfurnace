package com.toofifty.easyblastfurnace.steps;

import lombok.Getter;

@Getter
public class ObjectStep extends MethodStep
{
    private final int objectId;

    public ObjectStep(String tooltip, int objectId)
    {
        super(tooltip);
        this.objectId = objectId;
    }
}
