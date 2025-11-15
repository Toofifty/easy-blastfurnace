package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Equipment;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.gameval.ItemID;

import java.util.Objects;

/**
 * Represents a basic method for all regular bars (using coal)
 * - Fill coal until threshold
 * - Do trips with ores
 * - Repeat
 */
abstract public class MetalBarMethod extends Method
{
    public abstract int oreItem();

    protected abstract MethodStep[] withdrawOre();

    protected abstract int barItem();

    protected abstract int coalPer();

    private MethodStep[] checkPrerequisite(BlastFurnaceState state, boolean hasCoalBag, boolean isNotSteelMethod)
    {
		boolean skillCapesEnabled = (state.getConfig().enableSkillCapes() && isNotSteelMethod);
        if (hasCoalBag && !state.getInventory().has(ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN)) {
            if (state.getInventory().has(oreItem())) {
                return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
            }
            return state.getBank().isOpen() ? withdrawCoalBag : openBank;
        }

        if (!state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().equipped(Equipment.ICE_GLOVES.items)) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getInventory().has(Equipment.ICE_GLOVES.items) && !state.getEquipment().equipped(Equipment.ICE_GLOVES.items)) {
            return equipIceOrSmithsGloves;
        }

		if (skillCapesEnabled && state.getBank().has(Equipment.MAX_CAPE.items) &&
				!state.getInventory().has(Equipment.MAX_CAPE.items) &&
				!state.getEquipment().equipped(Equipment.MAX_CAPE.items)) {
			return state.getBank().isOpen() ? withdrawMaxCape : openBank;
		}

		if (skillCapesEnabled && state.getInventory().has(Equipment.MAX_CAPE.items) &&
				!state.getEquipment().equipped(Equipment.MAX_CAPE.items)) {
			return equipMaxCape;
		}

		if (skillCapesEnabled && state.getBank().has(Equipment.SMITHING_CAPE.items) &&
				!state.getInventory().has(Equipment.SMITHING_CAPE.items) &&
				!state.getEquipment().equipped(Equipment.merge(Equipment.SMITHING_CAPE.items, Equipment.MAX_CAPE.items))) {
			return state.getBank().isOpen() ? withdrawSmithingCape : openBank;
		}

		if (skillCapesEnabled && state.getInventory().has(Equipment.SMITHING_CAPE.items) &&
				!state.getEquipment().equipped(Equipment.merge(Equipment.SMITHING_CAPE.items, Equipment.MAX_CAPE.items))) {
			return equipSmithingCape;
		}
        return null;
    }

	private MethodStep[] clearInventoryAndBarDispenser(BlastFurnaceState state, boolean barDispenserFull, boolean coalRun, boolean andOres)
	{
		if (state.getInventory().has(barItem()) || barDispenserFull || ((andOres || coalRun) && state.getInventory().has(oreItem()))) {
			return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
		}

		if (state.getFurnace().has(oreItem()) && state.getFurnace().has(barItem())) {
			if (state.getInventory().has(barItem(), oreItem())) {
				return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
			}
			return collectBars;
		}
		return null;
	}

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        boolean hasCoalBag = Equipment.hasCoalBag(state);
		boolean isNotSteelMethod = !Objects.equals(getName(), Strings.STEEL);
        MethodStep[] prerequisite = checkPrerequisite(state, hasCoalBag, isNotSteelMethod);
        if (prerequisite != null) return prerequisite;
        boolean coalRun = state.getFurnace().getQuantity(ItemID.COAL) < 27 * (coalPer() - state.getFurnace().getCoalOffset());
		boolean maxCoalIsThirtySix = state.getEquipment().equipped(Equipment.merge(Equipment.MAX_CAPE.items, Equipment.SMITHING_CAPE.items));
		boolean coalBagFull = state.getCoalBag().isFull();
		boolean coalBagEmpty = state.getCoalBag().isEmpty();
        boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
        boolean furnaceHasBar = state.getFurnace().has(barItem());
        boolean furnaceHasOre = state.getFurnace().has(oreItem());
        boolean tickPerfectMethod = isNotSteelMethod && state.getConfig().tickPerfectMethod();
        boolean barDispenserFull = state.getFurnace().getQuantity(barItem(), oreItem()) >= 28;
        boolean barDispenserAboutToMakeBars = !coalRun && furnaceHasOre;

		if (tickPerfectMethod && state.getInventory().getFreeSlotsIncludingOresAndBars() == 28) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, barDispenserFull, coalRun, true);
			if (clearBarsAndOres != null) return clearBarsAndOres;
			return state.getBank().isOpen() ? addDummyItemToInventory : openBank;
		}

		if (!state.getBank().isOpen() && coalRun && state.getInventory().has(oreItem())) {
			return openBank;
		}

        if (state.getBank().isOpen()) {
			MethodStep[] clearBarsAndOres = clearInventoryAndBarDispenser(state, barDispenserFull, coalRun, false);
			if (clearBarsAndOres != null) return clearBarsAndOres;

            if (!state.getConfig().useDepositInventory() && hasCoalBag && coalBagEmpty) {
                return fillCoalBag;
            }

            if (coalRun && !state.getInventory().has(ItemID.COAL)) {
                return withdrawCoal;
            }

            if (!coalRun && !state.getInventory().has(oreItem())) {
                return withdrawOre();
            }

			if (state.getConfig().useDepositInventory() && hasCoalBag && coalBagEmpty) {
				return fillCoalBag;
			}
        }

        if (!barDispenserFull && (state.getCoalBag().recentlyEmptiedCoalBag || state.getInventory().has(ItemID.COAL, oreItem()))) {
            state.getCoalBag().recentlyEmptiedCoalBag = false;
            return putOntoConveyorBelt;
        }

		if (hasCoalBag && !barDispenserFull && state.getPlayer().isAtConveyorBelt() && (coalBagFull || (!coalBagEmpty && maxCoalIsThirtySix)) ) {
			return emptyCoalBag;
		}

        if (hasCoalBag && barDispenserFull && state.getInventory().has(ItemID.COAL) && !coalBagFull) {
            return fillCoalBag;
        }

        if (barDispenserFull && state.getInventory().has(ItemID.COAL, oreItem(), barItem())) {
            return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
        }

        if (!tickPerfectMethod && (barDispenserAboutToMakeBars || oreOnConveyor)) {
            return waitForBars;
        }

		if (!state.getInventory().has(barItem()) && ((furnaceHasOre && furnaceHasBar) || (furnaceHasBar && (!tickPerfectMethod || oreOnConveyor)))) {
			return collectBars;
		}

        return openBank;
    }
}
