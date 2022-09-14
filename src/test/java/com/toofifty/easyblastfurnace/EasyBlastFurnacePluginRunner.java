package com.toofifty.easyblastfurnace;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EasyBlastFurnacePluginRunner
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(EasyBlastFurnacePlugin.class);
        RuneLite.main(args);
    }
}