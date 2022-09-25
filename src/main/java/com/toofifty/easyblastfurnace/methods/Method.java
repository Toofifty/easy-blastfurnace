package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.EasyBlastFurnacePlugin;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.*;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public abstract class Method
{
    // items
    protected final MethodStep fillCoalBag = new ItemStep(Strings.FILL_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG);
    protected final MethodStep refillCoalBag = new ItemStep(Strings.REFILL_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG);
    protected final MethodStep emptyCoalBag = new ItemStep(Strings.EMPTY_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG);
    protected final MethodStep withdrawCoalBag = new ItemStep(Strings.WITHDRAW_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG);

    protected final MethodStep withdrawCoal = new ItemStep(Strings.WITHDRAW_COAL, ItemID.COAL);
    protected final MethodStep withdrawGoldOre = new ItemStep(Strings.WITHDRAW_GOLD_ORE, ItemID.GOLD_ORE);
    protected final MethodStep withdrawIronOre = new ItemStep(Strings.WITHDRAW_IRON_ORE, ItemID.IRON_ORE);
    protected final MethodStep withdrawMithrilOre = new ItemStep(Strings.WITHDRAW_MITHRIL_ORE, ItemID.MITHRIL_ORE);
    protected final MethodStep withdrawAdamantiteOre = new ItemStep(Strings.WITHDRAW_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE);
    protected final MethodStep withdrawRuniteOre = new ItemStep(Strings.WITHDRAW_RUNITE_ORE, ItemID.RUNITE_ORE);

    protected final MethodStep withdrawIceOrSmithsGloves = new ItemStep(Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I);
    protected final MethodStep equipIceOrSmithsGloves = new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I);
    protected final MethodStep withdrawGoldsmithGauntlets = new ItemStep(Strings.WITHDRAW_GOLDSMITH_GAUNTLETS, ItemID.GOLDSMITH_GAUNTLETS);
    protected final MethodStep equipGoldsmithGauntlets = new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GOLDSMITH_GAUNTLETS);
    protected final MethodStep withdrawSmithingCape = new ItemStep(Strings.WITHDRAW_SMITHING_CAPE, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET);
    protected final MethodStep withdrawMaxCape = new ItemStep(Strings.WITHDRAW_MAX_CAPE, ItemID.MAX_CAPE);
    protected final MethodStep equipSmithingCape = new ItemStep(Strings.EQUIP_SMITHING_CAPE, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET);
    protected final MethodStep equipMaxCape = new ItemStep(Strings.EQUIP_MAX_CAPE, ItemID.MAX_CAPE);
    protected final MethodStep depositBarsAndOres = new ItemStep(Strings.DEPOSIT_BARS_AND_ORES, BarsOres.getAllIds());
    protected final MethodStep depositPotions = new ItemStep(Strings.DEPOSIT_POTIONS, ItemID.VIAL, ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4);

    // objects
    protected final MethodStep putOntoConveyorBelt = new ObjectStep(Strings.PUT_ORE_ONTO_CONVEYOR_BELT, EasyBlastFurnacePlugin.CONVEYOR_BELT);
    protected final MethodStep openBank = new ObjectStep(Strings.OPEN_BANK, EasyBlastFurnacePlugin.BANK_CHEST);
    protected final MethodStep collectBars = new ObjectStep(Strings.COLLECT_BARS, EasyBlastFurnacePlugin.BAR_DISPENSER);
    protected final MethodStep waitForBars = new TileStep(Strings.WAIT_FOR_BARS, EasyBlastFurnacePlugin.PICKUP_POSITION);

    public abstract MethodStep next(BlastFurnaceState state);

    public abstract String getName();
}
