package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class GoldBarMethod extends Method
{
    private MethodStep[] checkPrerequisite(BlastFurnaceState state)
    {
        // ensure player has both ice gloves & goldsmith gauntlets either in inventory or equipped

        if (!state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I) && !state.getEquipment().hasIceGlovesEffect()) {
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

        if (!state.getInventory().has(ItemID.GOLDSMITH_GAUNTLETS) && !state.getEquipment().hasGoldsmithEffect()) {
            return state.getBank().isOpen() ? withdrawGoldsmithGauntlets : openBank;
        }

        return null;
    }

    @Override
    public MethodStep[] next(BlastFurnaceState state)
    {
        MethodStep[] prerequisite = checkPrerequisite(state);
        if (prerequisite != null) return prerequisite;
        boolean oreOnConveyor = state.getPlayer().hasOreOnConveyor();
        boolean furnaceHasBar = state.getFurnace().has(ItemID.GOLD_BAR);
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();

        if (state.getInventory().has(ItemID.GOLD_ORE)) {
            if (!state.getEquipment().hasGoldsmithEffect()) {
                return equipGoldsmithGauntlets;
            }
            return putOntoConveyorBelt;
        }

		if (!tickPerfectMethod && oreOnConveyor) {
			return waitForBars;
		}

        if (tickPerfectMethod && !state.getPlayer().isAtBarDispenser() && oreOnConveyor) {
            return goToDispenserAndEquipIceOrSmithsGloves;
        }

        if (tickPerfectMethod && state.getPlayer().isAtBarDispenser() && (oreOnConveyor || furnaceHasBar)) {
            return collectBarsAndEquipGoldsmithGauntlets;
        }

        if (!tickPerfectMethod && furnaceHasBar) {
            if (!state.getEquipment().hasIceGlovesEffect()) {
                return equipIceOrSmithsGloves;
            }
            return collectBars;
        }

        if (state.getBank().isOpen()) {
            if (state.getInventory().has(ItemID.GOLD_BAR)) {
                return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
            }

            if (!tickPerfectMethod && !state.getEquipment().hasGoldsmithEffect()) {
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
