package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.PlayerTypeInfo;
import com.aleksey.combatradar.config.RadarConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class GuiPlayerSettingsScreen extends GuiScreen {
    private static final int BUTTON_ID_PING_NEUTRAL = 1;
    private static final int BUTTON_ID_PING_ALLY = 2;
    private static final int BUTTON_ID_PING_ENEMY = 3;
    private static final int BUTTON_ID_DONE = 100;

    private RadarConfig _config;
    private GuiScreen _parent;
    private GuiSlider _neutralRedSlider;
    private GuiSlider _neutralGreenSlider;
    private GuiSlider _neutralBlueSlider;
    private GuiSlider _allyRedSlider;
    private GuiSlider _allyGreenSlider;
    private GuiSlider _allyBlueSlider;
    private GuiSlider _enemyRedSlider;
    private GuiSlider _enemyGreenSlider;
    private GuiSlider _enemyBlueSlider;
    private GuiButton _neutralButton;
    private GuiButton _allyButton;
    private GuiButton _enemyButton;

    public GuiPlayerSettingsScreen(GuiScreen parent, RadarConfig config) {
        _parent = parent;
        _config = config;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        int y = this.height / 4 - 16 + 12;
        int x = this.width / 2 - 100;

        PlayerTypeInfo neutralInfo = _config.getPlayerTypeInfo(PlayerType.Neutral);
        PlayerTypeInfo allyInfo = _config.getPlayerTypeInfo(PlayerType.Ally);
        PlayerTypeInfo enemyInfo = _config.getPlayerTypeInfo(PlayerType.Enemy);

        this.buttonList.add(_neutralRedSlider = new GuiSlider(0, x, y, 66, 1, 0, "Red", neutralInfo.color.getRed() / 255f, false));
        this.buttonList.add(_neutralGreenSlider = new GuiSlider(0, x + 66 + 1, y, 66, 1, 0, "Green", neutralInfo.color.getGreen() / 255f, false));
        this.buttonList.add(_neutralBlueSlider = new GuiSlider(0, x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", neutralInfo.color.getBlue() / 255f, false));
        y += 24 + 12;
        this.buttonList.add(_allyRedSlider = new GuiSlider(0, x, y, 66, 1, 0, "Red", allyInfo.color.getRed() / 255f, false));
        this.buttonList.add(_allyGreenSlider = new GuiSlider(0, x + 66 + 1, y, 66, 1, 0, "Green", allyInfo.color.getGreen() / 255f, false));
        this.buttonList.add(_allyBlueSlider = new GuiSlider(0, x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", allyInfo.color.getBlue() / 255f, false));
        y += 24 + 12;
        this.buttonList.add(_enemyRedSlider = new GuiSlider(0, x, y, 66, 1, 0, "Red", enemyInfo.color.getRed() / 255f, false));
        this.buttonList.add(_enemyGreenSlider = new GuiSlider(0, x + 66 + 1, y, 66, 1, 0, "Green", enemyInfo.color.getGreen() / 255f, false));
        this.buttonList.add(_enemyBlueSlider = new GuiSlider(0, x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", enemyInfo.color.getBlue() / 255f, false));
        y += 24;
        this.buttonList.add(_neutralButton = new GuiButton(BUTTON_ID_PING_NEUTRAL, x, y, 200, 20, "Neutral Player Ping"));
        y += 24;
        this.buttonList.add(_allyButton = new GuiButton(BUTTON_ID_PING_ALLY, x, y, 200, 20, "Ally Player Ping"));
        y += 24;
        this.buttonList.add(_enemyButton = new GuiButton(BUTTON_ID_PING_ENEMY, x, y, 200, 20, "Enemy Player Ping"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_DONE, x, y, 200, 20, "Done"));
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(!guiButton.enabled)
            return;

        PlayerType changePingPlayerType = null;

        switch(guiButton.id) {
            case BUTTON_ID_PING_NEUTRAL:
                changePingPlayerType = PlayerType.Neutral;
                break;
            case BUTTON_ID_PING_ALLY:
                changePingPlayerType = PlayerType.Ally;
                break;
            case BUTTON_ID_PING_ENEMY:
                changePingPlayerType = PlayerType.Enemy;
                break;
            case BUTTON_ID_DONE:
                mc.displayGuiScreen(_parent);
                break;
        }

        if(changePingPlayerType != null) {
            PlayerTypeInfo playerTypeInfo = _config.getPlayerTypeInfo(changePingPlayerType);
            playerTypeInfo.ping = !playerTypeInfo.ping;
            _config.save();
        }
    }

    @Override
    public void updateScreen() {
        boolean isChanged = false;

        Color neutralColor = new Color(_neutralRedSlider.getValue(), _neutralGreenSlider.getValue(), _neutralBlueSlider.getValue());
        Color allyColor = new Color(_allyRedSlider.getValue(), _allyGreenSlider.getValue(), _allyBlueSlider.getValue());
        Color enemyColor = new Color(_enemyRedSlider.getValue(), _enemyGreenSlider.getValue(), _enemyBlueSlider.getValue());

        isChanged = changeColor(PlayerType.Neutral, neutralColor) || isChanged;
        isChanged = changeColor(PlayerType.Ally, allyColor) || isChanged;
        isChanged = changeColor(PlayerType.Enemy, enemyColor) || isChanged;

        if(isChanged)
            _config.save();

        _neutralButton.displayString = "Neutral Player Ping: " + (_config.getPlayerTypeInfo(PlayerType.Neutral).ping ? "On" : "Off");
        _allyButton.displayString = "Ally Player Ping: " + (_config.getPlayerTypeInfo(PlayerType.Ally).ping ? "On" : "Off");
        _enemyButton.displayString = "Enemy Player Ping: " + (_config.getPlayerTypeInfo(PlayerType.Enemy).ping ? "On" : "Off");
    }

    private boolean changeColor(PlayerType playerType, Color color) {
        PlayerTypeInfo info = _config.getPlayerTypeInfo(playerType);

        if(info.color == color)
            return false;

        info.color = color;

        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRenderer, "Player Settings", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
        drawCenteredString(this.fontRenderer, "Neutral", this.width / 2, _neutralRedSlider.y - 12, _config.getPlayerTypeInfo(PlayerType.Neutral).color.getRGB());
        drawCenteredString(this.fontRenderer, "Ally", this.width / 2, _allyRedSlider.y - 12, _config.getPlayerTypeInfo(PlayerType.Ally).color.getRGB());
        drawCenteredString(this.fontRenderer, "Enemy", this.width / 2, _enemyRedSlider.y - 12, _config.getPlayerTypeInfo(PlayerType.Enemy).color.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
