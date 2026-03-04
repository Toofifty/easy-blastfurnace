package com.toofifty.easyblastfurnace.utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.methods.*;
import com.toofifty.easyblastfurnace.overlays.ItemStepOverlay;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Singleton
public class MethodHandler
{

    @Inject
    private EasyBlastFurnaceConfig easyBlastFurnaceConfig;

    @Inject
    private BlastFurnaceState state;

    private final DrinkPotionMethod drinkPotionMethod = new DrinkPotionMethod();

    @Getter
    private Method method;

    @Getter
    private MethodStep[] steps;

    public void next()
    {
        if (method == null) return;
        if (!state.getPlayer().isOnBlastFurnaceWorld()) return;
        ItemStepOverlay.currentWidgetItem = null;

        steps = drinkPotionMethod.next(state);
        if (steps == null) steps = method.next(state);
    }

    public void clear()
    {
        method = null;
        steps = null;
    }

    private boolean inInventory(int itemId)
    {
        return state.getInventory().has(itemId);
    }

    private Method getMethodFromInventory()
    {
        // ensure method doesn't reset after gold/metal has been removed from inventory
        if (method instanceof GoldHybridMethod) return null;
		if (method instanceof SilverHybridMethod) return null;

        if (inInventory(ItemID.GOLD_ORE) ||
			(method instanceof GoldBarMethod && !inInventory(ItemID.SILVER_ORE))) {

            if (inInventory(ItemID.MITHRIL_ORE) ||
                method instanceof MithrilBarMethod)
                return new MithrilGoldHybridMethod();

            if (inInventory(ItemID.ADAMANTITE_ORE) ||
                method instanceof AdamantiteBarMethod)
                return new AdamantiteGoldHybridMethod();

            if (inInventory(ItemID.RUNITE_ORE) ||
                method instanceof RuniteBarMethod)
                return new RuniteGoldHybridMethod();

            return new GoldBarMethod();
        }

		if (inInventory(ItemID.SILVER_ORE) ||
				method instanceof SilverBarMethod) {

			if (inInventory(ItemID.MITHRIL_ORE) ||
					method instanceof MithrilBarMethod)
				return new MithrilSilverHybridMethod();

			if (inInventory(ItemID.ADAMANTITE_ORE) ||
					method instanceof AdamantiteBarMethod)
				return new AdamantiteSilverHybridMethod();

			if (inInventory(ItemID.RUNITE_ORE) ||
					method instanceof RuniteBarMethod)
				return new RuniteSilverHybridMethod();

			return new SilverBarMethod();
		}

        if (inInventory(ItemID.IRON_ORE)) return new SteelBarMethod();
        if (inInventory(ItemID.MITHRIL_ORE)) return new MithrilBarMethod();
        if (inInventory(ItemID.ADAMANTITE_ORE)) return new AdamantiteBarMethod();
        if (inInventory(ItemID.RUNITE_ORE)) return new RuniteBarMethod();

        return null;
    }

    public void setMethodFromInventory()
    {
        Method method = getMethodFromInventory();
        if (method == null ||
            (this.method != null && this.method.getClass().isInstance(method)))
            return;

        clear();
        this.method = method;
    }
}
