package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import net.runelite.api.ItemID;

public class GoldBarMethod extends Method
{
    private MethodStep checkPrerequisite(BlastFurnaceState state)
    {
        // ensure player has both ice gloves & goldsmith gauntlets either in inventory or equipped

        if ((!state.getInventory().has(ItemID.ICE_GLOVES) &&
             !state.getEquipment().equipped(ItemID.ICE_GLOVES)) &&
            (!state.getInventory().has(ItemID.SMITHS_GLOVES_I) &&
             !state.getEquipment().equipped(ItemID.SMITHS_GLOVES_I))) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (!state.getInventory().has(ItemID.GOLDSMITH_GAUNTLETS) &&
            !state.getEquipment().equipped(ItemID.GOLDSMITH_GAUNTLETS) &&
            !state.getInventory().has(ItemID.SMITHING_CAPE) &&
            !state.getEquipment().equipped(ItemID.SMITHING_CAPE) &&
            !state.getInventory().has(ItemID.SMITHING_CAPET) &&
            !state.getEquipment().equipped(ItemID.SMITHING_CAPET)) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        return null;
    }

    @Override
    public MethodStep next(BlastFurnaceState state)
    {
        MethodStep prerequisite = checkPrerequisite(state);
        if (prerequisite != null) return prerequisite;

        if (state.getInventory().has(ItemID.GOLD_ORE)) {
            if (!state.getEquipment().equipped(ItemID.GOLDSMITH_GAUNTLETS) &&
                !state.getEquipment().equipped(ItemID.SMITHING_CAPE) &&
                !state.getEquipment().equipped(ItemID.SMITHING_CAPET)) {
                return equipGoldsmithGauntlets;
            }
            return putOntoConveyorBelt;
        }

        if (state.getPlayer().hasLoadedOres()) {
            return waitForBars;
        }

        if (state.getFurnace().has(ItemID.GOLD_BAR)) {
            if (!state.getEquipment().equipped(ItemID.ICE_GLOVES) ||
                !state.getEquipment().equipped(ItemID.SMITHS_GLOVES_I)) {
                return equipIceOrSmithsGloves;
            }
            return collectBars;
        }

        if (state.getBank().isOpen()) {
            if (state.getInventory().has(ItemID.GOLD_BAR)) {
                return depositInventory;
            }

            if (!state.getInventory().has(ItemID.GOLD_ORE)) {
                return withdrawGoldOre;
            }
        }

        return openBank;
    }

    @Override
    public String getName()
    {
        return "Gold bars";
    }
}
