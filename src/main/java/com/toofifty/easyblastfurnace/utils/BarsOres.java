package com.toofifty.easyblastfurnace.utils;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;

public enum BarsOres
{
    COAL(Varbits.BLAST_FURNACE_COAL, ItemID.COAL),
    COPPER_ORE(Varbits.BLAST_FURNACE_COPPER_ORE, ItemID.COPPER_ORE),
    TIN_ORE(Varbits.BLAST_FURNACE_TIN_ORE, ItemID.TIN_ORE),
    IRON_ORE(Varbits.BLAST_FURNACE_IRON_ORE, ItemID.IRON_ORE),
    MITHRIL_ORE(Varbits.BLAST_FURNACE_MITHRIL_ORE, ItemID.MITHRIL_ORE),
    ADAMANTITE_ORE(Varbits.BLAST_FURNACE_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE),
    RUNITE_ORE(Varbits.BLAST_FURNACE_RUNITE_ORE, ItemID.RUNITE_ORE),
    SILVER_ORE(Varbits.BLAST_FURNACE_SILVER_ORE, ItemID.SILVER_ORE),
    GOLD_ORE(Varbits.BLAST_FURNACE_GOLD_ORE, ItemID.GOLD_ORE),
    BRONZE_BAR(Varbits.BLAST_FURNACE_BRONZE_BAR, ItemID.BRONZE_BAR),
    IRON_BAR(Varbits.BLAST_FURNACE_IRON_BAR, ItemID.IRON_BAR),
    STEEL_BAR(Varbits.BLAST_FURNACE_STEEL_BAR, ItemID.STEEL_BAR),
    MITHRIL_BAR(Varbits.BLAST_FURNACE_MITHRIL_BAR, ItemID.MITHRIL_BAR),
    ADAMANTITE_BAR(Varbits.BLAST_FURNACE_ADAMANTITE_BAR, ItemID.ADAMANTITE_BAR),
    RUNITE_BAR(Varbits.BLAST_FURNACE_RUNITE_BAR, ItemID.RUNITE_BAR),
    SILVER_BAR(Varbits.BLAST_FURNACE_SILVER_BAR, ItemID.SILVER_BAR),
    GOLD_BAR(Varbits.BLAST_FURNACE_GOLD_BAR, ItemID.GOLD_BAR);

    @Getter
    private final int varbit;
    @Getter
    private final int itemID;


    BarsOres(int varbit, int itemID)
    {
        this.varbit = varbit;
        this.itemID = itemID;
    }

    public static int[] getAllIds()
    {
        int[] allItemIds = new int[BarsOres.values().length];
        for (int i = 0; i < BarsOres.values().length; i++) {
            allItemIds[i] = BarsOres.values()[i].itemID;
        }
        return allItemIds;
    }
}
