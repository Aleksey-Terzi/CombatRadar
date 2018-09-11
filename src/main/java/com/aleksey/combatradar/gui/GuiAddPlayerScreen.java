package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.RadarConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

/**
 * @author Aleksey Terzi
 */
public class GuiAddPlayerScreen extends GuiScreen {
    private static final int BUTTON_ID_ADD = 0;
    private static final int BUTTON_ID_CANCEL = 1;

    private RadarConfig _config;
    private GuiScreen _parent;
    private PlayerType _playerType;
    private GuiTextField _playerNameTextField;

    public GuiAddPlayerScreen(GuiScreen parent, RadarConfig config, PlayerType playerType) {
        _parent = parent;
        _config = config;
        _playerType = playerType;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        int y = this.height / 3;
        int x = this.width / 2 - 100;

        _playerNameTextField = new GuiTextField(101, fontRenderer, x, y, 200, 20);
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_ADD, x, y, 200, 20, "Add"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_CANCEL, x, y, 200, 20, "Cancel"));

        _playerNameTextField.setFocused(true);
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) throws IOException {
        super.mouseClicked(x, y, mouseButton);
        _playerNameTextField.mouseClicked(x, y, mouseButton);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(!guiButton.enabled)
            return;

        switch(guiButton.id) {
            case BUTTON_ID_ADD:
                actionAdd();
                break;
            case BUTTON_ID_CANCEL:
                actionCancel();
                break;
        }
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {
        if(_playerNameTextField.isFocused())
            _playerNameTextField.textboxKeyTyped(keyChar, keyCode);

        if(keyCode == Keyboard.KEY_RETURN)
            actionAdd();
        else if(keyCode == Keyboard.KEY_ESCAPE)
            actionCancel();
    }

    private void actionAdd() {
        String playerName = _playerNameTextField.getText().trim();

        if(playerName.length() == 0)
            return;

        _config.setPlayerType(playerName, _playerType);
        _config.save();

        mc.displayGuiScreen(_parent);
    }

    private void actionCancel() {
        mc.displayGuiScreen(_parent);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String playerTypeName = _playerType == PlayerType.Ally ? "Ally" : "Enemy";

        drawBackground(0);
        drawCenteredString(this.fontRenderer, "Add " + playerTypeName + " Player", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());

        fontRenderer.drawStringWithShadow("Player username", this.width / 2 - 100, _playerNameTextField.y - 12, Color.LIGHT_GRAY.getRGB());

        _playerNameTextField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}