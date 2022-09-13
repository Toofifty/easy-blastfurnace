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
    protected final MethodStep fillCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.FILLCOALBAG.getTxt());
    protected final MethodStep refillCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.REFILLCOALBAG.getTxt());
    protected final MethodStep emptyCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.EMPTYCOALBAG.getTxt());
    protected final MethodStep withdrawCoalBag = new ItemStep(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG, Strings.WITHDRAWCOALBAG.getTxt());

    protected final MethodStep withdrawCoal = new ItemStep(ItemID.COAL, Strings.WITHDRAWCOAL.getTxt());
    protected final MethodStep withdrawGoldOre = new ItemStep(ItemID.GOLD_ORE, Strings.WITHDRAWGOLDORE.getTxt());
    protected final MethodStep withdrawIronOre = new ItemStep(ItemID.IRON_ORE, Strings.WITHDRAWIRONORE.getTxt());
    protected final MethodStep withdrawMithrilOre = new ItemStep(ItemID.MITHRIL_ORE, Strings.WITHDRAWMITHRILORE.getTxt());
    protected final MethodStep withdrawAdamantiteOre = new ItemStep(ItemID.ADAMANTITE_ORE, Strings.WITHDRAWADAMANTITEORE.getTxt());
    protected final MethodStep withdrawRuniteOre = new ItemStep(ItemID.RUNITE_ORE, Strings.WITHDRAWRUNITEORE.getTxt());

    protected final MethodStep withdrawIceOrSmithsGloves = new ItemStep(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I, Strings.WITHDRAWICEORSMITHSGLOVES.getTxt());
    protected final MethodStep equipIceOrSmithsGloves = new ItemStep(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I, Strings.EQUIPICEORSMITHSGLOVES.getTxt());
    protected final MethodStep withdrawGoldsmithGauntlets = new ItemStep(ItemID.GOLDSMITH_GAUNTLETS, Strings.WITHDRAWGOLDSMITHGAUNTLETS.getTxt());
    protected final MethodStep equipGoldsmithGauntlets = new ItemStep(ItemID.GOLDSMITH_GAUNTLETS, Strings.EQUIPGOLDSMITHGAUNTLETS.getTxt());
    protected final MethodStep withdrawSmithingCape = new ItemStep(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, Strings.WITHDRAWSMITHINGCAPE.getTxt());
    protected final MethodStep equipSmithingCape = new ItemStep(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, Strings.EQUIPSMITHINGCAPE.getTxt());

    // objects
    protected final MethodStep depositInventory = new WidgetStep(WidgetInfo.BANK_DEPOSIT_INVENTORY, Strings.DEPOSITINVENTORY.getTxt());
    protected final MethodStep putOntoConveyorBelt = new ObjectStep(EasyBlastFurnacePlugin.CONVEYOR_BELT, Strings.PUTONTOCONVEYORBELT.getTxt());
    protected final MethodStep openBank = new ObjectStep(EasyBlastFurnacePlugin.BANK_CHEST, Strings.OPENBANK.getTxt());
    protected final MethodStep collectBars = new ObjectStep(EasyBlastFurnacePlugin.BAR_DISPENSER, Strings.COLLECTBARS.getTxt());
    protected final MethodStep waitForBars = new TileStep(EasyBlastFurnacePlugin.PICKUP_POSITION, Strings.WAITFORBARS.getTxt());

    abstract public MethodStep next(BlastFurnaceState state);

    abstract public String getName();
}
