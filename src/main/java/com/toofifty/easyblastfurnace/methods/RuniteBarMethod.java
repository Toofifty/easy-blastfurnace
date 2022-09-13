package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class RuniteBarMethod extends MetalBarMethod
{
    @Override
    protected MethodStep withdrawOre()
    {
        return withdrawRuniteOre;
    }

    @Override
    public int oreItem()
    {
        return ItemID.RUNITE_ORE;
    }

    @Override
    protected int barItem()
    {
        return ItemID.RUNITE_BAR;
    }

    @Override
    protected int coalPer()
    {
        return 4;
    }

    @Override
    public String getName()
    {
        return Strings.RUNITE.getTxt();
    }
}
