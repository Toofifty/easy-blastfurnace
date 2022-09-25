package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class GoldBarMethod extends Method
{
    private MethodStep checkPrerequisite(BlastFurnaceState state, boolean hasGoldsmithEffect)
    {
        // ensure player has both ice gloves & goldsmith gauntlets either in inventory or equipped

        if (!state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) &&
            !state.getEquipment().equipped(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I)) {
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

        if (!state.getInventory().has(ItemID.GOLDSMITH_GAUNTLETS) && !hasGoldsmithEffect) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        return null;
    }

    @Override
    public MethodStep next(BlastFurnaceState state)
    {
        boolean hasGoldsmithEffect = state.getEquipment().hasGoldsmithEffect();
        MethodStep prerequisite = checkPrerequisite(state, hasGoldsmithEffect);
        if (prerequisite != null) return prerequisite;

        if (state.getInventory().has(ItemID.GOLD_ORE)) {

            if (!hasGoldsmithEffect) {
                return equipGoldsmithGauntlets;
            }

            return putOntoConveyorBelt;
        }

        if (state.getPlayer().hasLoadedOres()) {
            if (!state.getEquipment().equipped(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I)) {
                return equipIceOrSmithsGloves;
            }
            return waitForBars;
        }

        if (state.getFurnace().has(ItemID.GOLD_BAR)) {
            return collectBars;
        }

        if (state.getBank().isOpen()) {
            if (state.getInventory().has(ItemID.GOLD_BAR)) {
                return depositBarsAndOres;
            }

            if (!hasGoldsmithEffect) {
                return equipGoldsmithGauntlets;
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
        return Strings.GOLD;
    }
}
