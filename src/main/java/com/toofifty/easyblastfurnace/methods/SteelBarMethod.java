package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.CoalPer;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class SteelBarMethod extends MetalBarMethod
{
    @Override
    protected MethodStep[] withdrawOre()
    {
        return withdrawIronOre;
    }

    @Override
    public int oreItem()
    {
        return ItemID.IRON_ORE;
    }

    @Override
    protected int barItem()
    {
        return ItemID.STEEL_BAR;
    }

    @Override
    protected int coalPer()
    {
        return CoalPer.IRON.getValue();
    }

    @Override
    public String getName()
    {
        return Strings.STEEL;
    }
}
