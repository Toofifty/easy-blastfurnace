package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import net.runelite.api.ItemID;

abstract public class GoldHybridMethod extends MetalBarMethod
{
    private MethodStep[] checkPrerequisite(BlastFurnaceState state)
    {
        if (!state.getInventory().has(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG)) {
            return state.getBank().isOpen() ? withdrawCoalBag : openBank;
        }

        if (!state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) && !state.getEquipment().hasIceGlovesEffect()) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getBank().has(ItemID.MAX_CAPE) &&
            !state.getInventory().has(ItemID.MAX_CAPE) &&
            !state.getEquipment().equipped(ItemID.MAX_CAPE)) {
            return state.getBank().isOpen() ? withdrawMaxCape : openBank;
        }

        if (state.getInventory().has(ItemID.MAX_CAPE) &&
            !state.getEquipment().equipped(ItemID.MAX_CAPE)) {
            return equipMaxCape;
        }

        if (state.getBank().has(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET) &&
            !state.getInventory().has(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET) &&
            !state.getEquipment().equipped(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, ItemID.MAX_CAPE)) {
            return state.getBank().isOpen() ? withdrawSmithingCape : openBank;
        }

        if (state.getInventory().has(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET) &&
            !state.getEquipment().equipped(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET, ItemID.MAX_CAPE)) {
            return equipSmithingCape;
        }

        if (!state.getInventory().has(ItemID.GOLDSMITH_GAUNTLETS) && !state.getEquipment().hasGoldsmithEffect()) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        if (!state.getEquipment().hasIceGlovesEffect() && !state.getEquipment().hasGoldsmithEffect()) {
            return equipGoldsmithGauntlets;
        }

        return null;
    }

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        MethodStep[] prerequisite = checkPrerequisite(state);
        if (prerequisite != null) return prerequisite;
        boolean smithingCapeEquipped = state.getEquipment().equipped(ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET);
        int maxCoalInventory = state.getInventory().getFreeSlotsIncludingOresAndBars();
        boolean coalRun = state.getFurnace().getQuantity(ItemID.COAL) < maxCoalInventory * (coalPer() - state.getFurnace().getCoalOffset());
		boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
		boolean furnaceHasBar = state.getFurnace().has(barItem(), ItemID.GOLD_BAR);
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
        boolean coalBagFull = state.getCoalBag().isFull();
        boolean coalBagEmpty = state.getCoalBag().isEmpty();

		if (state.getBank().isOpen()) {

			if (state.getCoalBag().isFull() && state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return putOntoConveyorBelt;
			}

			if (state.getInventory().has(ItemID.GOLD_BAR, barItem())) {
				return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
			}

			if (coalRun && !state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return withdrawGoldOre;
			}

			if (!state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return withdrawOre();
			}

			if (!coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}
		}

        if (state.getInventory().has(ItemID.GOLD_ORE) && !state.getEquipment().hasGoldsmithEffect()) {
            return equipGoldsmithGauntlets;
        }

        if (state.getInventory().has(ItemID.COAL, ItemID.GOLD_ORE) || (state.getInventory().has(oreItem()) && !coalRun)) {
            return putOntoConveyorBelt;
        }

        if ( (coalBagFull && state.getPlayer().isAtConveyorBelt()) || (!coalBagEmpty && smithingCapeEquipped) ) {
            return emptyCoalBag;
        }

        if (!tickPerfectMethod && oreOnConveyor) {
            return waitForBars;
        }

        if (tickPerfectMethod && !state.getPlayer().isAtBarDispenser()) {
            return goToDispenserAndEquipIceOrSmithsGloves;
        }

        if (tickPerfectMethod && state.getPlayer().isAtBarDispenser()) {
            return collectBarsAndEquipGoldsmithGauntlets;
        }

		if (!tickPerfectMethod && furnaceHasBar) {
			if (!state.getEquipment().hasIceGlovesEffect()) {
				return equipIceOrSmithsGloves;
			}
			return collectBars;
		}

        return openBank;
    }
}
