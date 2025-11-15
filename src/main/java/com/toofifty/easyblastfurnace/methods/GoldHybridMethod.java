package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Equipment;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

@Slf4j
abstract public class GoldHybridMethod extends MetalBarMethod
{
	protected boolean lastInvWasGold = false;
    private MethodStep[] checkPrerequisite(BlastFurnaceState state, boolean hasCoalBag, boolean hasGoldsmithEquipment)
    {
		boolean skillCapesEnabled = state.getConfig().enableSkillCapes();
        if (hasCoalBag && !state.getInventory().has(ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN)) {
			if (state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
			}
            return state.getBank().isOpen() ? withdrawCoalBag : openBank;
        }

        if (!state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().hasIceGlovesEffect()) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (skillCapesEnabled &&
			state.getBank().has(Equipment.MAX_CAPE.items) &&
            !state.getInventory().has(Equipment.MAX_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.MAX_CAPE.items)) {
            return state.getBank().isOpen() ? withdrawMaxCape : openBank;
        }

        if (skillCapesEnabled &&
			state.getInventory().has(Equipment.MAX_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.MAX_CAPE.items)) {
            return equipMaxCape;
        }

        if (skillCapesEnabled &&
			state.getBank().has(Equipment.SMITHING_CAPE.items) &&
            !state.getInventory().has(Equipment.SMITHING_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.merge(Equipment.SMITHING_CAPE.items, Equipment.MAX_CAPE.items))) {
            return state.getBank().isOpen() ? withdrawSmithingCape : openBank;
        }

        if (skillCapesEnabled &&
			state.getInventory().has(Equipment.SMITHING_CAPE.items) &&
            !state.getEquipment().equipped(Equipment.merge(Equipment.SMITHING_CAPE.items, Equipment.MAX_CAPE.items))) {
            return equipSmithingCape;
        }

        if (hasGoldsmithEquipment && !state.getInventory().has(Equipment.GOLDSMITH.items) && !state.getEquipment().hasGoldsmithEffect()) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        if (hasGoldsmithEquipment && !state.getEquipment().hasIceGlovesEffect() && !state.getEquipment().hasGoldsmithEffect()) {
            return equipGoldsmithGauntlets;
        }

        return null;
    }

	private MethodStep[] clearInventoryAndBarDispenser(BlastFurnaceState state, boolean needToCollectBars, boolean useDepositInventory, boolean andOres)
	{
		if (needToCollectBars) {
			if (state.getInventory().has(oreItem(), ItemID.GOLD_ORE, barItem(), ItemID.GOLD_BAR)) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}
			lastInvWasGold = true;
			return collectBars;
		}

		int[] itemsToDeposit = andOres ? new int[]{ ItemID.GOLD_BAR, ItemID.GOLD_ORE, barItem(), oreItem() } : new int[]{ ItemID.GOLD_BAR, barItem() };
		if (state.getInventory().has(itemsToDeposit)) {
			return useDepositInventory ? depositInventory : depositBarsAndOres;
		}
		return null;
	}

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        boolean hasCoalBag = Equipment.hasCoalBag(state);
        boolean hasGoldsmithEquipment = Equipment.hasGoldsmithEquipment(state);
        MethodStep[] prerequisite = checkPrerequisite(state, hasCoalBag, hasGoldsmithEquipment);
        if (prerequisite != null) return prerequisite;
        boolean maxCoalIsThirtySix = state.getEquipment().equipped(Equipment.merge(Equipment.MAX_CAPE.items, Equipment.SMITHING_CAPE.items));
        int maxCoalInventory = state.getInventory().getFreeSlotsIncludingOresAndBars();
        boolean coalRun = state.getFurnace().getQuantity(ItemID.COAL) < maxCoalInventory * (coalPer() - state.getFurnace().getCoalOffset());
		boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
		boolean furnaceHasGoldBar = state.getFurnace().has(ItemID.GOLD_BAR);
		boolean furnaceHasGoldOre = state.getFurnace().has(ItemID.GOLD_ORE);
		boolean furnaceHasMetalBar = state.getFurnace().has(barItem());
		boolean furnaceHasMetalOre = state.getFurnace().has(oreItem());
		boolean furnaceHasBar = state.getFurnace().has(barItem(), ItemID.GOLD_BAR);
        boolean coalBagFull = state.getCoalBag().isFull();
        boolean coalBagEmpty = state.getCoalBag().isEmpty();
		boolean atBarDispenser = state.getPlayer().isAtBarDispenser();
		boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
		boolean useDepositInventory = state.getConfig().useDepositInventory();
		boolean fullOfMetalBarsAndOres = (state.getFurnace().getQuantity(oreItem(), barItem()) >= 28);
		boolean barDispenserFull = (furnaceHasMetalBar && furnaceHasGoldBar) || (furnaceHasGoldOre && furnaceHasGoldBar) || fullOfMetalBarsAndOres;
		boolean needToCollectBars = (barDispenserFull || (!tickPerfectMethod && furnaceHasBar));

		if (tickPerfectMethod && state.getInventory().getFreeSlotsIncludingOresAndBars() == 28) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, needToCollectBars, useDepositInventory, true);
			if (clearBarsAndOres != null) return clearBarsAndOres;
			return state.getBank().isOpen() ? addDummyItemToInventory : openBank;
		}

		if (!state.getBank().isOpen() && coalRun && state.getInventory().has(oreItem())) {
			return openBank;
		}

		if (state.getBank().isOpen()) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, needToCollectBars, useDepositInventory, false);
			if (clearBarsAndOres != null) return clearBarsAndOres;

			if ((hasCoalBag && state.getCoalBag().isFull()) && state.getInventory().has(oreItem(), ItemID.GOLD_ORE)) {
				return putOntoConveyorBelt;
			}

			if (state.getInventory().has(ItemID.GOLD_BAR, barItem())) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}

			if (hasCoalBag && !useDepositInventory && !coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}

			if (hasGoldsmithEquipment && !useDepositInventory && coalRun && !state.getEquipment().hasGoldsmithEffect()) {
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

			if (hasCoalBag && useDepositInventory && !coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}
		}

        if (hasGoldsmithEquipment && state.getInventory().has(ItemID.GOLD_ORE) && !state.getEquipment().hasGoldsmithEffect()) {
            return equipGoldsmithGauntlets;
        }

        if (!barDispenserFull && (state.getCoalBag().recentlyEmptiedCoalBag || state.getInventory().has(ItemID.COAL, ItemID.GOLD_ORE, oreItem()))) {
			state.getCoalBag().recentlyEmptiedCoalBag = false;
            return putOntoConveyorBelt;
        }

        if (hasCoalBag && !barDispenserFull && atConveyorBelt && (coalBagFull || (!coalBagEmpty && maxCoalIsThirtySix)) ) {
            return emptyCoalBag;
        }

		if (hasCoalBag && barDispenserFull && state.getInventory().has(ItemID.COAL) && !state.getCoalBag().isFull()) {
			return fillCoalBag;
		}

		if (needToCollectBars && state.getInventory().has(ItemID.COAL, oreItem(), barItem(), ItemID.GOLD_ORE)) {
			return useDepositInventory ? depositInventory : depositBarsAndOres;
		}

		// 1. Add gold and go to bank
		// 2. Add gold and pick up prev run's bars until enough coal
		// 3. Add metal ore and pick up gold

		// 4. Add gold and pick up gold until enough coal
		// 5. Add metal ore and pick up metal bars
		// 6. Repeat steps 4 & 5

		if (tickPerfectMethod && (
			(furnaceHasGoldBar && (oreOnConveyor || furnaceHasGoldOre || furnaceHasMetalOre)) ||
			(furnaceHasMetalBar && (oreOnConveyor || furnaceHasGoldOre || fullOfMetalBarsAndOres || furnaceHasGoldBar))
		)) {
			if (atConveyorBelt) {
				return goToDispenser;
			}

			if (furnaceHasGoldBar && (oreOnConveyor || furnaceHasGoldOre) && lastInvWasGold && state.getEquipment().hasIceGlovesEffect()) {
				return collectBarsAndEquipGoldsmithGauntlets;
			}

			if (oreOnConveyor && lastInvWasGold && furnaceHasMetalBar) {
				return waitForGoldBars;
			}

			if (!atBarDispenser) {
				return goToDispenserAndEquipIceOrSmithsGloves;
			}

			return collectBars;
		}

        if (!tickPerfectMethod && (oreOnConveyor || furnaceHasGoldOre || furnaceHasMetalOre)) {
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
