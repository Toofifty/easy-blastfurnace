package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.CoalPer;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.gameval.ItemID;

public class AdamantiteGoldHybridMethod extends GoldHybridMethod
{
    @Override
    protected MethodStep[] withdrawOre()
    {
        return withdrawAdamantiteOre;
    }

    @Override
    public int oreItem()
    {
        return ItemID.ADAMANTITE_ORE;
    }

    @Override
    protected int barItem()
    {
        return ItemID.ADAMANTITE_BAR;
    }

    @Override
    protected int coalPer()
    {
        return CoalPer.ADAMANTITE.getValue();
    }

    @Override
    public String getName()
    {
        return Strings.ADAMANTITE_GOLD_HYBRID;
    }
}
