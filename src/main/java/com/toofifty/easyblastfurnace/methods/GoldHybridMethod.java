package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
@Slf4j
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

			if (state.getFurnace().getQuantity(ItemID.GOLD_ORE, oreItem()) == 28 && state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return collectBars;
			}

			if (!useDepositInventory && coalRun && !state.getEquipment().hasGoldsmithEffect()) {
				return equipGoldsmithGauntlets;
			}

			if (coalRun && !state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return withdrawGoldOre;
			}

			if (!state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
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

			log.info("gold and adamantite bars: " + state.getFurnace().getQuantity(barItem(), ItemID.GOLD_BAR));
			log.info("gold ore in furnace: " + state.getFurnace().getQuantity(ItemID.GOLD_ORE));
			if (oreOnConveyor && state.getFurnace().getQuantity(ItemID.GOLD_BAR) < 28 && !furnaceHasMetalBar) {
				return waitForGoldBars;
			}

			if (!atBarDispenser) {
				return goToDispenserAndEquipIceOrSmithsGloves;
			}

			if (furnaceHasGoldBar && state.getFurnace().getQuantity(ItemID.GOLD_ORE) > 0 && state.getFurnace().getQuantity(barItem(), oreItem()) == 0) {
				return collectBarsAndEquipGoldsmithGauntlets;
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
