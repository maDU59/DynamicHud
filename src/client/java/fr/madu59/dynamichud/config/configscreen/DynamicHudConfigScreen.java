package fr.madu59.dynamichud.config.configscreen;

import fr.madu59.dynamichud.config.SettingsManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DynamicHudConfigScreen extends Screen {
    private MyConfigListWidget list;

    private final String INDENT = " ⤷  ";

    protected DynamicHudConfigScreen(Screen parent) {
        super(Component.literal("Dynamic Hud configuration screen"));
        this.parent = parent;
    }

    private final Screen parent;

    @Override
    protected void init() {
        super.init();
        // Create the scrolling list
        this.list = new MyConfigListWidget(this.minecraft, this.width, this.height - 80, 40, 26);

        // Example: Add categories + buttons
        list.addCategory("dynamichud.config.category.main");
        list.addButton(SettingsManager.CROSSHAIR_STATE, btn -> {
            SettingsManager.CROSSHAIR_STATE.setToNextValue();
        });
        list.addButton(SettingsManager.HOTBAR_STATE, btn -> {
            SettingsManager.HOTBAR_STATE.setToNextValue();
        });
        list.addButton(SettingsManager.CONTEXTUAL_BAR_STATE, btn -> {
            SettingsManager.CONTEXTUAL_BAR_STATE.setToNextValue();
        });
        list.addButton(SettingsManager.HEALTH_STATE, btn -> {
            SettingsManager.HEALTH_STATE.setToNextValue();
        });
        list.addButton(SettingsManager.ARMOR_STATE, btn -> {
            SettingsManager.ARMOR_STATE.setToNextValue();
        });
        list.addButton(SettingsManager.FOOD_STATE, btn -> {
            SettingsManager.FOOD_STATE.setToNextValue();
        });
        list.addSlider(SettingsManager.DYNAMIC_FOOD_BAR_MINIMUM, 0f, 10f, 0.5f, INDENT);
        list.addButton(SettingsManager.DYNAMIC_FOOD_BAR_HOLDING, btn -> {
            SettingsManager.DYNAMIC_FOOD_BAR_HOLDING.setToNextValue();
        }, INDENT);
        list.addButton(SettingsManager.AIR_STATE, btn -> {
            SettingsManager.AIR_STATE.setToNextValue();
        });
        list.addButton(SettingsManager.MOUNT_HEALTH_STATE, btn -> {
            SettingsManager.MOUNT_HEALTH_STATE.setToNextValue();
        });
        list.addCategory("dynamichud.config.category.global_settings");
        list.addButton(SettingsManager.EASING_FUNCTION, btn -> {
            SettingsManager.EASING_FUNCTION.setToNextValue();
        });
        list.addButton(SettingsManager.FADEIN_TYPE, btn -> {
            SettingsManager.FADEIN_TYPE.setToNextValue();
        });
        list.addButton(SettingsManager.FADEOUT_TYPE, btn -> {
            SettingsManager.FADEOUT_TYPE.setToNextValue();
        });
        list.addSlider(SettingsManager.FADING_DURATION, 0.1f, 1f, 0.1f);
        list.addSlider(SettingsManager.SHOWN_DURATION, 1f, 5f, 0.1f);

        Button doneButton = Button.builder(Component.translatable("dynamichud.config.done"), b -> {
            this.minecraft.setScreen(this.parent);
            SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
        }).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build();

        this.addRenderableWidget(this.list);
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
        SettingsManager.saveSettings(SettingsManager.ALL_OPTIONS);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.list.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}
