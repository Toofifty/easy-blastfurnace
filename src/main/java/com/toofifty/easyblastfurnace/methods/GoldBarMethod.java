package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Equipment;
import com.toofifty.easyblastfurnace.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

@Slf4j
public class GoldBarMethod extends Method
{
    private MethodStep[] checkPrerequisite(BlastFurnaceState state, boolean hasGoldsmithEquipment)
    {
		boolean skillCapesEnabled = state.getConfig().enableSkillCapes();
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
        return null;
    }

	private MethodStep[] clearInventoryAndBarDispenser(BlastFurnaceState state, boolean barDispenserFull, boolean furnaceHasOre, boolean tickPerfectMethod, boolean furnaceHasBar, boolean andOres)
	{
		boolean useDepositInventory = state.getConfig().useDepositInventory();
		if (furnaceHasOre && furnaceHasBar || barDispenserFull || (!tickPerfectMethod && furnaceHasBar)) {
			if (state.getInventory().has(ItemID.GOLD_BAR, ItemID.GOLD_ORE)) {
				return useDepositInventory ? depositInventory : depositBarsAndOres;
			}
			return collectBars;
		}


		int[] itemsToDeposit = andOres ? new int[]{ ItemID.GOLD_BAR, ItemID.GOLD_ORE } : new int[]{ ItemID.GOLD_BAR };
		if (state.getInventory().has(itemsToDeposit)) {
			return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
		}
		return null;
	}

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        boolean hasGoldsmithEquipment = Equipment.hasGoldsmithEquipment(state);
        MethodStep[] prerequisite = checkPrerequisite(state, hasGoldsmithEquipment);
        if (prerequisite != null) return prerequisite;
        boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
        boolean furnaceHasBar = state.getFurnace().has(ItemID.GOLD_BAR);
        boolean furnaceHasOre = state.getFurnace().has(ItemID.GOLD_ORE);
        boolean atBarDispenser = state.getPlayer().isAtBarDispenser();
        boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
		boolean barDispenserFull = state.getFurnace().getQuantity(ItemID.GOLD_BAR) >= 28;

		if (tickPerfectMethod && state.getInventory().getFreeSlotsIncludingOresAndBars() == 28) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, barDispenserFull, furnaceHasOre, tickPerfectMethod, furnaceHasBar, true);
			if (clearBarsAndOres != null) return clearBarsAndOres;
			return state.getBank().isOpen() ? addDummyItemToInventory : openBank;
		}

        if (state.getBank().isOpen()) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, barDispenserFull, furnaceHasOre, tickPerfectMethod, furnaceHasBar, false);
			if (clearBarsAndOres != null) return clearBarsAndOres;

			if (hasGoldsmithEquipment && !state.getEquipment().hasGoldsmithEffect() && (!tickPerfectMethod || !state.getEquipment().hasIceGlovesEffect())) {
				return equipGoldsmithGauntlets;
			}

            if (!state.getInventory().has(ItemID.GOLD_ORE)) {
                return withdrawGoldOre;
            }
        }

        if (tickPerfectMethod && !barDispenserFull && state.getInventory().has(ItemID.GOLD_ORE)) {
            if (furnaceHasBar) {
                return putOntoConveyorBelt;
            } else {
                return hasGoldsmithEquipment ? putOntoConveyorBeltAndEquipGoldsmithGauntlets : putOntoConveyorBelt;
            }
        }

        if (tickPerfectMethod && (oreOnConveyor || furnaceHasOre) && furnaceHasBar) {
            if (atConveyorBelt) {
                return goToDispenser;
            }

            if (!atBarDispenser) {
                return goToDispenserAndEquipIceOrSmithsGloves;
            }

            return hasGoldsmithEquipment ? collectBarsAndEquipGoldsmithGauntlets : collectBars;
        }

        if (!tickPerfectMethod && !barDispenserFull && state.getInventory().has(ItemID.GOLD_ORE)) {
            if (hasGoldsmithEquipment && !state.getEquipment().hasGoldsmithEffect()) {
                return equipGoldsmithGauntlets;
            }
            return putOntoConveyorBelt;
        }

		if (!tickPerfectMethod && (oreOnConveyor || furnaceHasOre) && !state.getInventory().has(ItemID.GOLD_ORE)) {
			return waitForGoldBars;
		}

        if (!tickPerfectMethod && furnaceHasBar && !state.getInventory().has(ItemID.GOLD_ORE)) {
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
        return Strings.GOLD;
    }
}
