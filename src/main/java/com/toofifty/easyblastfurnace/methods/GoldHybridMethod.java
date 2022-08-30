package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import net.runelite.api.ItemID;

abstract public class GoldHybridMethod extends MetalBarMethod
{
    private MethodStep checkPrerequisite(BlastFurnaceState state)
    {
        if (!state.getInventory().has(new int[]{ItemID.COAL_BAG_12019, ItemID.OPEN_COAL_BAG})) {
            return state.getBank().isOpen() ? withdrawCoalBag : openBank;
        }

        if (!state.getInventory().has(new int[]{ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I}) &&
            !state.getEquipment().equipped(new int[]{ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I})) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getBank().has(new int[]{ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET}) &&
            !state.getInventory().has(new int[]{ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET}) &&
            !state.getEquipment().equipped(new int[]{ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET})) {
            return state.getBank().isOpen() ? withdrawSmithingCape : openBank;
        }

        if (!state.getInventory().has(new int[]{ItemID.GOLDSMITH_GAUNTLETS}) &&
            !state.getEquipment().equipped(new int[]{ItemID.GOLDSMITH_GAUNTLETS})) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        if (!state.getEquipment().equipped(new int[]{ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I, ItemID.GOLDSMITH_GAUNTLETS, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET})) {
            return equipGoldsmithGauntlets;
        }

        return null;
    }

    @Override
    public MethodStep next(BlastFurnaceState state)
    {
        MethodStep prerequisite = checkPrerequisite(state);
        if (prerequisite != null) return prerequisite;

        // continue doing gold bars until enough coal has been deposited
        // then do one trip of metal bars

        if (state.getInventory().has(new int[]{ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET}) &&
            !state.getEquipment().equipped(new int[]{ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET})) {
            return equipSmithingCape;
        }

        if (state.getInventory().has(new int[]{ItemID.GOLD_ORE}) &&
            !state.getEquipment().equipped(new int[]{ItemID.GOLDSMITH_GAUNTLETS, ItemID.SMITHING_CAPE, ItemID.SMITHING_CAPET})) {
            return equipGoldsmithGauntlets;
        }

        if (state.getInventory().has(new int[]{ItemID.COAL, ItemID.GOLD_ORE, oreItem()})) {
            return putOntoConveyorBelt;
        }

        if (state.getPlayer().isAtConveyorBelt() &&
            state.getCoalBag().isFull()) {
            return emptyCoalBag;
        }

        if (state.getPlayer().hasLoadedOres()) {
            return waitForBars;
        }

        if (state.getFurnace().has(new int[]{barItem(), ItemID.GOLD_BAR})) {
            if (!state.getEquipment().equipped(new int[]{ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I})) {
                return equipIceOrSmithsGloves;
            }
            return collectBars;
        }

        if (state.getBank().isOpen()) {
            if (state.getInventory().has(new int[]{ItemID.GOLD_BAR, barItem()})) {
                return depositInventory;
            }

            if (!state.getCoalBag().isFull()) {
                return state.getCoalBag().isEmpty() ? fillCoalBag : refillCoalBag;
            }

            if (state.getInventory().has(new int[]{ItemID.GOLD_ORE, oreItem()})) {
                return putOntoConveyorBelt;
            }

            if (state.getFurnace().getQuantity(new int[]{ItemID.COAL}) < 26 * (coalPer() - coalOffset)) {
                return withdrawGoldOre;
            }

            if (!state.getInventory().has(new int[]{oreItem()})) {
                return withdrawOre();
            }
        }

        return openBank;
    }
}
