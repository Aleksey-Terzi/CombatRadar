package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.SoundHelper;
import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.config.SoundInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class GuiChooseSoundScreen extends GuiScreen {
    private static final int BUTTON_ID_DONE = 1;
    private static final int BUTTON_ID_FIRSTSOUND = 100;

    private static final int MAX_BUTTON_PER_COL = 6;

    private RadarConfig _config;
    private GuiScreen _parent;
    private PlayerType _playerType;
    private int _titleTop;

    public GuiChooseSoundScreen(GuiScreen parent, RadarConfig config, PlayerType playerType) {
        _parent = parent;
        _config = config;
        _playerType = playerType;
    }

    @Override
    public void initGui() {
        _titleTop = this.height / 4 - 40;

        this.buttonList.clear();

        int topY = this.height / 4 - 16;
        int leftX = this.width / 2 - 60;

        int y = topY;
        int x = leftX;

        String sound = _config.getPlayerTypeInfo(_playerType).soundEventName;

        for(int i = 0; i < SoundInfo.SOUND_LIST.length; i++) {
            SoundInfo info = SoundInfo.SOUND_LIST[i];

            GuiCheckButton chk = new GuiCheckButton(BUTTON_ID_FIRSTSOUND + i, x, y, 80, info.name);
            chk.setChecked(sound.equalsIgnoreCase(info.value));

            this.buttonList.add(chk);

            if(i == MAX_BUTTON_PER_COL - 1) {
                y = topY;
                x = leftX + 80;
            } else {
                y += 20;
            }
        }

        this.buttonList.add(new GuiButton(BUTTON_ID_DONE, this.width / 2 - 100, y, 200, 20, "Done"));
    }

    @Override
    public void onGuiClosed() {
        _config.save();
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(!guiButton.enabled)
            return;

        int id = guiButton.id;

        switch(id) {
            case BUTTON_ID_DONE:
                mc.displayGuiScreen(_parent);
                break;
            default:
                chooseSound(id - BUTTON_ID_FIRSTSOUND);
                break;
        }
    }

    private void chooseSound(int index) {
        for(int i = 0; i < SoundInfo.SOUND_LIST.length; i++) {
            boolean isChecked = i == index;
            ((GuiCheckButton)this.buttonList.get(i)).setChecked(isChecked);
        }

        SoundInfo soundInfo = SoundInfo.SOUND_LIST[index];

        _config.getPlayerTypeInfo(_playerType).soundEventName = soundInfo.value;
        _config.save();

        SoundHelper.playSound(this.mc, soundInfo.value, this.mc.player.getUniqueID());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String playerTypeName;

        switch(_playerType) {
            case Ally:
                playerTypeName = "Ally";
                break;
            case Enemy:
                playerTypeName = "Enemy";
                break;
            default:
                playerTypeName = "Neutral";
                break;
        }

        String title = "Ping Sound for " + playerTypeName + " Players";

        drawBackground(0);
        drawCenteredString(this.fontRenderer, title, this.width / 2, _titleTop, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}