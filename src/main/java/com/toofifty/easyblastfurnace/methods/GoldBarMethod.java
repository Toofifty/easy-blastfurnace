package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;

@Slf4j
public class GoldBarMethod extends Method
{
    private MethodStep[] checkPrerequisite(BlastFurnaceState state)
    {
        // ensure player has both ice gloves & goldsmith gauntlets either in inventory or equipped

        if (!state.getInventory().has(ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE) && !state.getEquipment().hasIceGlovesEffect()) {
            return state.getBank().isOpen() ? withdrawIceOrSmithsGloves : openBank;
        }

        if (state.getBank().has(ItemID.SKILLCAPE_MAX) &&
                !state.getInventory().has(ItemID.SKILLCAPE_MAX) &&
                !state.getEquipment().equipped(ItemID.SKILLCAPE_MAX)) {
            return state.getBank().isOpen() ? withdrawMaxCape : openBank;
        }

        if (state.getInventory().has(ItemID.SKILLCAPE_MAX) &&
                !state.getEquipment().equipped(ItemID.SKILLCAPE_MAX)) {
            return equipMaxCape;
        }

        if (state.getBank().has(ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED) &&
            !state.getInventory().has(ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED) &&
            !state.getEquipment().equipped(ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED, ItemID.SKILLCAPE_MAX)) {
            return state.getBank().isOpen() ? withdrawSmithingCape : openBank;
        }

        if (state.getInventory().has(ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED) &&
            !state.getEquipment().equipped(ItemID.SKILLCAPE_SMITHING, ItemID.SKILLCAPE_SMITHING_TRIMMED, ItemID.SKILLCAPE_MAX)) {
            return equipSmithingCape;
        }

        if (!state.getInventory().has(ItemID.GAUNTLETS_OF_GOLDSMITHING) && !state.getEquipment().hasGoldsmithEffect()) {
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
        boolean furnaceHasOre = state.getFurnace().has(ItemID.GOLD_ORE);
        boolean tickPerfectMethod = state.getConfig().tickPerfectMethod();
        boolean atBarDispenser = state.getPlayer().isAtBarDispenser();
        boolean atConveyorBelt = state.getPlayer().isAtConveyorBelt();
        boolean useDepositInventory = state.getConfig().useDepositInventory();

        if (state.getBank().isOpen()) {
            if (furnaceHasOre && furnaceHasBar || (!tickPerfectMethod && furnaceHasBar)) {
                if (state.getInventory().has(ItemID.GOLD_BAR, ItemID.GOLD_ORE)) {
                    return useDepositInventory ? depositInventory : depositBarsAndOres;
                }
                return collectBars;
            }

            if (state.getInventory().has(ItemID.GOLD_BAR)) {
                return state.getConfig().useDepositInventory() ? depositInventory : depositBarsAndOres;
            }

            if (tickPerfectMethod && !state.getEquipment().hasGoldsmithEffect() && !state.getEquipment().hasIceGlovesEffect()) {
                return equipGoldsmithGauntlets;
            }

            if (!tickPerfectMethod && !state.getEquipment().hasGoldsmithEffect()) {
                return equipGoldsmithGauntlets;
            }

            if (!state.getInventory().has(ItemID.GOLD_ORE)) {
                return withdrawGoldOre;
            }
        }

        if (tickPerfectMethod && state.getInventory().has(ItemID.GOLD_ORE)) {
            if (furnaceHasBar) {
                return putOntoConveyorBelt;
            } else {
                return putOntoConveyorBeltAndEquipGoldsmithGauntlets;
            }
        }

        if (tickPerfectMethod && (oreOnConveyor || furnaceHasOre) && furnaceHasBar) {
            if (atConveyorBelt) {
                return goToDispenser;
            }

            if (!atBarDispenser) {
                return goToDispenserAndEquipIceOrSmithsGloves;
            }

            return collectBarsAndEquipGoldsmithGauntlets;
        }

        if (!tickPerfectMethod && state.getInventory().has(ItemID.GOLD_ORE)) {
            if (!state.getEquipment().hasGoldsmithEffect()) {
                return equipGoldsmithGauntlets;
            }
            return putOntoConveyorBelt;
        }

		if (!tickPerfectMethod && (oreOnConveyor || furnaceHasOre)) {
			return waitForGoldBars;
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
        return Strings.GOLD;
    }
}
