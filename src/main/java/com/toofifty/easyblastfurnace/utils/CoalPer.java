package com.toofifty.easyblastfurnace.utils;

import lombok.Getter;

public enum CoalPer
{
    IRON(1),
    MITHRIL(2),
    ADAMANTITE(3),
    RUNITE(4);

    @Getter
    private final int value;

    CoalPer(int value)
    {
        this.value = value;
    }
}
