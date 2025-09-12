package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import net.runelite.api.gameval.ItemID;

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

    private MethodStep[] checkPrerequisite(BlastFurnaceState state)
    {
        if (!state.getInventory().has(ItemID.COAL_BAG, ItemID.COAL_BAG_OPEN)) {
            if (state.getInventory().has(oreItem())) {
                return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
            }
            return state.getBank().isOpen() ? withdrawCoalBag : openBank;
        }

        if (!state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE) &&
            !state.getEquipment().equipped(ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE)) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE)) {
            return equipIceOrSmithsGloves;
        }

        return null;
    }

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        MethodStep[] prerequisite = checkPrerequisite(state);
        if (prerequisite != null) return prerequisite;
        boolean coalRun = state.getFurnace().getQuantity(ItemID.COAL) < 27 * (coalPer() - state.getFurnace().getCoalOffset());
        boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
        boolean furnaceHasBar = state.getFurnace().has(barItem());
        boolean furnaceHasOre = state.getFurnace().has(oreItem());
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
        boolean barDispenserFull = state.getFurnace().getQuantity(barItem(), oreItem()) >= 28;
        boolean barDispenserAboutToMakeBars = !coalRun && furnaceHasOre;

        if (state.getBank().isOpen()) {
            if (state.getInventory().has(barItem()) || barDispenserFull || (coalRun && state.getInventory().has(oreItem()))) {
                return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
            }

            if (state.getFurnace().has(oreItem()) && state.getFurnace().has(barItem())) {
                if (state.getInventory().has(barItem(), oreItem())) {
                    return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
                }
                return collectBars;
            }

            if (coalRun && !state.getInventory().has(ItemID.COAL)) {
                return withdrawCoal;
            }

            if (!coalRun && !state.getInventory().has(oreItem())) {
                return withdrawOre();
            }

			if (state.getCoalBag().isEmpty()) {
				return fillCoalBag;
			}
        }

        if (!barDispenserFull && (state.getCoalBag().recentlyEmptiedCoalBag || state.getInventory().has(ItemID.COAL, oreItem()))) {
            state.getCoalBag().recentlyEmptiedCoalBag = false;
            return putOntoConveyorBelt;
        }

        if (!barDispenserFull && state.getPlayer().isAtConveyorBelt() && !state.getCoalBag().isEmpty()) {
            return emptyCoalBag;
        }

        if (barDispenserFull && state.getInventory().has(ItemID.COAL) && !state.getCoalBag().isFull()) {
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
