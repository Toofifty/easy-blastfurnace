package com.toofifty.easyblastfurnace.methods;

import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.ItemStep;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.Strings;
import net.runelite.api.ItemID;

public class DrinkStaminaMethod extends Method
{
    private final MethodStep withdrawStaminaPotion1 = new ItemStep(ItemID.STAMINA_POTION1, Strings.WITHDRAW_STAMINA_POTION1.getTxt());
    private final MethodStep withdrawStaminaPotion2 = new ItemStep(ItemID.STAMINA_POTION2, Strings.WITHDRAW_STAMINA_POTION2.getTxt());
    private final MethodStep withdrawStaminaPotion3 = new ItemStep(ItemID.STAMINA_POTION3, Strings.WITHDRAW_STAMINA_POTION3.getTxt());
    private final MethodStep withdrawStaminaPotion4 = new ItemStep(ItemID.STAMINA_POTION4, Strings.WITHDRAW_STAMINA_POTION4.getTxt());

    private final MethodStep drinkStaminaPotion1 = new ItemStep(ItemID.STAMINA_POTION1, Strings.DRINK_STAMINA_POTION1.getTxt());
    private final MethodStep drinkStaminaPotion2 = new ItemStep(ItemID.STAMINA_POTION2, Strings.DRINK_STAMINA_POTION2.getTxt());
    private final MethodStep drinkStaminaPotion3 = new ItemStep(ItemID.STAMINA_POTION3, Strings.DRINK_STAMINA_POTION3.getTxt());
    private final MethodStep drinkStaminaPotion4 = new ItemStep(ItemID.STAMINA_POTION4, Strings.DRINK_STAMINA_POTION4.getTxt());

    private final MethodStep getMoreStaminaPotions = new ItemStep(ItemID.COAL_BAG_12019, Strings.GET_MORE_STAMINA_POTIONS.getTxt());

    @Override
    public MethodStep next(BlastFurnaceState state)
    {
        boolean playerHasEnoughEnergy = state.getPlayer().hasEnoughEnergy();
        if (playerHasEnoughEnergy &&
            (state.getInventory().has(ItemID.VIAL, ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3))) {
            return depositInventory;
        }

        if (!state.getBank().isOpen() || playerHasEnoughEnergy) return null;

        if (!state.getInventory().hasFreeSlots()) {
            return depositInventory;
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
        return Strings.DRINKSTAMINA.getTxt();
    }
}
