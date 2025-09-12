package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.EasyBlastFurnacePlugin;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.*;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;

public abstract class Method
{
    // items
    protected final MethodStep[] fillCoalBag = new MethodStep[] { new ItemStep(Strings.FILL_COAL_BAG, ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN) };
    protected final MethodStep[] refillCoalBag = new MethodStep[] { new ItemStep(Strings.REFILL_COAL_BAG, ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN) };
    protected final MethodStep[] emptyCoalBag = new MethodStep[] { new ItemStep(Strings.EMPTY_COAL_BAG, ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN) };
    protected final MethodStep[] withdrawCoalBag = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_COAL_BAG, ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN) };

    protected final MethodStep[] withdrawCoal = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_COAL, ItemID.COAL) };
    protected final MethodStep[] withdrawGoldOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_GOLD_ORE, ItemID.GOLD_ORE) };
    protected final MethodStep[] withdrawIronOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_IRON_ORE, ItemID.IRON_ORE) };
    protected final MethodStep[] withdrawMithrilOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_MITHRIL_ORE, ItemID.MITHRIL_ORE) };
    protected final MethodStep[] withdrawAdamantiteOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ADAMANTITE_ORE, ItemID.ADAMANTITE_ORE) };
    protected final MethodStep[] withdrawRuniteOre = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_RUNITE_ORE, ItemID.RUNITE_ORE) };

    protected final MethodStep[] withdrawIceOrSmithsGloves = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE) };
    protected final MethodStep[] equipIceOrSmithsGloves = new MethodStep[] { new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE) };
    protected final MethodStep[] withdrawGoldsmithGauntlets = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_GOLDSMITH_GAUNTLETS, ItemID.GAUNTLETS_OF_GOLDSMITHING) };
    protected final MethodStep[] equipGoldsmithGauntlets = new MethodStep[] { new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GAUNTLETS_OF_GOLDSMITHING) };
    protected final MethodStep[] withdrawSmithingCape = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_SMITHING_CAPE, ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED) };
    protected final MethodStep[] withdrawMaxCape = new MethodStep[] { new BankItemStep(Strings.WITHDRAW_MAX_CAPE, ItemID.SKILLCAPE_MAX) };
    protected final MethodStep[] equipSmithingCape = new MethodStep[] { new ItemStep(Strings.EQUIP_SMITHING_CAPE, ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED) };
    protected final MethodStep[] equipMaxCape = new MethodStep[] { new ItemStep(Strings.EQUIP_MAX_CAPE, ItemID.SKILLCAPE_MAX) };
    protected final MethodStep[] depositBarsAndOres = new MethodStep[] { new ItemStep(Strings.DEPOSIT_BARS_AND_ORES, BarsOres.getAllIds()), new ObjectStep(Strings.OPEN_BANK, EasyBlastFurnacePlugin.BANK_CHEST) };
    protected final MethodStep[] depositStaminaPotions = new MethodStep[] { new ItemStep(Strings.DEPOSIT_STAMINA_POTIONS, ItemID.VIAL_EMPTY, ItemID._1DOSESTAMINA, ItemID._2DOSESTAMINA, ItemID._3DOSESTAMINA, ItemID._4DOSESTAMINA) };
    protected final MethodStep[] depositSuperEnergyPotions = new MethodStep[] { new ItemStep(Strings.DEPOSIT_SUPER_ENERGY_POTIONS, ItemID.VIAL_EMPTY, ItemID._1DOSE2ENERGY, ItemID._2DOSE2ENERGY, ItemID._3DOSE2ENERGY, ItemID._4DOSE2ENERGY) };
    protected final MethodStep[] depositEnergyPotions = new MethodStep[] { new ItemStep(Strings.DEPOSIT_ENERGY_POTIONS, ItemID.VIAL_EMPTY, ItemID._1DOSE1ENERGY, ItemID._2DOSE1ENERGY, ItemID._3DOSE1ENERGY, ItemID._4DOSE1ENERGY) };
	protected final MethodStep[] depositStrangeFruit = new MethodStep[] { new ItemStep(Strings.DEPOSIT_STRANGE_FRUIT, ItemID.MACRO_TRIFFIDFRUIT) };

    // objects
    protected final MethodStep[] depositInventory = new MethodStep[] { new WidgetStep(Strings.DEPOSIT_INVENTORY, InterfaceID.BANKMAIN, InterfaceID.Bankmain.DEPOSITINV), new ObjectStep(Strings.OPEN_BANK, EasyBlastFurnacePlugin.BANK_CHEST) };
    protected final MethodStep[] putOntoConveyorBelt = new MethodStep[] { new ObjectStep(Strings.PUT_ORE_ONTO_CONVEYOR_BELT, EasyBlastFurnacePlugin.CONVEYOR_BELT) };
    protected final MethodStep[] putOntoConveyorBeltAndEquipGoldsmithGauntlets = new MethodStep[] { new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GAUNTLETS_OF_GOLDSMITHING), new ObjectStep(Strings.PUT_ORE_ONTO_CONVEYOR_BELT, EasyBlastFurnacePlugin.CONVEYOR_BELT) };
    protected final MethodStep[] openBank = new MethodStep[] { new ObjectStep(Strings.OPEN_BANK, EasyBlastFurnacePlugin.BANK_CHEST) };
    protected final MethodStep[] collectBars = new MethodStep[] { new ObjectStep(Strings.COLLECT_BARS, EasyBlastFurnacePlugin.BAR_DISPENSER), new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE), new TileStep("", EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] waitForBars = new MethodStep[] { new TileStep(Strings.WAIT_FOR_BARS, EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] waitForGoldBars = new MethodStep[] { new TileStep(Strings.WAIT_FOR_BARS, EasyBlastFurnacePlugin.PICKUP_POSITION), new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS, ItemID.GAUNTLETS_OF_GOLDSMITHING) };
    protected final MethodStep[] goToDispenser = new MethodStep[] { new TileStep(Strings.GO_TO_DISPENSER, EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] goToDispenserAndEquipIceOrSmithsGloves = new MethodStep[] { new ItemStep(Strings.EQUIP_ICE_OR_SMITHS_GLOVES, ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE), new TileStep(Strings.GO_TO_DISPENSER, EasyBlastFurnacePlugin.PICKUP_POSITION) };
    protected final MethodStep[] collectBarsAndEquipGoldsmithGauntlets = new MethodStep[] { new ItemStep(Strings.EQUIP_GOLDSMITH_GAUNTLETS_AFTER_COLLECT_BARS, ItemID.GAUNTLETS_OF_GOLDSMITHING), new ObjectStep(Strings.COLLECT_BARS, EasyBlastFurnacePlugin.BAR_DISPENSER) };

    public abstract MethodStep[] next(BlastFurnaceState state);

    public abstract String getName();
}
