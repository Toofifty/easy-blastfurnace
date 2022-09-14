package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.CoalPer;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class MithrilHybridMethod extends GoldHybridMethod
{
    @Override
    protected MethodStep withdrawOre()
    {
        return withdrawMithrilOre;
    }

    @Override
    public int oreItem()
    {
        return ItemID.MITHRIL_ORE;
    }

    @Override
    protected int barItem()
    {
        return ItemID.MITHRIL_BAR;
    }

    @Override
    protected int coalPer()
    {
        return CoalPer.MITHRIL.getValue();
    }

    @Override
    public String getName()
    {
        return Strings.MITHRILHYBRID.getTxt();
    }
}
