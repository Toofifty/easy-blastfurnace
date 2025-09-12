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
        ItemID.SKILLCAPE_MAX,
        ItemID.SKILLCAPE_MAX,
        ItemID.SKILLCAPE_MAX_WORN,
        ItemID.SKILLCAPE_MAX_ARDY,
        ItemID.SKILLCAPE_MAX_ASSEMBLER,
        ItemID.SKILLCAPE_MAX_ASSEMBLER_TROUVER,
        ItemID.SKILLCAPE_MAX_ASSEMBLER_MASORI,
        ItemID.SKILLCAPE_MAX_ASSEMBLER_MASORI_TROUVER,
        ItemID.SKILLCAPE_MAX_FIRECAPE,
        ItemID.SKILLCAPE_MAX_FIRECAPE_TROUVER,
        ItemID.SKILLCAPE_MAX_GUTHIX,
        ItemID.SKILLCAPE_MAX_GUTHIX2,
        ItemID.SKILLCAPE_MAX_GUTHIX2_TROUVER,
        ItemID.SKILLCAPE_MAX_INFERNALCAPE,
        ItemID.SKILLCAPE_MAX_INFERNALCAPE_TROUVER,
        ItemID.SKILLCAPE_MAX_SARADOMIN,
        ItemID.SKILLCAPE_MAX_SARADOMIN2,
        ItemID.SKILLCAPE_MAX_SARADOMIN2_TROUVER,
        ItemID.SKILLCAPE_MAX_ZAMORAK,
        ItemID.SKILLCAPE_MAX_ZAMORAK2,
        ItemID.SKILLCAPE_MAX_ZAMORAK2_TROUVER,
        ItemID.SKILLCAPE_MAX_ANMA,
        ItemID.SKILLCAPE_MAX_MYTHICAL,
        ItemID.SKILLCAPE_MAX_DIZANAS,
        ItemID.SKILLCAPE_MAX_DIZANAS_TROUVER,
    }),
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
