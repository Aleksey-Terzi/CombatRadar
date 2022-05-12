package com.aleksey.combatradar.gui.screens;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.PlayerTypeInfo;
import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.config.SoundInfo;
import com.aleksey.combatradar.gui.components.SliderButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class PlayerSettingsScreen extends Screen {
    private RadarConfig _config;
    private Screen _parent;
    private SliderButton _neutralRedSlider;
    private SliderButton _neutralGreenSlider;
    private SliderButton _neutralBlueSlider;
    private SliderButton _allyRedSlider;
    private SliderButton _allyGreenSlider;
    private SliderButton _allyBlueSlider;
    private SliderButton _enemyRedSlider;
    private SliderButton _enemyGreenSlider;
    private SliderButton _enemyBlueSlider;
    private Button _neutralPingButton;
    private Button _neutralSoundButton;
    private Button _allyPingButton;
    private Button _allySoundButton;
    private Button _enemyPingButton;
    private Button _enemySoundButton;

    public PlayerSettingsScreen(Screen parent, RadarConfig config) {
        super(TextComponent.EMPTY);
        _parent = parent;
        _config = config;
    }

    @Override
    public void init() {
        int y = this.height / 4 - 16 + 12;
        int x = this.width / 2 - 100;

        PlayerTypeInfo neutralInfo = _config.getPlayerTypeInfo(PlayerType.Neutral);
        PlayerTypeInfo allyInfo = _config.getPlayerTypeInfo(PlayerType.Ally);
        PlayerTypeInfo enemyInfo = _config.getPlayerTypeInfo(PlayerType.Enemy);

        addRenderableWidget(_neutralRedSlider = new SliderButton(x, y, 66, 1, 0, "Red", neutralInfo.color.getRed() / 255f, false));
        addRenderableWidget(_neutralGreenSlider = new SliderButton(x + 66 + 1, y, 66, 1, 0, "Green", neutralInfo.color.getGreen() / 255f, false));
        addRenderableWidget(_neutralBlueSlider = new SliderButton(x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", neutralInfo.color.getBlue() / 255f, false));
        y += 24 + 12;
        addRenderableWidget(_allyRedSlider = new SliderButton(x, y, 66, 1, 0, "Red", allyInfo.color.getRed() / 255f, false));
        addRenderableWidget(_allyGreenSlider = new SliderButton(x + 66 + 1, y, 66, 1, 0, "Green", allyInfo.color.getGreen() / 255f, false));
        addRenderableWidget(_allyBlueSlider = new SliderButton(x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", allyInfo.color.getBlue() / 255f, false));
        y += 24 + 12;
        addRenderableWidget(_enemyRedSlider = new SliderButton(x, y, 66, 1, 0, "Red", enemyInfo.color.getRed() / 255f, false));
        addRenderableWidget(_enemyGreenSlider = new SliderButton(x + 66 + 1, y, 66, 1, 0, "Green", enemyInfo.color.getGreen() / 255f, false));
        addRenderableWidget(_enemyBlueSlider = new SliderButton(x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", enemyInfo.color.getBlue() / 255f, false));

        y += 24;
        addRenderableWidget(_neutralPingButton = new Button(x, y, 133, 20, new TextComponent("Neutral Player Ping"),
                btn -> changePing(PlayerType.Neutral)));

        addRenderableWidget(_neutralSoundButton = new Button(x + 133 + 1, y, 66, 20, new TextComponent("Sound"),
                btn -> changePingSound(PlayerType.Neutral)));

        y += 24;
        addRenderableWidget(_allyPingButton = new Button(x, y, 133, 20, new TextComponent("Ally Player Ping"),
                btn -> changePing(PlayerType.Ally)));

        addRenderableWidget(_allySoundButton = new Button(x + 133 + 1, y, 66, 20, new TextComponent("Sound"),
                btn -> changePingSound(PlayerType.Ally)));

        y += 24;
        addRenderableWidget(_enemyPingButton = new Button(x, y, 133, 20, new TextComponent("Enemy Player Ping"),
                btn -> changePing(PlayerType.Enemy)));

        addRenderableWidget(_enemySoundButton = new Button(x + 133 + 1, y, 66, 20, new TextComponent("Sound"),
                btn -> changePingSound(PlayerType.Enemy)));

        y += 24;
        addRenderableWidget(new Button(x, y, 200, 20, new TextComponent("Done"),
                btn -> this.minecraft.setScreen(_parent)));
    }

    private void changePing(PlayerType playerType) {
        PlayerTypeInfo playerTypeInfo = _config.getPlayerTypeInfo(playerType);
        playerTypeInfo.ping = !playerTypeInfo.ping;
        _config.save();
    }

    private void changePingSound(PlayerType playerType) {
        this.minecraft.setScreen(new ChooseSoundScreen(this, _config, playerType));
    }

    @Override
    public void tick() {
        boolean isChanged = false;

        Color neutralColor = new Color(_neutralRedSlider.getValue(), _neutralGreenSlider.getValue(), _neutralBlueSlider.getValue());
        Color allyColor = new Color(_allyRedSlider.getValue(), _allyGreenSlider.getValue(), _allyBlueSlider.getValue());
        Color enemyColor = new Color(_enemyRedSlider.getValue(), _enemyGreenSlider.getValue(), _enemyBlueSlider.getValue());

        isChanged = changeColor(PlayerType.Neutral, neutralColor) || isChanged;
        isChanged = changeColor(PlayerType.Ally, allyColor) || isChanged;
        isChanged = changeColor(PlayerType.Enemy, enemyColor) || isChanged;

        if(isChanged)
            _config.save();

        PlayerTypeInfo neutralPlayer = _config.getPlayerTypeInfo(PlayerType.Neutral);
        PlayerTypeInfo allyPlayer = _config.getPlayerTypeInfo(PlayerType.Ally);
        PlayerTypeInfo enemyPlayer = _config.getPlayerTypeInfo(PlayerType.Enemy);

        _neutralPingButton.setMessage(new TextComponent("Neutral Player Ping: " + (neutralPlayer.ping ? "On" : "Off")));
        _neutralSoundButton.setMessage(new TextComponent(SoundInfo.getByValue(neutralPlayer.soundEventName).name));
        _allyPingButton.setMessage(new TextComponent("Ally Player Ping: " + (allyPlayer.ping ? "On" : "Off")));
        _allySoundButton.setMessage(new TextComponent(SoundInfo.getByValue(allyPlayer.soundEventName).name));
        _enemyPingButton.setMessage(new TextComponent("Enemy Player Ping: " + (enemyPlayer.ping ? "On" : "Off")));
        _enemySoundButton.setMessage(new TextComponent(SoundInfo.getByValue(enemyPlayer.soundEventName).name));
    }

    private boolean changeColor(PlayerType playerType, Color color) {
        PlayerTypeInfo info = _config.getPlayerTypeInfo(playerType);

        if(info.color == color)
            return false;

        info.color = color;

        return true;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1, 1, 1, 0.75f);

        renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, "Player Settings", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
        drawCenteredString(poseStack, this.font, "Neutral", this.width / 2, _neutralRedSlider.y - 12, _config.getPlayerTypeInfo(PlayerType.Neutral).color.getRGB());
        drawCenteredString(poseStack, this.font, "Ally", this.width / 2, _allyRedSlider.y - 12, _config.getPlayerTypeInfo(PlayerType.Ally).color.getRGB());
        drawCenteredString(poseStack, this.font, "Enemy", this.width / 2, _enemyRedSlider.y - 12, _config.getPlayerTypeInfo(PlayerType.Enemy).color.getRGB());

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
