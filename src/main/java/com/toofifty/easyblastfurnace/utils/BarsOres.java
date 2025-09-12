package com.toofifty.easyblastfurnace.utils;

import lombok.Getter;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

public enum BarsOres
{
	COAL(VarbitID.BLAST_FURNACE_COAL, ItemID.COAL, 2.267),
	COPPER_ORE(VarbitID.BLAST_FURNACE_COPPER_ORE, ItemID.COPPER_ORE, 2.267),
	TIN_ORE(VarbitID.BLAST_FURNACE_TIN_ORE, ItemID.TIN_ORE, 2.267),
	IRON_ORE(VarbitID.BLAST_FURNACE_IRON_ORE, ItemID.IRON_ORE, 2.267),
	MITHRIL_ORE(VarbitID.BLAST_FURNACE_MITHRIL_ORE, ItemID.MITHRIL_ORE, 1.814),
	ADAMANTITE_ORE(VarbitID.BLAST_FURNACE_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE, 2.721),
	RUNITE_ORE(VarbitID.BLAST_FURNACE_RUNITE_ORE, ItemID.RUNITE_ORE, 2.267),
	SILVER_ORE(VarbitID.BLAST_FURNACE_SILVER_ORE, ItemID.SILVER_ORE, 2.267),
	GOLD_ORE(VarbitID.BLAST_FURNACE_GOLD_ORE, ItemID.GOLD_ORE, 2.267),
	BRONZE_BAR(VarbitID.BLAST_FURNACE_BRONZE_BARS, ItemID.BRONZE_BAR, 1.814),
	IRON_BAR(VarbitID.BLAST_FURNACE_IRON_BARS, ItemID.IRON_BAR, 1.814),
	STEEL_BAR(VarbitID.BLAST_FURNACE_STEEL_BARS, ItemID.STEEL_BAR, 1.814),
	MITHRIL_BAR(VarbitID.BLAST_FURNACE_MITHRIL_BARS, ItemID.MITHRIL_BAR, 1.587),
	ADAMANTITE_BAR(VarbitID.BLAST_FURNACE_ADAMANTITE_BARS, ItemID.ADAMANTITE_BAR, 2.041),
	RUNITE_BAR(VarbitID.BLAST_FURNACE_RUNITE_BARS, ItemID.RUNITE_BAR, 1.814),
	SILVER_BAR(VarbitID.BLAST_FURNACE_SILVER_BARS, ItemID.SILVER_BAR, 1.814),
	GOLD_BAR(VarbitID.BLAST_FURNACE_GOLD_BARS, ItemID.GOLD_BAR, 1.814);

    @Getter
    private final int varbit;
    @Getter
    private final int itemID;
    @Getter
    private final double weight;

    BarsOres(int varbit, int itemID, double weight)
    {
        this.varbit = varbit;
        this.itemID = itemID;
        this.weight = weight;
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
