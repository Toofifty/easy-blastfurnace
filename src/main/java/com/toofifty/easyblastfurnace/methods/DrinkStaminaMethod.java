package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.ItemStep;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class DrinkStaminaMethod extends Method
{
    private final MethodStep withdrawStaminaPotion1 = new ItemStep(Strings.WITHDRAW_STAMINA_POTION1, ItemID.STAMINA_POTION1);
    private final MethodStep withdrawStaminaPotion2 = new ItemStep(Strings.WITHDRAW_STAMINA_POTION2, ItemID.STAMINA_POTION2);
    private final MethodStep withdrawStaminaPotion3 = new ItemStep(Strings.WITHDRAW_STAMINA_POTION3, ItemID.STAMINA_POTION3);
    private final MethodStep withdrawStaminaPotion4 = new ItemStep(Strings.WITHDRAW_STAMINA_POTION4, ItemID.STAMINA_POTION4);

    private final MethodStep drinkStaminaPotion1 = new ItemStep(Strings.DRINK_STAMINA_POTION1, ItemID.STAMINA_POTION1);
    private final MethodStep drinkStaminaPotion2 = new ItemStep(Strings.DRINK_STAMINA_POTION2, ItemID.STAMINA_POTION2);
    private final MethodStep drinkStaminaPotion3 = new ItemStep(Strings.DRINK_STAMINA_POTION3, ItemID.STAMINA_POTION3);
    private final MethodStep drinkStaminaPotion4 = new ItemStep(Strings.DRINK_STAMINA_POTION4, ItemID.STAMINA_POTION4);

    private final MethodStep getMoreStaminaPotions = new ItemStep(Strings.GET_MORE_STAMINA_POTIONS, ItemID.COAL_BAG_12019);

    @Override
    public MethodStep next(BlastFurnaceState state)
    {
        if (!state.getPlayer().needsStamina() &&
            (state.getInventory().has(ItemID.VIAL, ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4))) {
            return depositPotions;
        }

        if (!state.getBank().isOpen() || !state.getPlayer().needsStamina()) return null;

        if (!state.getInventory().has(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4) &&
            !state.getInventory().hasFreeSlots()) {
            return depositBarsAndOres;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION1)) {
            return drinkStaminaPotion1;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION2)) {
            return drinkStaminaPotion2;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION3)) {
            return drinkStaminaPotion3;
        }

        if (state.getInventory().has(ItemID.STAMINA_POTION4)) {
            return drinkStaminaPotion4;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION1)) {
            return withdrawStaminaPotion1;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION2)) {
            return withdrawStaminaPotion2;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION3)) {
            return withdrawStaminaPotion3;
        }

        if (state.getBank().has(ItemID.STAMINA_POTION4)) {
            return withdrawStaminaPotion4;
        }

        return getMoreStaminaPotions;
    }

    @Override
    public String getName()
    {
        return Strings.DRINK_STAMINA;
    }
}
