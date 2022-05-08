package com.aleksey.combatradar.gui.screens;

import com.aleksey.combatradar.SoundHelper;
import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.config.SoundInfo;
import com.aleksey.combatradar.gui.components.CheckButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Aleksey Terzi
 */
public class ChooseSoundScreen extends Screen {
    private static final int MAX_BUTTON_PER_COL = 6;

    private RadarConfig _config;
    private Screen _parent;
    private PlayerType _playerType;
    private int _titleTop;
    private ArrayList<CheckButton> _checkButtons;

    public ChooseSoundScreen(Screen parent, RadarConfig config, PlayerType playerType) {
        super(TextComponent.EMPTY);
        _parent = parent;
        _config = config;
        _playerType = playerType;
        _checkButtons = new ArrayList<>();
    }

    @Override
    protected void init() {
        _titleTop = this.height / 4 - 40;

        int topY = this.height / 4 - 16;
        int leftX = this.width / 2 - 60;

        int y = topY;
        int x = leftX;

        String sound = _config.getPlayerTypeInfo(_playerType).soundEventName;

        for(int i = 0; i < SoundInfo.SOUND_LIST.length; i++) {
            SoundInfo info = SoundInfo.SOUND_LIST[i];
            final int soundIndex = i;

            CheckButton chk = new CheckButton(x, y, 80, info.name, btn -> chooseSound(soundIndex));
            _checkButtons.add(chk);

            chk.setChecked(sound.equalsIgnoreCase(info.value));

            addRenderableWidget(chk);

            if(i == MAX_BUTTON_PER_COL - 1) {
                y = topY;
                x = leftX + 80;
            } else {
                y += 20;
            }
        }

        addRenderableWidget(new Button(
                this.width / 2 - 100,
                y,
                200,
                20,
                new TextComponent("Done"),
                btn -> this.minecraft.setScreen(_parent)
        ));
    }

    @Override
    public void removed() {
        _config.save();
    }

    private void chooseSound(int index) {
        for(int i = 0; i < SoundInfo.SOUND_LIST.length; i++) {
            boolean isChecked = i == index;
            _checkButtons.get(i).setChecked(isChecked);
        }

        SoundInfo soundInfo = SoundInfo.SOUND_LIST[index];

        _config.getPlayerTypeInfo(_playerType).soundEventName = soundInfo.value;
        _config.save();

        SoundHelper.playSound(soundInfo.value, this.minecraft.player.getUUID());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
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

        renderDirtBackground(0);

        drawCenteredString(poseStack, this.font, title, this.width / 2, _titleTop, Color.WHITE.getRGB());

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}