package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.EasyBlastFurnacePlugin;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.*;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetInfo;

public abstract class Method
{
    // items
    protected final MethodStep fillCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.FILL_COAL_BAG);
    protected final MethodStep refillCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.REFILL_COAL_BAG);
    protected final MethodStep emptyCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.EMPTY_COAL_BAG);
    protected final MethodStep withdrawCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.WITHDRAW_COAL_BAG);

    protected final MethodStep withdrawCoal = new ItemStep(ItemID.COAL, Strings.WITHDRAW_COAL);
    protected final MethodStep withdrawGoldOre = new ItemStep(ItemID.GOLD_ORE, Strings.WITHDRAW_GOLD_ORE);
    protected final MethodStep withdrawIronOre = new ItemStep(ItemID.IRON_ORE, Strings.WITHDRAW_IRON_ORE);
    protected final MethodStep withdrawMithrilOre = new ItemStep(ItemID.MITHRIL_ORE, Strings.WITHDRAW_MITHRIL_ORE);
    protected final MethodStep withdrawAdamantiteOre = new ItemStep(ItemID.ADAMANTITE_ORE, Strings.WITHDRAW_ADAMANTITE_ORE);
    protected final MethodStep withdrawRuniteOre = new ItemStep(ItemID.RUNITE_ORE, Strings.WITHDRAW_RUNITE_ORE);

    protected final MethodStep withdrawIceOrSmithsGloves = new ItemStep(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I, Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES);
    protected final MethodStep equipIceOrSmithsGloves = new ItemStep(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I, Strings.EQUIP_ICE_OR_SMITHS_GLOVES);
    protected final MethodStep withdrawGoldsmithGauntlets = new ItemStep(ItemID.GOLDSMITH_GAUNTLETS, Strings.WITHDRAW_GOLDSMITH_GAUNTLETS);
    protected final MethodStep equipGoldsmithGauntlets = new ItemStep(ItemID.GOLDSMITH_GAUNTLETS, Strings.EQUIP_GOLDSMITH_GAUNTLETS);
    protected final MethodStep withdrawSmithingCape = new ItemStep(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, Strings.WITHDRAW_SMITHING_CAPE);
    protected final MethodStep equipSmithingCape = new ItemStep(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, Strings.EQUIP_SMITHING_CAPE);

    // objects
    protected final MethodStep depositInventory = new WidgetStep(WidgetInfo.BANK_DEPOSIT_INVENTORY, Strings.DEPOSIT_INVENTORY);
    protected final MethodStep putOntoConveyorBelt = new ObjectStep(EasyBlastFurnacePlugin.CONVEYOR_BELT, Strings.PUT_ORE_ONTO_CONVEYOR_BELT);
    protected final MethodStep openBank = new ObjectStep(EasyBlastFurnacePlugin.BANK_CHEST, Strings.OPEN_BANK);
    protected final MethodStep collectBars = new ObjectStep(EasyBlastFurnacePlugin.BAR_DISPENSER, Strings.COLLECT_BARS);
    protected final MethodStep waitForBars = new TileStep(EasyBlastFurnacePlugin.PICKUP_POSITION, Strings.WAIT_FOR_BARS);

    abstract public MethodStep next(BlastFurnaceState state);

    abstract public String getName();
}
