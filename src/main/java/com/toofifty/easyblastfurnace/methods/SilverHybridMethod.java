package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Equipment;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

@Slf4j
abstract public class SilverHybridMethod extends MetalBarMethod
{
	protected boolean lastInvWasSilver = false;
	private MethodStep[] checkPrerequisite(BlastFurnaceState state, boolean hasCoalBag)
	{
		boolean skillCapesEnabled = state.getConfig().enableSkillCapes();
		if (hasCoalBag && !state.getInventory().has(ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN)) {
			if (state.getInventory().has(oreItem(), ItemID.SILVER_ORE)) {
				return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
			}
			return state.getBank().isOpen() ? withdrawCoalBag : openBank;
		}

		if (!state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().hasIceGlovesEffect()) {
			return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
		}

		if (!state.getEquipment().hasIceGlovesEffect()) {
			return equipIceOrSmithsGloves;
		}

		if (skillCapesEnabled && state.getBank().has(Equipment.SKILLING_CAPE.items) &&
				!state.getInventory().has(Equipment.SKILLING_CAPE.items) &&
				!state.getEquipment().equipped(Equipment.SKILLING_CAPE.items)) {
			return state.getBank().isOpen() ? withdrawSkillingCape : openBank;
		}

		if (skillCapesEnabled && state.getInventory().has(Equipment.SKILLING_CAPE.items) &&
				!state.getEquipment().equipped(Equipment.SKILLING_CAPE.items)) {
			return equipSkillingCape;
		}

		return null;
	}

	private MethodStep[] clearInventoryAndBarDispenser(BlastFurnaceState state, boolean needToCollectBars, boolean useDepositInventory, boolean andOres)
	{
		if (needToCollectBars) {
			if (state.getInventory().has(oreItem(), ItemID.SILVER_ORE, barItem(), ItemID.SILVER_BAR)) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}
			lastInvWasSilver = true;
			return collectBars;
		}
		int[] itemsToDeposit = andOres ? new int[]{ ItemID.SILVER_BAR, ItemID.SILVER_ORE, barItem(), oreItem() } : new int[]{ ItemID.SILVER_BAR, barItem() };
		if (state.getInventory().has(itemsToDeposit)) {
			return useDepositInventory ? depositInventory : depositBarsAndOres;
		}
		return null;
	}

	@Override
	public MethodStep[] next(BlastFurnaceState state)
	{
		boolean hasCoalBag = Equipment.hasCoalBag(state);
		MethodStep[] prerequisite = checkPrerequisite(state, hasCoalBag);
		if (prerequisite != null) return prerequisite;
		boolean maxCoalIsThirtySix = state.getEquipment().equipped(Equipment.SKILLING_CAPE.items);
		int maxCoalInventory = state.getInventory().getFreeSlotsIncludingOresAndBars();
		boolean coalRun = state.getFurnace().getQuantity(ItemID.COAL) < maxCoalInventory * (coalPer() - state.getFurnace().getCoalOffset());
		boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
		boolean furnaceHasSilverBar = state.getFurnace().has(ItemID.SILVER_BAR);
		boolean furnaceHasSilverOre = state.getFurnace().has(ItemID.SILVER_ORE);
		boolean furnaceHasMetalBar = state.getFurnace().has(barItem());
		boolean furnaceHasMetalOre = state.getFurnace().has(oreItem());
		boolean furnaceHasBar = state.getFurnace().has(barItem(), ItemID.SILVER_BAR);
		boolean coalBagFull = state.getCoalBag().isFull();
		boolean coalBagEmpty = state.getCoalBag().isEmpty();
		boolean atBarDispenser = state.getPlayer().isAtBarDispenser();
		boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
		boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
		boolean useDepositInventory = state.getConfig().useDepositInventory();
		boolean fullOfMetalBarsAndOres = (state.getFurnace().getQuantity(oreItem(), barItem()) >= 28);
		boolean barDispenserFull = (furnaceHasMetalBar && furnaceHasSilverBar) || (furnaceHasSilverOre && furnaceHasSilverBar) || fullOfMetalBarsAndOres;
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

			if ((hasCoalBag && state.getCoalBag().isFull()) && state.getInventory().has(oreItem(), ItemID.SILVER_ORE)) {
				return putOntoConveyorBelt;
			}

			if (state.getInventory().has(ItemID.SILVER_BAR, barItem())) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}

			if (hasCoalBag && !useDepositInventory && !coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}

			if (coalRun && !state.getInventory().has(oreItem(), ItemID.SILVER_ORE)) {
				lastInvWasSilver = true;
				return withdrawSilverOre;
			}

			if (!state.getInventory().has(oreItem(), ItemID.SILVER_ORE)) {
				lastInvWasSilver = false;
				return withdrawOre();
			}

			if (hasCoalBag && useDepositInventory && !coalBagFull) {
				return coalBagEmpty ? fillCoalBag : refillCoalBag;
			}
		}

		if (!barDispenserFull && (state.getCoalBag().recentlyEmptiedCoalBag || state.getInventory().has(ItemID.COAL, ItemID.SILVER_ORE, oreItem()))) {
			state.getCoalBag().recentlyEmptiedCoalBag = false;
			return putOntoConveyorBelt;
		}

		if (hasCoalBag && !barDispenserFull && atConveyorBelt && (coalBagFull || (!coalBagEmpty && maxCoalIsThirtySix)) ) {
			return emptyCoalBag;
		}

		if (hasCoalBag && barDispenserFull && state.getInventory().has(ItemID.COAL) && !state.getCoalBag().isFull()) {
			return fillCoalBag;
		}

		if (needToCollectBars && state.getInventory().has(ItemID.COAL, oreItem(), barItem(), ItemID.SILVER_ORE)) {
			return useDepositInventory ? depositInventory : depositBarsAndOres;
		}

		// 1. Add silver and go to bank
		// 2. Add silver and pick up prev run's bars until enough coal
		// 3. Add metal ore and pick up silver

		// 4. Add silver and pick up silver until enough coal
		// 5. Add metal ore and pick up metal bars
		// 6. Repeat steps 4 & 5

		if (tickPerfectMethod && (
			(furnaceHasSilverBar && (oreOnConveyor || furnaceHasSilverOre || furnaceHasMetalOre)) ||
			(furnaceHasMetalBar && (oreOnConveyor || furnaceHasSilverOre || fullOfMetalBarsAndOres || furnaceHasSilverBar))
		)) {
			if (atConveyorBelt) {
				return goToDispenser;
			}

			if (furnaceHasSilverBar && (oreOnConveyor || furnaceHasSilverOre) && lastInvWasSilver && state.getEquipment().hasIceGlovesEffect()) {
				return collectBars;
			}

			if (oreOnConveyor && lastInvWasSilver && furnaceHasMetalBar) {
				return waitForBars;
			}

			if (!atBarDispenser) {
				return goToDispenserAndEquipIceOrSmithsGloves;
			}

			return collectBars;
		}

		if (!tickPerfectMethod && (oreOnConveyor || furnaceHasSilverOre || furnaceHasMetalOre)) {
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
