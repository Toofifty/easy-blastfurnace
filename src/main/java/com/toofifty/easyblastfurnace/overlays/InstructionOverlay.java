package com.toofifty.easyblastfurnace.overlays;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.toofifty.easyblastfurnace.EasyBlastFurnaceConfig;
import com.toofifty.easyblastfurnace.EasyBlastFurnacePlugin;
import com.toofifty.easyblastfurnace.methods.Method;
import com.toofifty.easyblastfurnace.state.BlastFurnaceState;
import com.toofifty.easyblastfurnace.steps.MethodStep;
import com.toofifty.easyblastfurnace.utils.MethodHandler;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

@Singleton
public class InstructionOverlay extends OverlayPanel
{
    public static final String RESET_ACTION = "Reset";

    private static final Color TOOLTIP_COLOR = new Color(190, 190, 190);

    private final EasyBlastFurnacePlugin plugin;

    @Inject
    private EasyBlastFurnaceConfig config;

    @Inject
    private BlastFurnaceState state;

    @Inject
    private MethodHandler methodHandler;

    @Inject
    InstructionOverlay(EasyBlastFurnacePlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;

        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, RESET_ACTION, "Easy blast furnace overlay"));
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OverlayManager.OPTION_CONFIGURE, "Easy blast furnace overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isEnabled()) return null;
        if (!config.showStepOverlay()) return null;

        Method method = methodHandler.getMethod();
        MethodStep[] steps = methodHandler.getSteps();
        int index = 0;

        if (steps == null) return null;

        for (MethodStep step : steps) {
            String methodName = method != null ? method.getName() : "No method selected";
            String tooltip = state.getPlayer().isOnBlastFurnaceWorld()
                    ? (step != null
                    ? step.getTooltip()
                    : "Withdraw an ore from the bank to start. You can start a hybrid method by also withdrawing gold ore.")
                    : "You need to be on a Blast Furnace themed world to use this plugin.";

            if (index == 0) {
                panelComponent.getChildren().add(TitleComponent.builder().text("Easy Blast Furnace").build());
                panelComponent.getChildren().add(LineComponent.builder().left(methodName).leftColor(config.itemOverlayColor()).build());
            }

            index++;
            panelComponent.getChildren().add(LineComponent.builder().left(tooltip).leftColor(TOOLTIP_COLOR).build());
        }

        return super.render(graphics);
    }
}
