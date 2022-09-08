package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
//import net.runelite.client.plugins.runenergy.RunEnergyPlugin;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;

public class PlayerState
{
    private static final WorldPoint LOAD_POSITION = new WorldPoint(1942, 4967, 0);
    private static final int[] BLAST_FURNACE_WORLDS = new int[]{
        352, 355, 356, 357, 358, 386, 387, 395, 424, 466, 494, 495, 496, 515, 516
    };

    @Accessors(fluent = true)
    @Getter
    @Setter
    private boolean hasLoadedOres = false;

    @Inject
    private Client client;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private FurnaceState furnace;

    @Inject
    private EquipmentState equipment;

    @Inject
    private InventoryState inventory;

    @Inject
    private MethodHandler methodHandler;

    public boolean isAtConveyorBelt()
    {
        Player player = client.getLocalPlayer();
        assert player != null;

        WorldPoint location = player.getWorldLocation();
        return location.distanceTo(LOAD_POSITION) < 2;
    }

    public boolean hasEnoughEnergy()
    {
        if (!config.staminaPotionEnable()) {
            return true;
        }

        // Calculate the amount of energy lost every tick for the next run, and highlight stamina potion when necessary.
        // On a coal trip: 9 ticks with ore, 9 ticks without. 18 ticks or 10800 milliseconds.
        // On a metal ore/gold/hybrid trip: 9 ticks with ore, 5 ticks without, 4 ticks with bars
        Method method = methodHandler.getMethod();
        int inventoryBarsWeight = (int) Math.round(inventory.getWeightOfBarsInInventory());
        int nextOreWeight = (int) Math.round(inventory.getWeightOfNextOresInInventory());
        int staminaPotionEffectVarb = client.getVarbitValue(Varbits.STAMINA_EFFECT);
        final int effectiveWeight = client.getWeight() - inventoryBarsWeight;
        double energyNeeded;
        double lossRate = (Math.min(Math.max(effectiveWeight, 0), 64) / 100.0) + 0.64; // energy lost each tick
        double lossRateOres = (Math.min(Math.max(effectiveWeight + nextOreWeight, 0), 64) / 100.0) + 0.64;
        double lossRateBars = (Math.min(Math.max(effectiveWeight + inventoryBarsWeight, 0), 64) / 100.0) + 0.64;
        final Duration staminaDuration = Duration.of(10L * staminaPotionEffectVarb, RSTimeUnit.GAME_TICKS);
        double offset = staminaDuration.toMillis() == 0 ? 0 : 0.3; // Stamina effect reduces energy depletion to 30%

        // todo: && runEnergyPlugin.getRingOfEnduranceCharges() once Runelite accepts this PR: https://github.com/runelite/runelite/pull/15621.
        if (equipment.equipped(ItemID.RING_OF_ENDURANCE)) {
            lossRate *= 0.85; // Ring of Endurance passive effect reduces energy depletion to 85%
            lossRateOres *= 0.85;
            lossRateBars *= 0.85;
        }

        // The closer to 0 staminaDuration is, the closer to 1 the offset will be, i.e. very little lossRate reduction.
        // The closer to 10800 staminaDuration is, the closer to 0.3 the offset will be. I'm bad at maths this is the best I could do.
        if (staminaDuration.toMillis() < 10800) {
            offset = (0.7 - (0.7 * staminaDuration.toMillis() / 10800)) + 0.3;
        }

        lossRate *= offset;
        lossRateOres *= offset;
        lossRateBars *= offset;

        if (furnace.isCoalRun(method.coalPer())) {
            energyNeeded = Math.ceil(lossRate * 9 + lossRateOres * 9);
        } else {
            energyNeeded = Math.ceil(lossRateOres * 9 + lossRate * 5 + lossRateBars * 4);
        }

        return (client.getEnergy() - energyNeeded) > config.requireStaminaThreshold();
    }

    public boolean isOnBlastFurnaceWorld()
    {
        return Arrays.stream(BLAST_FURNACE_WORLDS).anyMatch(world -> world == client.getWorld());
    }
}
