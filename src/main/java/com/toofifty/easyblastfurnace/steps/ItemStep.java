package com.toofifty.easyblastfurnace.steps;

import lombok.Getter;

@Getter
public class ItemStep extends MethodStep
{
    private final int[] itemIds;

    public ItemStep(String tooltip, int ...itemIds)
    {
        super(tooltip);
        this.itemIds = itemIds;
    }
}
