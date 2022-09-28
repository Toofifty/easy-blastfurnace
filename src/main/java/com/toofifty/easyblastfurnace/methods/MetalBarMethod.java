package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import net.runelite.api.ItemID;

/**
 * Represents a basic method for all regular bars (using coal)
 * - Fill coal until threshold
 * - Do trips with ores
 * - Repeat
 */
abstract public class MetalBarMethod extends Method
{
    public abstract int oreItem();

    protected abstract MethodStep withdrawOre();

    protected abstract int barItem();

    protected abstract int coalPer();

    private MethodStep checkPrerequisite(BlastFurnaceState state)
    {
        if (!state.getInventory().has(ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG)) {
            return state.getBank().isOpen() ? withdrawCoalBag : openBank;
        }

        if (!state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) &&
            !state.getEquipment().equipped(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I)) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I)) {
            return equipIceOrSmithsGloves;
        }

        return null;
    }

    @Override
    public MethodStep next(BlastFurnaceState state, boolean useDepositInventory)
    {
        MethodStep prerequisite = checkPrerequisite(state);
        if (prerequisite != null) return prerequisite;
        boolean coalRun = state.getFurnace().getQuantity(ItemID.COAL) < 27 * (coalPer() - state.getFurnace().getCoalOffset());

        if (state.getInventory().has(ItemID.COAL) || (state.getInventory().has(oreItem()) && !coalRun)) {
            return putOntoConveyorBelt;
        }

        if (state.getPlayer().isAtConveyorBelt() &&
            !state.getCoalBag().isEmpty()) {
            return emptyCoalBag;
        }

        if (state.getPlayer().hasLoadedOres()) {
            return waitForBars;
        }

        if (state.getFurnace().has(barItem())) {
            return collectBars;
        }

        if (state.getBank().isOpen()) {
            if (state.getInventory().has(barItem(), oreItem())) {
                return useDepositInventory ? depositInventory : depositBarsAndOres;
            }

            if (state.getCoalBag().isEmpty()) {
                return fillCoalBag;
            }

            if (coalRun) {
                return withdrawCoal;
            }

            if (!state.getInventory().has(oreItem())) {
                return withdrawOre();
            }
        }

        return openBank;
    }
}
