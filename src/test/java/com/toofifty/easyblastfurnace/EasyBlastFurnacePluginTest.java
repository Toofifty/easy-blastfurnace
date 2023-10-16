package com.toofifty.easyblastfurnace;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.toofifty.easyblastfurnace.config.PotionOverlaySetting;
import com.toofifty.easyblastfurnace.overlays.InstructionOverlay;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.BarsOres;
import com.toofifty.easyblastfurnace.utils.CoalPer;
import com.toofifty.easyblastfurnace.utils.Strings;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import com.toofifty.easyblastfurnace.utils.StaminaHelper;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.inject.testing.fieldbinder.Bind;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EasyBlastFurnacePluginTest {

    @Inject
    private BlastFurnaceState state;

    @Inject
    EasyBlastFurnacePlugin easyBlastFurnacePlugin;

    @Inject
    private InstructionOverlay instructionOverlay;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    private StaminaHelper staminaHelper;

    @Mock
    @Bind
    private Client client;

    @Mock
    @Bind
    private GameObject patchObject;

    @Mock
    @Bind
    private RuneLiteConfig runeLiteConfig;

    @Mock
    @Bind
    private ConfigManager configManager;

    @Mock
    @Bind
    private ItemManager itemManager;

    @Mock
    @Bind
    private EasyBlastFurnaceConfig easyBlastFurnaceConfig;

    private final ItemContainer bankContainer = mock(ItemContainer.class);
    private final ItemContainer inventoryContainer = mock(ItemContainer.class);
    private final ItemContainer equipmentContainer = mock(ItemContainer.class);
    private final Player localPlayer = mock(Player.class);
    private final Widget bankWidget = mock(Widget.class);
    private final VarbitChanged blastFurnaceChange = new VarbitChanged();
    private final ItemContainerChanged event = new ItemContainerChanged(InventoryID.INVENTORY.getId(), inventoryContainer);
    private final MenuOptionClicked menuOptionClicked = mock(MenuOptionClicked.class);

    private final WorldPoint atConveyorBelt = new WorldPoint(1942, 4967, 0);
    private final WorldPoint notAtConveyorBelt = new WorldPoint(1949, 4967, 0);
    private final WorldPoint atBarDispenser = new WorldPoint(1940, 4963, 0);

    private int tickCount = 0;

    @Before
    public void before()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        GameObjectSpawned gameObjectSpawned = new GameObjectSpawned();
        gameObjectSpawned.setGameObject(patchObject);
        when(client.getWorld()).thenReturn(358);
        when(patchObject.getId()).thenReturn(ObjectID.BANK_CHEST_26707);
        easyBlastFurnacePlugin.onGameObjectSpawned(gameObjectSpawned);
        gameObjectSpawned.setGameObject(patchObject);
        when(patchObject.getId()).thenReturn(ObjectID.CONVEYOR_BELT);
        easyBlastFurnacePlugin.onGameObjectSpawned(gameObjectSpawned);
        gameObjectSpawned.setGameObject(patchObject);
        when(patchObject.getId()).thenReturn(ObjectID.BAR_DISPENSER);
        easyBlastFurnacePlugin.onGameObjectSpawned(gameObjectSpawned);

        when(client.getItemContainer(InventoryID.BANK)).thenReturn(bankContainer);
        when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inventoryContainer);
        when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(equipmentContainer);

        when(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER)).thenReturn(bankWidget);

        when(client.getLocalPlayer()).thenReturn(localPlayer);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));
        when(easyBlastFurnaceConfig.addCoalBuffer()).thenReturn(true);
        when(easyBlastFurnaceConfig.potionOverlayMode()).thenReturn(PotionOverlaySetting.STAMINA);
        assertTrue(easyBlastFurnacePlugin.isEnabled());
    }

    @Test
    public void steelBarMethod()
    {
        metalMethod(
            ItemID.IRON_ORE, BarsOres.IRON_ORE.getVarbit(),
            ItemID.STEEL_BAR, BarsOres.STEEL_BAR.getVarbit(),
            Strings.WITHDRAW_IRON_ORE, Strings.STEEL,
            CoalPer.IRON.getValue()
        );
    }

    @Test
    public void mithrilBarMethod()
    {
        metalMethod(
            ItemID.MITHRIL_ORE, BarsOres.MITHRIL_ORE.getVarbit(),
            ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.getVarbit(),
            Strings.WITHDRAW_MITHRIL_ORE, Strings.MITHRIL,
            CoalPer.MITHRIL.getValue()
        );
    }

    @Test
    public void adamantiteBarMethod()
    {
        metalMethod(
            ItemID.ADAMANTITE_ORE, BarsOres.ADAMANTITE_ORE.getVarbit(),
            ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.getVarbit(),
            Strings.WITHDRAW_ADAMANTITE_ORE, Strings.ADAMANTITE,
            CoalPer.ADAMANTITE.getValue()
        );
    }

    @Test
    public void runiteBarMethod()
    {
        metalMethod(
            ItemID.RUNITE_ORE, BarsOres.RUNITE_ORE.getVarbit(),
            ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.getVarbit(),
            Strings.WITHDRAW_RUNITE_ORE, Strings.RUNITE,
            CoalPer.RUNITE.getValue()
        );
    }

    @Test
    public void mithrilHybridMethod()
    {
        hybridMethod(
            ItemID.MITHRIL_ORE, BarsOres.MITHRIL_ORE.getVarbit(),
            ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.getVarbit(),
            Strings.WITHDRAW_MITHRIL_ORE, Strings.MITHRILHYBRID,
            CoalPer.MITHRIL.getValue(), false
        );
    }

    @Test
    public void adamantiteHybridMethod()
    {
        hybridMethod(
            ItemID.ADAMANTITE_ORE, BarsOres.ADAMANTITE_ORE.getVarbit(),
            ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.getVarbit(),
            Strings.WITHDRAW_ADAMANTITE_ORE, Strings.ADAMANTITEHYBRID,
            CoalPer.ADAMANTITE.getValue(), false
        );
    }

    @Test
    public void runiteHybridMethod()
    {
        hybridMethod(
            ItemID.RUNITE_ORE, BarsOres.RUNITE_ORE.getVarbit(),
            ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.getVarbit(),
            Strings.WITHDRAW_RUNITE_ORE, Strings.RUNITEHYBRID,
            CoalPer.RUNITE.getValue(), false
        );
    }

    @Test
    public void runiteHybridTickPerfectMethod()
    {
        when(easyBlastFurnaceConfig.tickPerfectMethod()).thenReturn(true);
        hybridMethod(
            ItemID.RUNITE_ORE, BarsOres.RUNITE_ORE.getVarbit(),
            ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.getVarbit(),
            Strings.WITHDRAW_RUNITE_ORE, Strings.RUNITEHYBRID,
            CoalPer.RUNITE.getValue(), true
        );
    }

    @Test
    public void goldBarMethod()
    {
        goldSharedMethod(false);
    }

    @Test
    public void goldBarTickPerfectMethod()
    {
        when(easyBlastFurnaceConfig.tickPerfectMethod()).thenReturn(true);
        goldSharedMethod(true);
    }

    @Test
    public void drinkStaminaMethod()
    {
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(false);
        assertTrue(state.getPlayer().hasEnoughEnergy());

        // setup
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(true);
        when(client.getWeight()).thenReturn(54);
        when(client.getBoostedSkillLevel(Skill.AGILITY)).thenReturn(35);
        setInventoryItems(new Item[0]);
        setEquipmentCount(ItemID.SMITHING_CAPE, 1);

        checkStaminaHelper(); // Check energy calculation

        // deposit inventory
        when(easyBlastFurnaceConfig.requireStaminaThreshold()).thenReturn(50);
        when(client.getEnergy()).thenReturn(6400);
        setInventoryCount(ItemID.VIAL, 1);
        assertStepTooltip(Strings.DEPOSIT_STAMINA_POTIONS);

        // Second deposit Inventory
        when(client.getEnergy()).thenReturn(4900);
        Item[] gold = new Item[28];
        for (int i = 0; i < 28; i++) {
            gold[i] = new Item(ItemID.GOLD_ORE, 1);
        }
        setInventoryItems(gold);
        assertStepTooltip(Strings.DEPOSIT_BARS_AND_ORES);

        // drink/withdraw stamina potions
        setInventoryItems(new Item[0]);
        checkStaminaPotion(ItemID.STAMINA_POTION4, ItemID.STAMINA_POTION1, Strings.DRINK_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, Strings.DRINK_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, Strings.DRINK_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4, Strings.DRINK_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION4, ItemID.STAMINA_POTION1, Strings.WITHDRAW_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, Strings.WITHDRAW_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, Strings.WITHDRAW_STAMINA_POTION);
        checkStaminaPotion(ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4, Strings.WITHDRAW_STAMINA_POTION);

        // getMoreStaminaPotions
        setBankCount(ItemID.STAMINA_POTION4, 0);
        assertStepTooltip(Strings.GET_MORE_STAMINA_POTIONS);
    }

    @Test
    public void drinkSuperEnergyMethod()
    {
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(false);
        assertTrue(state.getPlayer().hasEnoughEnergy());

        // setup
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(true);
        when(client.getWeight()).thenReturn(54);
        when(client.getBoostedSkillLevel(Skill.AGILITY)).thenReturn(35);
        when(easyBlastFurnaceConfig.potionOverlayMode()).thenReturn(PotionOverlaySetting.SUPER_ENERGY);
        setInventoryItems(new Item[0]);
        setEquipmentCount(ItemID.SMITHING_CAPE, 1);

        checkStaminaHelper();

        when(easyBlastFurnaceConfig.requireStaminaThreshold()).thenReturn(20);
        when(client.getEnergy()).thenReturn(8400);
        setInventoryCount(ItemID.VIAL, 1);
        assertStepTooltip(Strings.DEPOSIT_SUPER_ENERGY_POTIONS);

        setInventoryItems(new Item[0]);
        // Second deposit Inventory
        when(client.getEnergy()).thenReturn(1200);
        Item[] gold = new Item[28];
        for (int i = 0; i < 28; i++) {
            gold[i] = new Item(ItemID.GOLD_ORE, 1);
        }
        setInventoryItems(gold);
        assertStepTooltip(Strings.DEPOSIT_BARS_AND_ORES);

        // drink/withdraw stamina potions
        setInventoryItems(new Item[0]);
        checkStaminaPotion(ItemID.SUPER_ENERGY4, ItemID.SUPER_ENERGY1, Strings.DRINK_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY1, ItemID.SUPER_ENERGY2, Strings.DRINK_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY2, ItemID.SUPER_ENERGY3, Strings.DRINK_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY3, ItemID.SUPER_ENERGY4, Strings.DRINK_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY4, ItemID.SUPER_ENERGY1, Strings.WITHDRAW_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY1, ItemID.SUPER_ENERGY2, Strings.WITHDRAW_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY2, ItemID.SUPER_ENERGY3, Strings.WITHDRAW_SUPER_ENERGY_POTION);
        checkStaminaPotion(ItemID.SUPER_ENERGY3, ItemID.SUPER_ENERGY4, Strings.WITHDRAW_SUPER_ENERGY_POTION);

        when(easyBlastFurnaceConfig.potionOverlayMode()).thenReturn(PotionOverlaySetting.SUPER_ENERGY);

        setBankCount(ItemID.SUPER_ENERGY1, 0);
        setBankCount(ItemID.SUPER_ENERGY2, 0);
        setBankCount(ItemID.SUPER_ENERGY3, 1);
        setBankCount(ItemID.SUPER_ENERGY4, 0);
        assertStepTooltip(Strings.WITHDRAW_SUPER_ENERGY_POTION);
        setBankCount(ItemID.SUPER_ENERGY1, 0);
        setBankCount(ItemID.SUPER_ENERGY2, 1);
        setBankCount(ItemID.SUPER_ENERGY3, 0);
        setBankCount(ItemID.SUPER_ENERGY4, 0);
        assertStepTooltip(Strings.WITHDRAW_SUPER_ENERGY_POTION);
        setBankCount(ItemID.SUPER_ENERGY1, 1);
        setBankCount(ItemID.SUPER_ENERGY2, 0);
        setBankCount(ItemID.SUPER_ENERGY3, 0);
        setBankCount(ItemID.SUPER_ENERGY4, 0);
        assertStepTooltip(Strings.WITHDRAW_SUPER_ENERGY_POTION);
        setBankCount(ItemID.SUPER_ENERGY1, 1);
        setBankCount(ItemID.SUPER_ENERGY2, 1);
        setBankCount(ItemID.SUPER_ENERGY3, 1);
        setBankCount(ItemID.SUPER_ENERGY4, 1);
        assertStepTooltip(Strings.WITHDRAW_SUPER_ENERGY_POTION);
        // getMoreStaminaPotions
        setBankCount(ItemID.SUPER_ENERGY1, 0);
        setBankCount(ItemID.SUPER_ENERGY2, 0);
        setBankCount(ItemID.SUPER_ENERGY3, 0);
        setBankCount(ItemID.SUPER_ENERGY4, 0);
        assertStepTooltip(Strings.GET_MORE_SUPER_ENERGY_POTIONS);
    }

    @Test
    public void drinkEnergyMethod()
    {
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(false);
        assertTrue(state.getPlayer().hasEnoughEnergy());

        // setup
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(true);
        when(client.getWeight()).thenReturn(54);
        when(client.getBoostedSkillLevel(Skill.AGILITY)).thenReturn(35);
        when(easyBlastFurnaceConfig.potionOverlayMode()).thenReturn(PotionOverlaySetting.ENERGY);
        setInventoryItems(new Item[0]);
        setEquipmentCount(ItemID.SMITHING_CAPE, 1);

        checkStaminaHelper();

        when(easyBlastFurnaceConfig.requireStaminaThreshold()).thenReturn(20);
        when(client.getEnergy()).thenReturn(9400);
        setInventoryCount(ItemID.VIAL, 1);
        assertStepTooltip(Strings.DEPOSIT_ENERGY_POTIONS);

        setInventoryItems(new Item[0]);
        // Second deposit Inventory
        when(client.getEnergy()).thenReturn(1200);
        Item[] gold = new Item[28];
        for (int i = 0; i < 28; i++) {
            gold[i] = new Item(ItemID.GOLD_ORE, 1);
        }
        setInventoryItems(gold);
        assertStepTooltip(Strings.DEPOSIT_BARS_AND_ORES);

        // drink/withdraw stamina potions
        setInventoryItems(new Item[0]);
        checkStaminaPotion(ItemID.ENERGY_POTION1, ItemID.ENERGY_POTION1, Strings.DRINK_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION1, ItemID.ENERGY_POTION2, Strings.DRINK_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION2, ItemID.ENERGY_POTION3, Strings.DRINK_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION3, ItemID.ENERGY_POTION4, Strings.DRINK_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION4, ItemID.ENERGY_POTION1, Strings.WITHDRAW_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION1, ItemID.ENERGY_POTION2, Strings.WITHDRAW_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION2, ItemID.ENERGY_POTION3, Strings.WITHDRAW_ENERGY_POTION);
        checkStaminaPotion(ItemID.ENERGY_POTION3, ItemID.ENERGY_POTION4, Strings.WITHDRAW_ENERGY_POTION);

        when(easyBlastFurnaceConfig.potionOverlayMode()).thenReturn(PotionOverlaySetting.ENERGY);

        setBankCount(ItemID.ENERGY_POTION1, 0);
        setBankCount(ItemID.ENERGY_POTION2, 0);
        setBankCount(ItemID.ENERGY_POTION3, 1);
        setBankCount(ItemID.ENERGY_POTION4, 0);
        assertStepTooltip(Strings.WITHDRAW_ENERGY_POTION);
        setBankCount(ItemID.ENERGY_POTION1, 0);
        setBankCount(ItemID.ENERGY_POTION2, 1);
        setBankCount(ItemID.ENERGY_POTION3, 0);
        setBankCount(ItemID.ENERGY_POTION4, 0);
        assertStepTooltip(Strings.WITHDRAW_ENERGY_POTION);
        setBankCount(ItemID.ENERGY_POTION1, 1);
        setBankCount(ItemID.ENERGY_POTION2, 0);
        setBankCount(ItemID.ENERGY_POTION3, 0);
        setBankCount(ItemID.ENERGY_POTION4, 0);
        assertStepTooltip(Strings.WITHDRAW_ENERGY_POTION);
        setBankCount(ItemID.ENERGY_POTION1, 1);
        setBankCount(ItemID.ENERGY_POTION2, 1);
        setBankCount(ItemID.ENERGY_POTION3, 1);
        setBankCount(ItemID.ENERGY_POTION4, 1);
        assertStepTooltip(Strings.WITHDRAW_ENERGY_POTION);
        // getMoreStaminaPotions
        setBankCount(ItemID.ENERGY_POTION1, 0);
        setBankCount(ItemID.ENERGY_POTION2, 0);
        setBankCount(ItemID.ENERGY_POTION3, 0);
        setBankCount(ItemID.ENERGY_POTION4, 0);
        assertStepTooltip(Strings.GET_MORE_ENERGY_POTIONS);
    }

    private void goldSharedMethod(boolean tickPerfect)
    {
        setInventoryItems(new Item[]{new Item(ItemID.GOLD_ORE, 27)});
        setInventoryCount(ItemID.GOLD_ORE, 27);
        assertEquals(Strings.GOLD, methodHandler.getMethod().getName());
        assertStepTooltip(Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES);

        setInventoryCount(ItemID.ICE_GLOVES, 1);
        setBankCount(ItemID.SMITHING_CAPE, 1);
        assertStepTooltip(Strings.WITHDRAW_SMITHING_CAPE);

        setInventoryCount(ItemID.SMITHING_CAPE, 1);
        setBankCount(ItemID.SMITHING_CAPE, 0);
        assertStepTooltip(Strings.EQUIP_SMITHING_CAPE);

        setInventoryCount(ItemID.SMITHING_CAPE, 0);
        setBankCount(ItemID.MAX_CAPE, 1);
        assertStepTooltip(Strings.WITHDRAW_MAX_CAPE);

        setInventoryCount(ItemID.MAX_CAPE, 1);
        setBankCount(ItemID.MAX_CAPE, 0);
        assertStepTooltip(Strings.EQUIP_MAX_CAPE);

        setInventoryCount(ItemID.MAX_CAPE, 0);
        assertStepTooltip(Strings.WITHDRAW_GOLDSMITH_GAUNTLETS);

        setInventoryCount(ItemID.GOLDSMITH_GAUNTLETS, 1);
        assertStepTooltip(Strings.EQUIP_GOLDSMITH_GAUNTLETS);

        setInventoryCount(ItemID.GOLDSMITH_GAUNTLETS, 0);
        setEquipmentCount(ItemID.GOLDSMITH_GAUNTLETS, 1);
        assertStepTooltip(Strings.PUT_ORE_ONTO_CONVEYOR_BELT);

        setAtBank(false);
        setWorldPoint(atConveyorBelt);
        setInventoryCount(ItemID.GOLD_ORE, 0);

        if (tickPerfect) {
            goldTickPerfect();
        } else {
            gold();
        }
    }

    private void goldTickPerfect()
    {
        setWorldPoint(atConveyorBelt);
        setInventoryCount(ItemID.GOLD_ORE, 0);
        setFurnaceCount(BarsOres.GOLD_BAR.getVarbit(), 27);
        assertStepTooltip(Strings.GO_TO_DISPENSER_AND_EQUIP_ICE_OR_SMITHS_GLOVES);

        setWorldPoint(atBarDispenser);
        assertStepTooltip(Strings.EQUIP_GOLDSMITH_GAUNTLETS_AFTER_COLLECT_BARS);
    }

    private void gold()
    {
        assertStepTooltip(Strings.WAIT_FOR_BARS);

        setWorldPoint(atBarDispenser);
        setFurnaceCount(BarsOres.GOLD_BAR.getVarbit(), 27);
        assertStepTooltip(Strings.EQUIP_ICE_OR_SMITHS_GLOVES);

        equipGloves(true);
        assertStepTooltip(Strings.COLLECT_BARS);

        setInventoryCount(ItemID.GOLD_BAR, 27);
        setFurnaceCount(BarsOres.GOLD_BAR.getVarbit(), 0);

        assertStepTooltip(Strings.OPEN_BANK);

        setAtBank(true);
        assertStepTooltip(Strings.DEPOSIT_BARS_AND_ORES);

        setInventoryCount(ItemID.GOLD_BAR, 0);
        assertStepTooltip(Strings.EQUIP_GOLDSMITH_GAUNTLETS);

        equipGloves(false);
        assertStepTooltip(Strings.WITHDRAW_GOLD_ORE);
    }

    private void checkStaminaPotion(int staminaPotionA, int staminaPotionB, String methodStep)
    {
        setInventoryCount(staminaPotionA, 0);
        setBankCount(staminaPotionA, 0);
        if (methodStep.toLowerCase().contains("withdraw")) setBankCount(staminaPotionB, 1);
        else setInventoryCount(staminaPotionB, 1);
        assertStepTooltip(methodStep);
    }

    private void checkStaminaHelper()
    {
        // Check StaminaHelper works for all methods: coal and ore runs. This catches issues with strings changing too.
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(254);
        when(configManager.getRSProfileConfiguration("runenergy", "ringOfEnduranceCharges", Integer.class)).thenReturn(500);
        setEquipmentCount(ItemID.RING_OF_ENDURANCE, 1);
        runThroughBarMethods(ItemID.IRON_ORE,ItemID.MITHRIL_ORE,ItemID.ADAMANTITE_ORE,ItemID.RUNITE_ORE);
        assertFalse(state.getFurnace().isCoalRunNext(CoalPer.getValueFromString(methodHandler.getMethod().toString())));
        assertEquals(17, (int) staminaHelper.getEnergyNeededForNextRun());

        when(client.getVarbitValue(Varbits.STAMINA_EFFECT)).thenReturn(1);
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(0);
        runThroughBarMethods(ItemID.IRON_ORE,ItemID.MITHRIL_ORE,ItemID.ADAMANTITE_ORE,ItemID.RUNITE_ORE);
        assertTrue(state.getFurnace().isCoalRunNext(CoalPer.getValueFromString(methodHandler.getMethod().toString())));
        assertEquals(13, (int) staminaHelper.getEnergyNeededForNextRun());
    }

    private void runThroughBarMethods(int ...ores)
    {
        for (int i = 0; i < ores.length; i++) {
            resetMethod();
            setInventoryCount(ores[i], 1);
            setInventoryCount(ItemID.GOLD_ORE, 1);
            if (i != 0) setInventoryCount(ores[i - 1], 0);
            resetMethod();
            setInventoryCount(ItemID.GOLD_ORE, 0);
        }
    }

    private void resetMethod()
    {
        OverlayMenuEntry entry = new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, InstructionOverlay.RESET_ACTION, null);
        OverlayMenuClicked overlayMenuClickedEvent = new OverlayMenuClicked(entry, instructionOverlay);
        easyBlastFurnacePlugin.onOverlayMenuClicked(overlayMenuClickedEvent);
    }

    private void metalMethod(
        int oreID, int oreVarbit, int barID, int barVarbit, String withdrawOreText, String methodName, int coalPer
    ) {
        setInventoryItems(new Item[]{new Item(oreID, 1)});
        setInventoryCount(oreID, 1);
        assertEquals(methodName, methodHandler.getMethod().getName());
        assertStepTooltip(Strings.WITHDRAW_COAL_BAG);

        setInventoryCount(ItemID.OPEN_COAL_BAG, 1);
        assertStepTooltip(Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES);

        setInventoryCount(ItemID.ICE_GLOVES, 1);
        assertStepTooltip(Strings.EQUIP_ICE_OR_SMITHS_GLOVES);

        setInventoryCount(ItemID.ICE_GLOVES, 0);
        setEquipmentCount(ItemID.ICE_GLOVES, 1);
        assertStepTooltip(Strings.DEPOSIT_BARS_AND_ORES);

        setInventoryCount(oreID, 0);
        assertStepTooltip(Strings.FILL_COAL_BAG);

        setCoalBag(Strings.FILL);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());
        assertStepTooltip(Strings.WITHDRAW_COAL);

        setFurnaceCount(BarsOres.COAL.getVarbit(), 27 * (coalPer - state.getFurnace().getCoalOffset()));
        assertStepTooltip(withdrawOreText);

        setInventoryCount(oreID, 1);
        assertStepTooltip(Strings.PUT_ORE_ONTO_CONVEYOR_BELT);

        goToAndLoadFurnace(oreID, oreVarbit);
        assertStepTooltip(Strings.EMPTY_COAL_BAG);

        setCoalBag(Strings.EMPTY);
        assertEquals(0, state.getCoalBag().getCoal());
        setFurnaceCount(BarsOres.COAL.getVarbit(), state.getCoalBag().getCoal());
        assertStepTooltip(Strings.WAIT_FOR_BARS);

        setWorldPoint(notAtConveyorBelt);
        setFurnaceCount(oreVarbit, 0);
        setFurnaceCount(barVarbit, 1);
        assertStepTooltip(Strings.COLLECT_BARS);

        collectBars(barID, barVarbit);
        assertStepTooltip(Strings.OPEN_BANK);
    }

    private void hybridMethod(
        int oreID, int oreVarbit, int barID, int barVarbit, String withdrawOreText, String methodName, int coalPer,
        boolean tickPerfect
    ) {
        setInventoryItems(new Item[]{ new Item(ItemID.OPEN_COAL_BAG, 1), new Item(oreID, 1) });
        setInventoryCount(oreID, 1);
        setInventoryCount(ItemID.GOLD_ORE, 1);
        assertEquals(methodName, methodHandler.getMethod().getName());
        assertStepTooltip(Strings.WITHDRAW_COAL_BAG);

        setInventoryCount(ItemID.GOLD_ORE, 0);
        setInventoryCount(ItemID.OPEN_COAL_BAG, 1);
        assertStepTooltip(Strings.WITHDRAW_ICE_OR_SMITHS_GLOVES);

        setInventoryCount(ItemID.ICE_GLOVES, 1);
        setBankCount(ItemID.SMITHING_CAPE, 1);
        assertStepTooltip(Strings.WITHDRAW_SMITHING_CAPE);

        setInventoryCount(ItemID.SMITHING_CAPE, 1);
        setBankCount(ItemID.SMITHING_CAPE, 0);
        assertStepTooltip(Strings.EQUIP_SMITHING_CAPE);

        setInventoryCount(ItemID.SMITHING_CAPE, 0);
        setBankCount(ItemID.MAX_CAPE, 1);
        assertStepTooltip(Strings.WITHDRAW_MAX_CAPE);

        setInventoryCount(ItemID.MAX_CAPE, 1);
        setBankCount(ItemID.MAX_CAPE, 0);
        assertStepTooltip(Strings.EQUIP_MAX_CAPE);

        setInventoryCount(ItemID.MAX_CAPE, 0);
        assertStepTooltip(Strings.WITHDRAW_GOLDSMITH_GAUNTLETS);

        setInventoryCount(ItemID.GOLDSMITH_GAUNTLETS, 1);
        assertStepTooltip(Strings.EQUIP_GOLDSMITH_GAUNTLETS);
		setInventoryCount(ItemID.GOLDSMITH_GAUNTLETS, 0);
		setEquipmentCount(ItemID.GOLDSMITH_GAUNTLETS, 1);

		setInventoryCount(oreID, 0);
		assertStepTooltip(Strings.WITHDRAW_GOLD_ORE);

		setInventoryCount(oreID, 1);
		assertStepTooltip(Strings.FILL_COAL_BAG);
		setCoalBag(Strings.FILL);
		assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());

        setFurnaceCount(BarsOres.COAL.getVarbit(), 27 * (coalPer - state.getFurnace().getCoalOffset()));
		setInventoryCount(oreID, 0);
		assertStepTooltip(withdrawOreText);

        setInventoryCount(oreID, 1);
        assertStepTooltip(Strings.PUT_ORE_ONTO_CONVEYOR_BELT);

        goToAndLoadFurnace(oreID, oreVarbit);
        assertStepTooltip(Strings.EMPTY_COAL_BAG);

        setCoalBag(Strings.EMPTY);
        assertEquals(1, state.getCoalBag().getCoal());
        setFurnaceCount(BarsOres.COAL.getVarbit(), state.getCoalBag().getCoal());

        if (tickPerfect) {
            hybridTickPerfect(oreID, barVarbit, coalPer);
        } else {
            hybrid(oreVarbit, barID, barVarbit);
        }
    }

    private void hybrid(int oreVarbit, int barID, int barVarbit)
    {
        assertStepTooltip(Strings.WAIT_FOR_BARS);

        setWorldPoint(atBarDispenser);
        setFurnaceCount(oreVarbit, 0);
        setFurnaceCount(barVarbit, 2);
        assertStepTooltip(Strings.EQUIP_ICE_OR_SMITHS_GLOVES);

        equipGloves(true);
        assertStepTooltip(Strings.COLLECT_BARS);

        collectBars(barID, barVarbit);
        assertStepTooltip(Strings.OPEN_BANK);

        setAtBank(true);
        assertStepTooltip(Strings.DEPOSIT_BARS_AND_ORES);

        setInventoryCount(barID, 0);
        assertStepTooltip(Strings.WITHDRAW_GOLD_ORE);

        setInventoryCount(ItemID.GOLD_ORE, 1);
        assertStepTooltip(Strings.REFILL_COAL_BAG);

        setCoalBag(Strings.FILL);
        assertStepTooltip(Strings.PUT_ORE_ONTO_CONVEYOR_BELT);

        setAtBank(false);
        assertStepTooltip(Strings.EQUIP_GOLDSMITH_GAUNTLETS);
    }

    private void hybridTickPerfect(int oreID, int barVarbit, int coalPer)
    {
        setFurnaceCount(BarsOres.COAL.getVarbit(), 27 * (coalPer - state.getFurnace().getCoalOffset()));
        setInventoryCount(oreID, 1);
        setWorldPoint(atConveyorBelt);
        setInventoryCount(oreID, 0);
        setFurnaceCount(barVarbit, 1);
        assertStepTooltip(Strings.GO_TO_DISPENSER_AND_EQUIP_ICE_OR_SMITHS_GLOVES);

        setWorldPoint(atBarDispenser);
        setFurnaceCount(barVarbit, 1);
        assertStepTooltip(Strings.EQUIP_GOLDSMITH_GAUNTLETS_AFTER_COLLECT_BARS);
    }

    private void collectBars(int barID, int barVarbit)
    {
        setWorldPoint(notAtConveyorBelt);
        setInventoryCount(barID, 1);
        setFurnaceCount(barVarbit, 0);
    }

    private void goToAndLoadFurnace(int oreID, int oreVarbit)
    {
        setAtBank(false);
        setWorldPoint(atConveyorBelt);
        setInventoryCount(oreID, 0);
        setFurnaceCount(oreVarbit, 1);
    }

    private void setInventoryCount(int itemID, int count)
    {
        when(inventoryContainer.count(itemID)).thenReturn(count);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void setEquipmentCount(int itemID, int count)
    {
        when(equipmentContainer.count(itemID)).thenReturn(count);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void setBankCount(int itemID, int count)
    {
        when(bankContainer.count(itemID)).thenReturn(count);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void setFurnaceCount(int varbit, int count)
    {
        when(client.getVarbitValue(varbit)).thenReturn(count);
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
    }

    private void setWorldPoint(WorldPoint worldPoint)
    {
        when(localPlayer.getWorldLocation()).thenReturn(worldPoint);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void setAtBank(boolean atBank) {
        when(bankWidget.isHidden()).thenReturn(!atBank);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void assertStepTooltip(String expectedStrings)
    {
        MethodStep[] steps = methodHandler.getSteps();
        assertEquals(expectedStrings, steps[steps.length - 1].getTooltip());

    }

    private void setCoalBag(String emptyOrFillText)
    {
        // Empty coal bag to get Wait for bars step
        tickCount = tickCount + 1;
        when(client.getTickCount()).thenReturn(tickCount);
        when(menuOptionClicked.getMenuOption()).thenReturn(emptyOrFillText);
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
    }

    private void setInventoryItems(Item[] items)
    {
        when(inventoryContainer.getItems()).thenReturn(items);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void equipGloves(boolean iceGloves)
    {
        setInventoryCount(ItemID.ICE_GLOVES, iceGloves ? 0 : 1);
        setEquipmentCount(ItemID.ICE_GLOVES, iceGloves ? 1 : 0);
        setInventoryCount(ItemID.GOLDSMITH_GAUNTLETS, iceGloves ? 1 : 0);
        setEquipmentCount(ItemID.GOLDSMITH_GAUNTLETS, iceGloves ? 0 : 1);
    }
}
