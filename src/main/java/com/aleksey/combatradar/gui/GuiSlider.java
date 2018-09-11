package com.aleksey.combatradar.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.text.DecimalFormat;

import static com.mumfrey.liteloader.gl.GL.glColor4f;

/**
 * @author Aleksey Terzi
 */
public class GuiSlider extends GuiButton {
    private static DecimalFormat _decimalFormat = new DecimalFormat("#.##");

    private float _value;
    private boolean _dragging;
    private float _minValue;
    private float _maxValue;
    private String _name;
    private boolean _integer;

    public float getValue() { return _value; }

    public GuiSlider(int id, int x, int y, int width, float maxValue, float minValue, String name, float value, boolean integer) {
        super(id, x, y, width, 20, name);
        _maxValue = maxValue;
        _minValue = minValue;
        _value = value;
        _name = name;
        _integer = integer;
    }

    @Override
    public int getHoverState(boolean hovered) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.visible)
            return;

        if (_dragging)
            calculateValue(mouseX);

        update();

        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.x + (int) ((_value - _minValue) / (_maxValue - _minValue) * (this.width - 8)), this.y, 0, 66, 4, 20);
        drawTexturedModalRect(this.x + (int) ((_value - _minValue) / (_maxValue - _minValue) * (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (!super.mousePressed(minecraft, mouseX, mouseY))
            return false;

        calculateValue(mouseX);

        _dragging = true;

        return true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        _dragging = false;
    }

    private void update() {
        this.displayString = _name + ": " + _decimalFormat.format(_value);
    }

    private void calculateValue(int mouseX) {
        _value = ((float) (mouseX - 4 - this.x)) * (_maxValue - _minValue) / (width - 8) + _minValue;

        if(_integer)
            _value = Math.round(_value);

        if (_value < _minValue)
            _value = _minValue;
        else if (_value > _maxValue)
            _value = _maxValue;
    }
}