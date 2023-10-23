package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
@Slf4j
abstract public class GoldHybridMethod extends MetalBarMethod
{
	protected boolean lastInvWasGold = false;
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
		boolean furnaceHasGoldBar = state.getFurnace().has(ItemID.GOLD_BAR);
		boolean furnaceHasMetalBar = state.getFurnace().has(barItem());
		boolean furnaceMetalBarCount = state.getFurnace().has(barItem()) && state.getFurnace().getQuantity(barItem()) >= state.getInventory().getFreeSlotsIncludingOresAndBars();
		boolean furnaceHasBar = state.getFurnace().has(barItem(), ItemID.GOLD_BAR);
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
        boolean coalBagFull = state.getCoalBag().isFull();
        boolean coalBagEmpty = state.getCoalBag().isEmpty();
		boolean atBarDispenser = state.getPlayer().isAtBarDispenser();
		boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
		boolean useDepositInventory = state.getConfig().useDepositInventory();

		if (state.getBank().isOpen()) {

			if (state.getCoalBag().isFull() && state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return putOntoConveyorBelt;
			}

			if (state.getInventory().has(ItemID.GOLD_BAR, barItem())) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}

			if (!useDepositInventory && !coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}

			if (furnaceHasMetalBar && furnaceHasGoldBar) {
				return collectBars;
			}

			if (!useDepositInventory && coalRun && !state.getEquipment().hasGoldsmithEffect()) {
				return equipGoldsmithGauntlets;
			}

			if (coalRun && !state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				lastInvWasGold = true;
				return withdrawGoldOre;
			}

			if (!state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				lastInvWasGold = false;
				return withdrawOre();
			}

			if (useDepositInventory && !coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}
		}

        if (state.getInventory().has(ItemID.GOLD_ORE) && !state.getEquipment().hasGoldsmithEffect()) {
            return equipGoldsmithGauntlets;
        }

        if (state.getInventory().has(ItemID.COAL, ItemID.GOLD_ORE) || (state.getInventory().has(oreItem()) && !coalRun)) {
            return putOntoConveyorBelt;
        }

        if ( (coalBagFull && atConveyorBelt) || (!coalBagEmpty && smithingCapeEquipped) ) {
            return emptyCoalBag;
        }

		if (tickPerfectMethod && ((oreOnConveyor && furnaceHasGoldBar) || (furnaceHasMetalBar && furnaceHasGoldBar) || (!furnaceHasGoldBar && furnaceHasMetalBar && oreOnConveyor))) {
			if (atConveyorBelt) {
				return goToDispenser;
			}

			if (furnaceHasGoldBar && oreOnConveyor && !furnaceHasMetalBar && lastInvWasGold && state.getEquipment().hasIceGlovesEffect()) {
				return collectBarsAndEquipGoldsmithGauntlets;
			}

			if (oreOnConveyor && lastInvWasGold) {
				return waitForGoldBars;
			}

			if (!atBarDispenser) {
				return goToDispenserAndEquipIceOrSmithsGloves;
			}

			return collectBars;
		}

        if (!tickPerfectMethod && oreOnConveyor) {
            return waitForBars;
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
