package com.toofifty.easyblastfurnace.utils;

import lombok.Getter;

public enum CoalPer
{
	SILVER(0),
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

    public static int getValueFromString(String str)
    {
        String uppercaseStr = str.toUpperCase();
        if (uppercaseStr.contains("SILVER")) return SILVER.getValue();
		if (uppercaseStr.contains("STEEL")) return IRON.getValue();
        if (uppercaseStr.contains("MITHRIL")) return MITHRIL.getValue();
        if (uppercaseStr.contains("ADAMANTITE")) return ADAMANTITE.getValue();
        if (uppercaseStr.contains("RUNITE")) return RUNITE.getValue();
        return 0;
    }
}
