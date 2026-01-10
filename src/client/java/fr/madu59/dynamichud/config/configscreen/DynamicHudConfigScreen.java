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
        

        Button doneButton = Button.builder(Component.literal("Done"), b -> {
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
