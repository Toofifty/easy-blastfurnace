package com.toofifty.easyblastfurnace.utils;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;

public enum Equipment {
    GOLDSMITH(new int[] {
        ItemID.GAUNTLETS_OF_GOLDSMITHING
    }),

    SMITHING_CAPE(new int[] {
        ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED
    }),
    ICE_GLOVES(new int[] {
        ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE
    }),
    MAX_CAPE(new int[] {
        ItemID.SKILLCAPE_MAX, ItemID.SKILLCAPE_MAX_WORN,
    }),
	SKILLING_CAPE(
		merge(SMITHING_CAPE.items, MAX_CAPE.items)
	),
    COAL_BAG(new int[] {
        ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN
    });

    public final int[] items;

    Equipment(int[] items)
    {
        this.items = items;
    }

    public static int[] merge(int[]... arrays)
    {
        return Arrays.stream(arrays).flatMapToInt(Arrays::stream).toArray();
    }

    public static boolean hasGoldsmithEquipment(BlastFurnaceState state)
    {
        int[] goldsmithEquipment = merge(GOLDSMITH.items, MAX_CAPE.items, SMITHING_CAPE.items);
        return state.getInventory().has(goldsmithEquipment) || state.getBank().has(goldsmithEquipment) || state.getEquipment().equipped(goldsmithEquipment);
    }

    public static boolean hasCoalBag(BlastFurnaceState state)
    {
        return state.getInventory().has(COAL_BAG.items) || state.getBank().has(COAL_BAG.items);
    }
}
