package com.aleksey.combatradar.gui.screens;

import com.aleksey.combatradar.Speedometer;
import com.aleksey.combatradar.config.RadarConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class MainScreen extends Screen {
    private RadarConfig _config;
    private Speedometer _speedometer;
    private Screen _parent;
    private Button _playerStatusButton;
    private Button _speedometerButton;
    private Button _enableButton;
    private int _keyHintY;

    public MainScreen(Screen parent, RadarConfig config, Speedometer speedometer) {
        super(TextComponent.EMPTY);
        _parent = parent;
        _config = config;
        _speedometer = speedometer;
    }

    @Override
    public void init() {
        int y = this.height / 4 - 16;
        int x = this.width / 2 - 100;

        final Screen screen = this;

        addRenderableWidget(new Button(x, y, 200, 20, new TextComponent("Location and Color"),
                btn -> this.minecraft.setScreen(new LocationAndColorScreen(screen, _config))));

        y += 24;
        addRenderableWidget(new Button(x, y, 200, 20, new TextComponent("Player Settings"),
                btn -> this.minecraft.setScreen(new PlayerSettingsScreen(screen, _config))));

        y += 24;
        addRenderableWidget(new Button(x, y, 100, 20, new TextComponent("Radar Entities"),
                btn -> this.minecraft.setScreen(new EntityScreen(screen, _config))));

        addRenderableWidget(new Button(x + 101, y, 100, 20, new TextComponent("Manage Players"),
                btn -> this.minecraft.setScreen(new ManagePlayersScreen(screen, _config))));

        y += 24;
        _playerStatusButton = new Button(x, y, 200, 20, new TextComponent("Log Players Statuses:"),
                btn -> {
                    _config.setLogPlayerStatus(!_config.getLogPlayerStatus());
                    _config.save();
                }
        );
        addRenderableWidget(_playerStatusButton);

        y += 24;
        _enableButton = new Button(x, y, 100, 20, new TextComponent("Radar:"),
                btn -> {
                    _config.setEnabled(!_config.getEnabled());
                    _config.save();
                }
        );
        addRenderableWidget(_enableButton);

        _speedometerButton = new Button(x + 101, y, 100, 20, new TextComponent("Speed:"),
                btn -> {
                    _config.setSpeedometerEnabled(!_config.getSpeedometerEnabled());
                    _config.save();

                    if (!_config.getSpeedometerEnabled())
                        _speedometer.clearSpeed();
                }
        );
        addRenderableWidget(_speedometerButton);

        y += 24;
        addRenderableWidget(new Button(x, y, 200, 20, new TextComponent("Done"),
                btn -> this.minecraft.setScreen((_parent))));

        _keyHintY = y + 24;
    }

    @Override
    public void tick() {
        _playerStatusButton.setMessage(new TextComponent("Log Players Statuses: " + (_config.getLogPlayerStatus() ? "On" : "Off")));
        _enableButton.setMessage(new TextComponent("Radar: " + (_config.getEnabled() ? "On" : "Off")));
        _speedometerButton.setMessage(new TextComponent("Speed: " + (_config.getSpeedometerEnabled() ? "On" : "Off")));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        String keyName = _config.getSettingsKey().getTranslatedKeyMessage().getString().toUpperCase();

        RenderSystem.setShaderColor(1, 1, 1, 0.75f);

        renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, "Combat Radar Settings", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
        drawCenteredString(poseStack, this.font, "Ctrl+Alt+" + keyName + " - enable/disable radar", this.width / 2, _keyHintY, Color.LIGHT_GRAY.getRGB());
        drawCenteredString(poseStack, this.font, "Ctrl+" + keyName + " - enable/disable mobs", this.width / 2, _keyHintY + 12, Color.LIGHT_GRAY.getRGB());

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
