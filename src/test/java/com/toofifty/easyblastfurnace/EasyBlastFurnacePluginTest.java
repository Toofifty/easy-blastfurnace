package com.toofifty.easyblastfurnace;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.toofifty.easyblastfurnace.overlays.InstructionOverlay;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
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

        assertTrue(easyBlastFurnacePlugin.isEnabled());
    }

    @Test
    public void steelBarMethod()
    {
        metalMethods(ItemID.IRON_ORE, ItemID.STEEL_BAR, BarsOres.STEEL_BAR.name(), Strings.STEEL.name());
        assertEquals(Strings.WITHDRAWIRONORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void mithrilBarMethod()
    {
        metalMethods(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.name(), Strings.MITHRIL.name());
        assertEquals(Strings.WITHDRAWMITHRILORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void adamantiteBarMethod()
    {
        metalMethods(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.name(), Strings.ADAMANTITE.name());
        assertEquals(Strings.WITHDRAWADAMANTITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void runiteBarMethod()
    {
        metalMethods(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.name(), Strings.RUNITE.name());
        assertEquals(Strings.WITHDRAWRUNITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void mithrilHybridMethod()
    {
        metalMethods(ItemID.MITHRIL_ORE, ItemID.MITHRIL_BAR, BarsOres.MITHRIL_BAR.name(), Strings.MITHRILHYBRID.name());
        assertEquals(Strings.WITHDRAWMITHRILORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void adamantiteHybridMethod()
    {
        metalMethods(ItemID.ADAMANTITE_ORE, ItemID.ADAMANTITE_BAR, BarsOres.ADAMANTITE_BAR.name(), Strings.ADAMANTITEHYBRID.name());
        assertEquals(Strings.WITHDRAWADAMANTITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void runiteHybridMethod()
    {
        metalMethods(ItemID.RUNITE_ORE, ItemID.RUNITE_BAR, BarsOres.RUNITE_BAR.name(), Strings.RUNITEHYBRID.name());
        assertEquals(Strings.WITHDRAWRUNITEORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void goldBarMethod()
    {
        // Set method to Gold bars to get Withdraw ice or smiths gloves step
        getBarMethod(ItemID.GOLD_ORE, Strings.GOLD.name(), false);
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        goldPrerequisites();
        putOreOntoConveyor(ItemID.GOLD_ORE, true);
        assertEquals(Strings.WAITFORBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Equip ice gloves and give the Blast Furnace a gold bar to get Collect bars step
        when(client.getVarbitValue(BarsOres.GOLD_BAR.getVarbit())).thenReturn(1);
        equipIceGloves();
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Remove bars from inventory to get Withdraw gold ore step.
        takeBarsToBank(ItemID.GOLD_BAR, BarsOres.GOLD_BAR.name());
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWGOLDORE.getTxt(), methodHandler.getStep().getTooltip());
    }

    @Test
    public void drinkStaminaMethod()
    {
        // Check ignoreRemainingPotion works
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(false);
        assertTrue(state.getPlayer().hasEnoughEnergy());

        // setup
        when(easyBlastFurnaceConfig.staminaPotionEnable()).thenReturn(true);
        when(inventoryContainer.getItems()).thenReturn(new Item[0]);
        when(equipmentContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);

        checkStaminaHelper(); // Check energy calculation

        // deposit inventory
        when(easyBlastFurnaceConfig.requireStaminaThreshold()).thenReturn(50);
        when(client.getEnergy()).thenReturn(64);
        when(inventoryContainer.count(ItemID.VIAL)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());

        // Second deposit Inventory
        when(client.getEnergy()).thenReturn(49);
        Item[] gold = new Item[28];
        for (int i = 0; i < 28; i++) {
            gold[i] = new Item(ItemID.GOLD_ORE, 1);
        }
        when(inventoryContainer.getItems()).thenReturn(gold);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());

        // drink/withdraw stamina potions
        when(inventoryContainer.getItems()).thenReturn(new Item[0]);
        checkStaminaPotion(ItemID.STAMINA_POTION4, ItemID.STAMINA_POTION1, Strings.DRINK_STAMINA_POTION1.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, Strings.DRINK_STAMINA_POTION2.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, Strings.DRINK_STAMINA_POTION3.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4, Strings.DRINK_STAMINA_POTION4.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION4, ItemID.STAMINA_POTION1, Strings.WITHDRAW_STAMINA_POTION1.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, Strings.WITHDRAW_STAMINA_POTION2.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, Strings.WITHDRAW_STAMINA_POTION3.getTxt());
        checkStaminaPotion(ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4, Strings.WITHDRAW_STAMINA_POTION4.getTxt());

        // getMoreStaminaPotions
        when(bankContainer.count(ItemID.STAMINA_POTION4)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.GET_MORE_STAMINA_POTIONS.getTxt(), methodHandler.getStep().getTooltip());
    }

    private void checkStaminaPotion(int staminaPotionA, int staminaPotionB, String methodStep)
    {
        when(inventoryContainer.count(staminaPotionA)).thenReturn(0);
        when(bankContainer.count(staminaPotionA)).thenReturn(0);
        when((methodStep.toLowerCase().contains("withdraw") ? bankContainer : inventoryContainer).count(staminaPotionB)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(methodStep, methodHandler.getStep().getTooltip());
    }

    private void checkStaminaHelper()
    {
        // Check StaminaHelper works for all methods: coal and ore runs. This catches issues with strings changing too.
        when(client.getVarbitValue(Varbits.STAMINA_EFFECT)).thenReturn(1);
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(254);
        when(equipmentContainer.count(ItemID.RING_OF_ENDURANCE)).thenReturn(1); // todo: && runEnergyPlugin.getRingOfEnduranceCharges() >= 500 once Runelite accepts this PR: https://github.com/runelite/runelite/pull/15621.
        runThroughBarMethods(ItemID.IRON_ORE,ItemID.MITHRIL_ORE,ItemID.ADAMANTITE_ORE,ItemID.RUNITE_ORE);
        assertFalse(state.getFurnace().isCoalRunNext(CoalPer.getValueFromString(methodHandler.getMethod().toString())));
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(0);
        runThroughBarMethods(ItemID.IRON_ORE,ItemID.MITHRIL_ORE,ItemID.ADAMANTITE_ORE,ItemID.RUNITE_ORE);
        assertTrue(state.getFurnace().isCoalRunNext(CoalPer.getValueFromString(methodHandler.getMethod().toString())));
        assertEquals(6, (int) staminaHelper.getEnergyNeededForNextRun());
    }

    private void runThroughBarMethods(int ...ores)
    {
        for (int i = 0; i < ores.length; i++) {
            resetMethod();
            when(inventoryContainer.count(ores[i])).thenReturn(1);
            when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(1);
            if (i != 0) when(inventoryContainer.count(ores[i - 1])).thenReturn(0);
            easyBlastFurnacePlugin.onItemContainerChanged(event);
            resetMethod();
            when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
            easyBlastFurnacePlugin.onItemContainerChanged(event);
        }
    }

    private void resetMethod()
    {
        OverlayMenuEntry entry = new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, InstructionOverlay.RESET_ACTION, null);
        OverlayMenuClicked overlayMenuClickedEvent = new OverlayMenuClicked(entry, instructionOverlay);
        easyBlastFurnacePlugin.onOverlayMenuClicked(overlayMenuClickedEvent);
    }

    private void metalMethods(int ore, int bar, String barName, String barMethod)
    {
        boolean isHybrid = barMethod.contains("HYBRID"), isNotSteel = !barName.equals(BarsOres.STEEL_BAR.name());

        getBarMethod(ore, barMethod, isHybrid);
        assertEquals(Strings.WITHDRAWCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Fill coal bag to get Withdraw ice gloves step
        when(inventoryContainer.count(ItemID.OPEN_COAL_BAG)).thenReturn(1);
        state.getCoalBag().setCoal(state.getCoalBag().getMaxCoal());
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());

        if (isHybrid) goldPrerequisites();
        else equipIceGloves();
        putOreOntoConveyor(ore, isHybrid);
        assertEquals(Strings.EMPTYCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Empty coal bag to get Wait for bars step
        when(inventoryContainer.getItems()).thenReturn(new Item[]{new Item(ItemID.OPEN_COAL_BAG, 1)});
        when(menuOptionClicked.getMenuOption()).thenReturn(Strings.EMPTY.getTxt());
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
        assertEquals(0, state.getCoalBag().getCoal());
        assertEquals(Strings.WAITFORBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Give the Blast Furnace bars to get Collect bars step
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(1);
        if (isHybrid) equipIceGloves();
        easyBlastFurnacePlugin.onVarbitChanged(blastFurnaceChange);
        assertEquals(Strings.COLLECTBARS.getTxt(), methodHandler.getStep().getTooltip());

        // Remove bars from inventory to get Fill coal bag step
        takeBarsToBank(bar, barName);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.FILLCOALBAG.getTxt(), methodHandler.getStep().getTooltip());

        // Fill coal bag to get Withdraw gold/coal step
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(0);
        when(menuOptionClicked.getMenuOption()).thenReturn(Strings.FILL.getTxt());
        easyBlastFurnacePlugin.onMenuOptionClicked(menuOptionClicked);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());
        if (isHybrid) assertEquals(Strings.WITHDRAWGOLDORE.getTxt(), methodHandler.getStep().getTooltip());
        else if (isNotSteel) assertEquals(Strings.WITHDRAWCOAL.getTxt(), methodHandler.getStep().getTooltip());

        // Remove ores from inventory and fill Blast Furnace with coal to get Withdraw metal ore step
        when(client.getVarbitValue(BarsOres.COAL.getVarbit())).thenReturn(254);
        when(inventoryContainer.count(ItemID.COAL)).thenReturn(0);
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(state.getCoalBag().getMaxCoal(), state.getCoalBag().getCoal());
    }

    private void goldPrerequisites()
    {
        // Add ice gloves to inventory and smithing cape to bank to get Withdraw smithing cape step
        when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        when(bankContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWSMITHINGCAPE.getTxt(), methodHandler.getStep().getTooltip());

        // Add smithing cape to inventory to get Equip smithing cape step
        when(inventoryContainer.count(ItemID.SMITHING_CAPE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPSMITHINGCAPE.getTxt(), methodHandler.getStep().getTooltip());

        // Remove smithing cape from inventory and bank to get Withdraw goldsmith gauntlets step
        when(bankContainer.count(ItemID.SMITHING_CAPE)).thenReturn(0);
        when(inventoryContainer.count(ItemID.SMITHING_CAPE)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.WITHDRAWGOLDSMITHGAUNTLETS.getTxt(), methodHandler.getStep().getTooltip());

        // Add goldsmith gauntlets to inventory to get Equip goldsmith gauntlets step
        when(inventoryContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPGOLDSMITHGAUNTLETS.getTxt(), methodHandler.getStep().getTooltip());
    }

    private void putOreOntoConveyor(int ore, boolean needsGoldGloves)
    {
        if (needsGoldGloves) when(equipmentContainer.count(ItemID.GOLDSMITH_GAUNTLETS)).thenReturn(1);
        when(inventoryContainer.count(ore)).thenReturn(27);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.PUTONTOCONVEYORBELT.getTxt(), methodHandler.getStep().getTooltip());

        // Remove ores from inventory as they've been added to the Blast Furnace
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1942, 4967, 0));
        when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(0);
        when(inventoryContainer.count(ore)).thenReturn(0);
        state.update();
        easyBlastFurnacePlugin.onItemContainerChanged(event);
    }

    private void takeBarsToBank(int bar, String barName)
    {
        // Move bars from Blast Furnace to inventory to get Open bank step
        when(client.getVarbitValue(BarsOres.valueOf(barName).getVarbit())).thenReturn(0);
        when(inventoryContainer.count(bar)).thenReturn(1);
        when(localPlayer.getWorldLocation()).thenReturn(new WorldPoint(1949, 4967, 0));
        when(bankWidget.isHidden()).thenReturn(true);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.OPENBANK.getTxt(), methodHandler.getStep().getTooltip());

        // Show bank widget to get Deposit inventory step. Remove bars from inventory after.
        when(bankWidget.isHidden()).thenReturn(false);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.DEPOSITINVENTORY.getTxt(), methodHandler.getStep().getTooltip());
        when(inventoryContainer.count(bar)).thenReturn(0);
    }

    private void getBarMethod(int ore, String barMethod, boolean isHybrid)
    {
        // Place correct ores in inventory to get the right bar method.
        Item[] inv = new Item[]{new Item(ore, 1), new Item(ItemID.GOLD_ORE, 1)};
        when(inventoryContainer.getItems()).thenReturn(inv);
        when(inventoryContainer.count(ore)).thenReturn(1);
        if (isHybrid) when(inventoryContainer.count(ItemID.GOLD_ORE)).thenReturn(1);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.valueOf(barMethod).getTxt(), methodHandler.getMethod().getName());
    }

    private void equipIceGloves()
    {
        // Unequip ice gloves to get Equip ice or smiths gloves step. Re-equip afterwards.
        when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(0);
        easyBlastFurnacePlugin.onItemContainerChanged(event);
        assertEquals(Strings.EQUIPICEORSMITHSGLOVES.getTxt(), methodHandler.getStep().getTooltip());
        when(inventoryContainer.count(ItemID.ICE_GLOVES)).thenReturn(0);
        when(equipmentContainer.count(ItemID.ICE_GLOVES)).thenReturn(1);
    }
}
