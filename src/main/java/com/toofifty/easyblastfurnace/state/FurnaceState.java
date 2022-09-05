package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import net.runelite.api.Client;
import net.runelite.api.ItemID;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FurnaceState
{
    @Inject
    private Client client;

    @Inject
    private EasyBlastFurnaceConfig config;

    private final Map<Integer, Integer> previousQuantity = new HashMap<>();

    public void update()
    {
        for (BarsOres varbit : BarsOres.values()) {
            previousQuantity.put(varbit.getItemID(), getQuantity(varbit.getItemID()));
        }
    }

    public int getChange(int itemId)
    {
        return getQuantity(itemId) - previousQuantity.getOrDefault(itemId, 0);
    }


    public int getCoalOffset()
    {
        if (config.addCoalBuffer()) {
            return 0;
        }
        return 1;
    }

    public int getQuantity(int ...itemIds)
    {
        int total = 0;

        for (int itemId : itemIds) {
            Optional<BarsOres> varbit = Arrays.stream(BarsOres.values()).filter(e -> e.getItemID() == itemId).findFirst();
            assert varbit.isPresent();
            total += client.getVarbitValue(varbit.get().getVarbit());
        }

        return total;
    }

    public boolean has(int ...itemIds) {
        return getQuantity(itemIds) > 0;
    }

    public boolean isCoalRun(int coalPer) {
        int coalInFurnace = getQuantity(ItemID.COAL);
        return coalInFurnace < 27 * (coalPer - getCoalOffset());
    }
}
