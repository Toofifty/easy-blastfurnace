package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.CoalPer;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class RuniteHybridMethod extends GoldHybridMethod
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
        return CoalPer.RUNITE.getValue();
    }

    @Override
    public String getName()
    {
        return Strings.RUNITEHYBRID;
    }
}
