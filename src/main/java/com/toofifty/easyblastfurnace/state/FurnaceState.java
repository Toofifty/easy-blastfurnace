package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.utils.BarsOres;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FurnaceState
{
    @Inject
    private Client client;

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

    public int getQuantity(int itemId)
    {
        Optional<BarsOres> varbit = Arrays.stream(BarsOres.values()).filter(e -> e.getItemID() == itemId).findFirst();
        assert varbit.isPresent();
        return client.getVarbitValue(varbit.get().getVarbit());
    }

    public boolean has(int itemId)
    {
        return getQuantity(itemId) > 0;
    }
}
