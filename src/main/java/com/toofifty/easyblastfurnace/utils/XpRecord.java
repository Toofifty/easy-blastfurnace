package com.toofifty.easyblastfurnace.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.gameval.ItemID;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
class XpRecord
{
    private int oreId;
    private int barId;
    private int coalPer;
    private double xp;
	private double gauntletXp;

    private static final List<XpRecord> xpRecords = new ArrayList<>();

    static {
		xpRecords.add(new XpRecord(ItemID.GOLD_ORE, ItemID.GOLD_BAR, 0, 22.5, 56.2));
		xpRecords.add(new XpRecord(ItemID.SILVER_ORE, ItemID.SILVER_BAR, 1, 13.7, 13.7));
		xpRecords.add(new XpRecord(ItemID.IRON_ORE, ItemID.STEEL_BAR, 1, 17.5, 17.5));
		xpRecords.add(new XpRecord(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, 2, 30, 30));
		xpRecords.add(new XpRecord(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, 3, 37.5, 37.5));
		xpRecords.add(new XpRecord(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, 4, 50, 50));
    }

    public static XpRecord get(int id)
    {
        for (XpRecord record : xpRecords) {
            if (record.getOreId() == id || record.getBarId() == id) {
                return record;
            }
        }
        return null;
    }
}