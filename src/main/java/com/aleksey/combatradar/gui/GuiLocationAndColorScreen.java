package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.config.RadarConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class GuiLocationAndColorScreen extends GuiScreen {
    private static final int BUTTON_ID_TOPLEFT = 1;
    private static final int BUTTON_ID_TOPRIGHT = 2;
    private static final int BUTTON_ID_BOTTOMLEFT = 3;
    private static final int BUTTON_ID_BOTTOMRIGHT = 4;
    private static final int BUTTON_ID_DONE = 5;

    private RadarConfig _config;
    private GuiScreen _parent;
    private GuiSlider _redSlider;
    private GuiSlider _greenSlider;
    private GuiSlider _blueSlider;
    private GuiSlider _opacitySlider;
    private GuiSlider _sizeSlider;
    private GuiSlider _rangeSlider;
    private GuiSlider _iconScaleSlider;
    private GuiSlider _fontScaleSlider;

    public GuiLocationAndColorScreen(GuiScreen parent, RadarConfig config) {
        _parent = parent;
        _config = config;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        int y = this.height / 4 - 16;
        int x = this.width / 2 - 100;

        this.buttonList.add(_redSlider = new GuiSlider(0, x, y, 66, 1, 0, "Red", _config.getRadarColor().getRed() / 255f, false));
        this.buttonList.add(_greenSlider = new GuiSlider(0, x + 66 + 1, y, 66, 1, 0, "Green", _config.getRadarColor().getGreen() / 255f, false));
        this.buttonList.add(_blueSlider = new GuiSlider(0, x + 66 + 1 + 66 + 1, y, 66, 1, 0, "Blue", _config.getRadarColor().getBlue() / 255f, false));
        y += 24;
        this.buttonList.add(_opacitySlider = new GuiSlider(0, x, y, 200, 1, 0, "Radar Opacity", _config.getRadarOpacity(), false));
        y += 24;
        this.buttonList.add(_sizeSlider = new GuiSlider(0, x, y, 100, 1, 0.1f, "Radar Size", _config.getRadarSize(), false));
        this.buttonList.add(_rangeSlider = new GuiSlider(0, x + 101, y, 100, 8, 3, "Radar Range", _config.getRadarDistance() / 16f, true));
        y += 24;
        this.buttonList.add(_iconScaleSlider = new GuiSlider(0, x, y, 100, 1f, 0.1f, "Icon Size", _config.getIconScale() / 3f, false));
        this.buttonList.add(_fontScaleSlider = new GuiSlider(0, x + 101, y, 100, 1f, 0.2f, "Font Size", _config.getFontScale() / 3f, false));
        y += 24 + 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_TOPLEFT, x, y, 100, 20, "Snap top left"));
        this.buttonList.add(new GuiButton(BUTTON_ID_TOPRIGHT, x + 101, y, 100, 20, "Snap top right"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_BOTTOMLEFT, x, y, 100, 20, "Snap bottom left"));
        this.buttonList.add(new GuiButton(BUTTON_ID_BOTTOMRIGHT, x + 101, y, 100, 20, "Snap bottom right"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_DONE, x, y, 200, 20, "Done"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(!guiButton.enabled)
            return;

        switch(guiButton.id) {
            case BUTTON_ID_TOPLEFT:
                _config.setRadarX(0);
                _config.setRadarY(0);
                _config.save();
                break;
            case BUTTON_ID_TOPRIGHT:
                _config.setRadarX(1);
                _config.setRadarY(0);
                _config.save();
                break;
            case BUTTON_ID_BOTTOMLEFT:
                _config.setRadarX(0);
                _config.setRadarY(1);
                _config.save();
                break;
            case BUTTON_ID_BOTTOMRIGHT:
                _config.setRadarX(1);
                _config.setRadarY(1);
                _config.save();
                break;
            case BUTTON_ID_DONE:
                mc.displayGuiScreen(_parent);
                break;
        }
    }

    @Override
    public void updateScreen() {
        boolean isChanged = false;

        ScaledResolution res = new ScaledResolution(mc);
        float xSpeed = 1.f / res.getScaledWidth();
        float ySpeed = 1.f / res.getScaledHeight();

        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            _config.setRadarX(_config.getRadarX() - xSpeed);
            isChanged = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            _config.setRadarX(_config.getRadarX() + xSpeed);
            isChanged = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            _config.setRadarY(_config.getRadarY() - ySpeed);
            isChanged = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            _config.setRadarY(_config.getRadarY() + ySpeed);
            isChanged = true;
        }

        Color radarColor = new Color(_redSlider.getValue(), _greenSlider.getValue(), _blueSlider.getValue());

        isChanged = _config.setRadarColor(radarColor) || isChanged;
        isChanged = _config.setRadarOpacity(_opacitySlider.getValue()) || isChanged;
        isChanged = _config.setRadarSize(_sizeSlider.getValue()) || isChanged;
        isChanged = _config.setRadarDistance((int)(_rangeSlider.getValue() * 16)) || isChanged;
        isChanged = _config.setIconScale(_iconScaleSlider.getValue() * 3f) || isChanged;
        isChanged = _config.setFontScale(_fontScaleSlider.getValue() * 3f) || isChanged;

        if(isChanged)
            _config.save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRenderer, "Location and Color", this.width / 2, this.height / 4 - 40, Color.WHITE.getRGB());
        drawCenteredString(this.fontRenderer, "Use arrow keys to reposition radar", this.width / 2, _iconScaleSlider.y + 24 + 12, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}