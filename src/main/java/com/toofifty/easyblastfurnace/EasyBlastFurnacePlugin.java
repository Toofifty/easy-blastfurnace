package com.toofifty.easyblastfurnace;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.toofifty.easyblastfurnace.overlays.*;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import com.toofifty.easyblastfurnace.utils.ObjectManager;
import com.toofifty.easyblastfurnace.utils.SessionStatistics;
import com.toofifty.easyblastfurnace.utils.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
    name = "Easy Blast Furnace",
    description = "Helps you train at the blast furnace more efficiently"
)
public class EasyBlastFurnacePlugin extends Plugin
{
    public static final int CONVEYOR_BELT = ObjectID.CONVEYOR_BELT;
    public static final int BAR_DISPENSER = NullObjectID.NULL_9092;
    public static final int BANK_CHEST = ObjectID.BANK_CHEST_26707;

    public static final WorldPoint PICKUP_POSITION = new WorldPoint(1940, 4962, 0);

    private static final Pattern COAL_FULL_MESSAGE = Pattern.compile(Strings.COAL_FULL);
    private static final Pattern COAL_EMPTY_MESSAGE = Pattern.compile(Strings.COAL_EMPTY);

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private BlastFurnaceState state;

    @Inject
    private ObjectManager objectManager;

    @Inject
    private InstructionOverlay instructionOverlay;

    @Inject
    private StatisticsOverlay statisticsOverlay;

    @Inject
    private ItemStepOverlay itemStepOverlay;

    @Inject
    private BankItemStepOverlay bankItemStepOverlay;

    @Inject
    private WidgetStepOverlay widgetStepOverlay;

    @Inject
    private ObjectStepOverlay objectStepOverlay;

    @Inject
    private TileStepOverlay tileStepOverlay;

    @Inject
    private CoalBagOverlay coalBagOverlay;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    private SessionStatistics statistics;

    @Getter
    private boolean isEnabled = false;

    @Override
    protected void startUp()
    {
        overlayManager.add(instructionOverlay);
        overlayManager.add(statisticsOverlay);
        overlayManager.add(coalBagOverlay);
        overlayManager.add(itemStepOverlay);
        overlayManager.add(bankItemStepOverlay);
        overlayManager.add(widgetStepOverlay);
        overlayManager.add(objectStepOverlay);
        overlayManager.add(tileStepOverlay);
    }

    @Override
    protected void shutDown()
    {
        statistics.clear();
        methodHandler.clear();

        overlayManager.remove(instructionOverlay);
        overlayManager.remove(statisticsOverlay);
        overlayManager.remove(coalBagOverlay);
        overlayManager.remove(itemStepOverlay);
        overlayManager.remove(bankItemStepOverlay);
        overlayManager.remove(widgetStepOverlay);
        overlayManager.remove(objectStepOverlay);
        overlayManager.remove(tileStepOverlay);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject gameObject = event.getGameObject();

        switch (gameObject.getId()) {
            case BANK_CHEST:
                objectManager.add(gameObject);
                break;
            case CONVEYOR_BELT:
            case BAR_DISPENSER:
                objectManager.add(gameObject);
                isEnabled = true;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject gameObject = event.getGameObject();

        switch (gameObject.getId()) {
            case CONVEYOR_BELT:
            case BAR_DISPENSER:
                if (config.clearMethodOnExit()) methodHandler.clear();
                if (config.clearStatisticsOnExit()) statistics.clear();
                isEnabled = false;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN) {
            if (config.clearMethodOnLogout()) methodHandler.clear();
            if (config.clearStatisticsOnLogout()) statistics.clear();
            isEnabled = false;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (!isEnabled) return;

        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            methodHandler.setMethodFromInventory();
            state.update();
        }

        // handle any inventory or bank changes
        methodHandler.next();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (!isEnabled) return;

        statistics.onFurnaceUpdate();
        state.update();

        // handle furnace ore/bar quantity changes
        methodHandler.next();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if(Objects.equals(event.getGroup(), "easy-blastfurnace") && Objects.equals(event.getKey(), "potionMode")) {
            clientThread.invokeLater(() -> methodHandler.next());
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (!isEnabled) return;
        if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM) return;

        String message = event.getMessage();
        int maxConveyorCount = state.getCoalBag().getMaxCoal() == 27 ? 2 : 3;
        Matcher emptyMatcher = COAL_EMPTY_MESSAGE.matcher(message);
        Matcher filledMatcher = COAL_FULL_MESSAGE.matcher(message);

        if (emptyMatcher.matches()) {
            state.getCoalBag().empty();

        } else if (filledMatcher.matches()) {
            state.getCoalBag().fill();
        }

        if (message.equals("All your ore goes onto the conveyor belt.")) {
            if (state.getInventory().has(ItemID.COAL)) {
                state.getCoalBag().oreOntoConveyor();
            } else {
                state.getCoalBag().oreOntoConveyor(1);
            }
        }

        // After emptying coal bag onto conveyor, ensure coal amount is 0.
        if (maxConveyorCount == state.getCoalBag().getOreOntoConveyorCount()) {
            state.getCoalBag().oreOntoConveyor(0);
            if (state.getCoalBag().getCoal() > 1) state.getCoalBag().setCoal(0);
        }

        // handle coal bag changes
        methodHandler.next();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!isEnabled) return;

        if (event.getMenuOption().equals(Strings.FILL)) state.getCoalBag().fill();
        if (event.getMenuOption().equals(Strings.EMPTY)) state.getCoalBag().empty();
        if (event.getMenuOption().equals(Strings.DRINK)) statistics.drinkStamina();

        // handle coal bag changes
        methodHandler.next();
    }

    @Subscribe
    public void onOverlayMenuClicked(OverlayMenuClicked event)
    {
        if (event.getOverlay() == instructionOverlay &&
            event.getEntry().getOption().equals(InstructionOverlay.RESET_ACTION)) {
            methodHandler.clear();
        }
        if (event.getOverlay() == statisticsOverlay &&
            event.getEntry().getOption().equals(StatisticsOverlay.CLEAR_ACTION)) {
            statistics.clear();
        }
    }

    @Provides
    EasyBlastFurnaceConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(EasyBlastFurnaceConfig.class);
    }
}
