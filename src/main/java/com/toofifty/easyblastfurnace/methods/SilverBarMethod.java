package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Equipment;
import com.toofifty.easyblastfurnace.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

@Slf4j
public class SilverBarMethod extends Method {

	private MethodStep[] checkPrerequisite(BlastFurnaceState state)
	{
		if (!state.getEquipment().equipped(Equipment.ICE_GLOVES.items) && state.getInventory().has(ItemID.SILVER_ORE)) {
			return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
		}
		if (!state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().equipped(Equipment.ICE_GLOVES.items)) {
			return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
		}
		if (state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().equipped(Equipment.ICE_GLOVES.items)) {
			return equipIceOrSmithsGloves;
		}
		return null;
	}

	private MethodStep[] clearInventoryAndBarDispenser(BlastFurnaceState state, boolean barDispenserFull, boolean furnaceHasOre, boolean tickPerfectMethod, boolean furnaceHasBar, boolean andOres)
	{
		boolean useDepositInventory = state.getConfig().useDepositInventory();
		if (barDispenserFull || (furnaceHasOre && furnaceHasBar) || (!tickPerfectMethod && furnaceHasBar)) {
			if (state.getInventory().has(ItemID.SILVER_ORE, ItemID.SILVER_BAR)) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}
			return collectBars;
		}
		int[] itemsToDeposit = andOres ? new int[]{ ItemID.SILVER_BAR, ItemID.SILVER_ORE } : new int[]{ ItemID.SILVER_BAR };
		if (state.getInventory().has(itemsToDeposit)) {
			return useDepositInventory ? depositInventory : depositBarsAndOres;
		}

		return null;
	}

	@Override
	public MethodStep[] next(BlastFurnaceState state)
	{
		MethodStep[] prerequisite = checkPrerequisite(state);
		if (prerequisite != null) return prerequisite;
		boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
		boolean furnaceHasBar = state.getFurnace().has(ItemID.SILVER_BAR);
		boolean furnaceHasOre = state.getFurnace().has(ItemID.SILVER_ORE);
		boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
		boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
		boolean barDispenserFull = state.getFurnace().getQuantity(ItemID.SILVER_BAR) >= 28;

		if (tickPerfectMethod && state.getInventory().getFreeSlotsIncludingOresAndBars() == 28) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, barDispenserFull, furnaceHasOre, tickPerfectMethod, furnaceHasBar, true);
			if (clearBarsAndOres != null) return clearBarsAndOres;
			return state.getBank().isOpen() ? addDummyItemToInventory : openBank;
		}

		if (state.getBank().isOpen()) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, barDispenserFull, furnaceHasOre, tickPerfectMethod, furnaceHasBar, false);
			if (clearBarsAndOres != null) return clearBarsAndOres;

			if (!state.getInventory().has(ItemID.SILVER_ORE)) {
				return withdrawSilverOre;
			}
		}

		if (state.getInventory().has(ItemID.SILVER_ORE)) {
			return putOntoConveyorBelt;
		}

		if (tickPerfectMethod && (oreOnConveyor || furnaceHasOre || barDispenserFull) && furnaceHasBar) {
			if (atConveyorBelt) {
				return goToDispenser;
			}
			return collectBars;
		}

		if (!tickPerfectMethod && (oreOnConveyor || furnaceHasOre || barDispenserFull)) {
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

	@Override
	public String getName()
	{
		return Strings.SILVER;
	}
}
