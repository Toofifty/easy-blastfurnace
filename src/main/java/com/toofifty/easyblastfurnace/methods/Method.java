package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.EasyBlastFurnacePlugin;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.*;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetInfo;

public abstract class Method
{
    // items
    protected final MethodStep[] fillCoalBag = new MethodStep[] { new ItemStep(Strings.FILL_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG) };
    protected final MethodStep[] refillCoalBag = new MethodStep[] { new ItemStep(Strings.REFILL_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG) };
    protected final MethodStep[] emptyCoalBag = new MethodStep[] { new ItemStep(Strings.EMPTY_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG) };
    protected final MethodStep[] withdrawCoalBag = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_COAL_BAG, ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG) };

    protected final MethodStep[] withdrawCoal = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_COAL, ItemID.COAL) };
    protected final MethodStep[] withdrawGoldOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_GOLD_ORE, ItemID.GOLD_ORE) };
    protected final MethodStep[] withdrawIronOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_IRON_ORE, ItemID.IRON_ORE) };
    protected final MethodStep[] withdrawMithrilOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_MITHRIL_ORE, ItemID.MITHRIL_ORE) };
    protected final MethodStep[] withdrawAdamantiteOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE) };
    protected final MethodStep[] withdrawRuniteOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_RUNITE_ORE, ItemID.RUNITE_ORE) };

    protected final MethodStep[] withdrawIceOrSmithsGloves = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) };
    protected final MethodStep[] equipIceOrSmithsGloves = new MethodStep[] { new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) };
    protected final MethodStep[] withdrawGoldsmithGauntlets = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_GOLDSMITH_GAUNTLETS, ItemID.GOLDSMITH_GAUNTLETS) };
    protected final MethodStep[] equipGoldsmithGauntlets = new MethodStep[] { new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GOLDSMITH_GAUNTLETS) };
    protected final MethodStep[] withdrawSmithingCape = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_SMITHING_CAPE, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET) };
    protected final MethodStep[] withdrawMaxCape = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_MAX_CAPE, ItemID.MAX_CAPE) };
    protected final MethodStep[] equipSmithingCape = new MethodStep[] { new ItemStep(Strings.EQUIP_SMITHING_CAPE, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET) };
    protected final MethodStep[] equipMaxCape = new MethodStep[] { new ItemStep(Strings.EQUIP_MAX_CAPE, ItemID.MAX_CAPE) };
    protected final MethodStep[] depositBarsAndOres = new MethodStep[] { new ItemStep(Strings.DEPOSIT_BARS_AND_ORES, BarsOres.getAllIds()) };
    protected final MethodStep[] depositStaminaPotions = new MethodStep[] { new ItemStep(Strings.DEPOSIT_STAMINA_POTIONS, ItemID.VIAL, ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4) };
    protected final MethodStep[] depositSuperEnergyPotions = new MethodStep[] { new ItemStep(Strings.DEPOSIT_SUPER_ENERGY_POTIONS, ItemID.VIAL, ItemID.SUPER_ENERGY1, ItemID.SUPER_ENERGY2, ItemID.SUPER_ENERGY3, ItemID.SUPER_ENERGY4) };
    protected final MethodStep[] depositEnergyPotions = new MethodStep[] { new ItemStep(Strings.DEPOSIT_ENERGY_POTIONS, ItemID.VIAL, ItemID.ENERGY_POTION1, ItemID.ENERGY_POTION2, ItemID.ENERGY_POTION3, ItemID.ENERGY_POTION4) };

    // objects
    protected final MethodStep[] depositInventory = new MethodStep[] { new WidgetStep(Strings.DEPOSIT_INVENTORY, WidgetInfo.BANK_DEPOSIT_INVENTORY) };
    protected final MethodStep[] putOntoConveyorBelt = new MethodStep[] { new ObjectStep(Strings.PUT_ORE_ONTO_CONVEYOR_BELT, EasyBlastFurnacePlugin.CONVEYOR_BELT) };
    protected final MethodStep[] putOntoConveyorBeltAndEquipGoldsmithGauntlets = new MethodStep[] { new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GOLDSMITH_GAUNTLETS), new ObjectStep(Strings.PUT_ORE_ONTO_CONVEYOR_BELT, EasyBlastFurnacePlugin.CONVEYOR_BELT) };
    protected final MethodStep[] openBank = new MethodStep[] { new ObjectStep(Strings.OPEN_BANK, EasyBlastFurnacePlugin.BANK_CHEST) };
    protected final MethodStep[] collectBars = new MethodStep[] { new ObjectStep(Strings.COLLECT_BARS, EasyBlastFurnacePlugin.BAR_DISPENSER), new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I), new TileStep("", EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] waitForBars = new MethodStep[] { new TileStep(Strings.WAIT_FOR_BARS, EasyBlastFurnacePlugin.PICKUP_POSITION), new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) };
    protected final MethodStep[] waitForGoldBars = new MethodStep[] { new TileStep(Strings.WAIT_FOR_BARS, EasyBlastFurnacePlugin.PICKUP_POSITION), new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GOLDSMITH_GAUNTLETS) };
    protected final MethodStep[] goToDispenser = new MethodStep[] { new TileStep(Strings.GO_TO_DISPENSER, EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] goToDispenserAndEquipIceOrSmithsGloves = new MethodStep[] { new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I), new TileStep(Strings.GO_TO_DISPENSER, EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] collectBarsAndEquipGoldsmithGauntlets = new MethodStep[] { new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS_AFTER_COLLECT_BARS, ItemID.GOLDSMITH_GAUNTLETS), new ObjectStep(Strings.COLLECT_BARS, EasyBlastFurnacePlugin.BAR_DISPENSER) };

    public abstract MethodStep[] next(BlastFurnaceState state);

    public abstract String getName();
}
