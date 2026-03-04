package com.toofifty.easyblastfurnace.state;

import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.config.PotionOverlaySetting;
import com.toofifty.easyblastfurnace.utils.StaminaHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import java.util.Arrays;

public class PlayerState
{
    private static final WorldPoint LOAD_POSITION = new WorldPoint(1942, 4967, 0);
    private static final WorldPoint COLLECT_POSITION = new WorldPoint(1940, 4962, 0);
    private static final int[] BLAST_FURNACE_WORLDS = new int[]{
        352, 355, 356, 357, 358, 386, 387, 395, 424, 466, 494, 495, 496, 515, 516
    };

    @Accessors(fluent = true)
    @Getter
    @Setter
    private boolean hasOreOnConveyor = false;

    @Accessors(fluent = true)
    @Getter
    @Setter
    private boolean needsToIngest = false;

    @Inject
    private Client client;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private StaminaHelper staminaHelper;

    public boolean isAtConveyorBelt()
    {
        Player player = client.getLocalPlayer();
        assert player != null;

        WorldPoint location = player.getWorldLocation();
        return location.distanceTo(LOAD_POSITION) < 2;
    }

    public boolean isAtBarDispenser()
    {
        Player player = client.getLocalPlayer();
        assert player != null;

        WorldPoint location = player.getWorldLocation();
        return location.distanceTo(COLLECT_POSITION) < 2;
    }

    public boolean hasEnoughEnergy()
    {
        double energyPercentage = client.getEnergy() / 100.0;
        if (!config.staminaPotionEnable()) {
            return true;
        }
        switch(config.potionOverlayMode()) {
            case STAMINA:
            case EXTENDED_STAMINA:
                return (energyPercentage - staminaHelper.getEnergyNeededForNextRun()) > config.requireStaminaThreshold();
            case STRANGE_FRUIT:
                return (energyPercentage) >= 70;
            case SUPER_ENERGY:
            case SUPER_ENERGY_MIX:
                return (energyPercentage) >= 80;
            case ENERGY:
            case ENERGY_MIX:
                return (energyPercentage) >= 90;
            default:
                return true;
        }
    }

    public boolean isOnBlastFurnaceWorld()
    {
        return Arrays.stream(BLAST_FURNACE_WORLDS).anyMatch(world -> world == client.getWorld());
    }
}
