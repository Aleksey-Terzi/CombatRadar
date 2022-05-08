package com.aleksey.combatradar.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.text.DecimalFormat;

/**
 * @author Aleksey Terzi
 */
public class SliderButton extends Button {
    private static DecimalFormat _decimalFormat = new DecimalFormat("#.##");

    private float _value;
    private float _minValue;
    private float _maxValue;
    private String _name;
    private boolean _integer;

    public float getValue() { return _value; }

    public SliderButton(int x, int y, int width, float maxValue, float minValue, String name, float value, boolean integer) {
        super(x, y, width, 20, new TextComponent(name), btn -> {});
        _maxValue = maxValue;
        _minValue = minValue;
        _value = value;
        _name = name;
        _integer = integer;
    }

    @Override
    protected int getYImage(boolean hovered) {
        return 0;
    }

    @Override
    protected void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY) {
        String text = _name + ": " + _decimalFormat.format(_value);
        setMessage(new TextComponent(text));

        int xOffset = (int)((_value - _minValue) / (_maxValue - _minValue) * (this.width - 8));
        int textureYOffset = this.isHovered ? 86 : 66;

        blit(poseStack, this.x + xOffset, this.y, 0, textureYOffset, 4, 20);
        blit(poseStack, this.x + xOffset + 4, this.y, 196, textureYOffset, 4, 20);
    }

    @Override
    protected void onDrag(double mouseX, double p_93592_, double p_93593_, double p_93594_) {
        calculateValue(mouseX);
    }

    private void calculateValue(double mouseX) {
        _value = ((float) (mouseX - 4 - this.x)) * (_maxValue - _minValue) / (width - 8) + _minValue;

        if(_integer)
            _value = Math.round(_value);

        if (_value < _minValue)
            _value = _minValue;
        else if (_value > _maxValue)
            _value = _maxValue;
    }
}
