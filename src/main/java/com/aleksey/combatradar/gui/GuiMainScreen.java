package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.config.RadarConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class GuiMainScreen extends GuiScreen {
    private static final int BUTTON_ID_LOCATION_AND_COLOR = 1;
    private static final int BUTTON_ID_ENTITIES = 2;
    private static final int BUTTON_ID_ENABLE = 3;
    private static final int BUTTON_ID_MANAGE_PLAYERS = 4;
    private static final int BUTTON_ID_PLAYERS_SETTINGS = 5;
    private static final int BUTTON_ID_PLAYER_STATUS = 6;
    private static final int BUTTON_ID_DONE = 100;

    private RadarConfig _config;
    private GuiScreen _parent;
    private GuiButton _playerStatusButton;
    private GuiButton _enableButton;

    public GuiMainScreen(GuiScreen parent, RadarConfig config) {
        _parent = parent;
        _config = config;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        int y = this.height / 4 - 16;
        int x = this.width / 2 - 100;

        this.buttonList.add(new GuiButton(BUTTON_ID_LOCATION_AND_COLOR, x, y, 200, 20, "Location and Color"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_ENTITIES, x, y, 200, 20, "Radar Entities"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_MANAGE_PLAYERS, x, y, 200, 20, "Manage Players"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_PLAYERS_SETTINGS, x, y, 200, 20, "Player Settings"));
        y += 24;
        this.buttonList.add(_playerStatusButton = new GuiButton(BUTTON_ID_PLAYER_STATUS, x, y, 200, 20, "Log Players Statuses:"));
        y += 24;
        this.buttonList.add(_enableButton = new GuiButton(BUTTON_ID_ENABLE, x, y, 100, 20, "Radar: "));
        this.buttonList.add(new GuiButton(BUTTON_ID_DONE, x + 101, y, 100, 20, "Done"));
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(!guiButton.enabled)
            return;

        int id = guiButton.id;

        switch(id) {
            case BUTTON_ID_LOCATION_AND_COLOR:
                mc.displayGuiScreen(new GuiLocationAndColorScreen(this, _config));
                break;
            case BUTTON_ID_ENTITIES:
                mc.displayGuiScreen(new GuiEntityScreen(this, _config));
                break;
            case BUTTON_ID_MANAGE_PLAYERS:
                mc.displayGuiScreen(new GuiManagePlayerScreen(this, _config));
                break;
            case BUTTON_ID_PLAYERS_SETTINGS:
                mc.displayGuiScreen(new GuiPlayerSettingsScreen(this, _config));
                break;
            case BUTTON_ID_PLAYER_STATUS:
                _config.setLogPlayerStatus(!_config.getLogPlayerStatus());
                _config.save();
                break;
            case BUTTON_ID_ENABLE:
                _config.setEnabled(!_config.getEnabled());
                _config.save();
                break;
            case BUTTON_ID_DONE:
                mc.displayGuiScreen(_parent);
                break;
        }
    }

    @Override
    public void updateScreen() {
        _playerStatusButton.displayString = "Log Players Statuses: " + (_config.getLogPlayerStatus() ? "On" : "Off");
        _enableButton.displayString = "Radar: " + (_config.getEnabled() ? "On" : "Off");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String keyName = Keyboard.getKeyName(_config.getSettingsKey().getKeyCode());

        drawDefaultBackground();
        drawCenteredString(this.fontRenderer, "Combat Radar Settings", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
        drawCenteredString(fontRenderer, "Ctrl+Alt+" + keyName + " - enable/disable radar", this.width / 2, _enableButton.y + 24, Color.LIGHT_GRAY.getRGB());
        drawCenteredString(fontRenderer, "Ctrl+" + keyName + " - enable/disable mobs", this.width / 2, _enableButton.y + 24 + 12, Color.LIGHT_GRAY.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
