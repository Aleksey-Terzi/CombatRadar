package com.aleksey.combatradar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;

import java.awt.*;
import java.util.ArrayList;

public class Speedometer {
    private static final int _maxCount = 5;
    private static final double _maxSpeed = 100;

    private final ArrayList<Double> _speedList = new ArrayList<>(_maxCount);
    private long _prevTime;
    private double _prevX;
    private double _prevZ;
    private String _speedText;

    public void calc() {
        if (_speedList.size() == _maxCount)
            _speedList.remove(0);

        double currentSpeed = calcCurrentSpeed();

        _speedList.add(currentSpeed);

        double sum = 0;
        for (double speed : _speedList)
            sum += speed;

        double avgSpeed = (double)Math.round(10.0 * sum / _speedList.size()) / 10.0;

        _speedText = String.format("%.1f m/s", avgSpeed);
    }

    public void clearSpeed() {
        _speedList.clear();
        _speedText = "0.0 m/s";
        _prevTime = 0;
    }

    private double calcCurrentSpeed() {
        LocalPlayer player = Minecraft.getInstance().player;
        long time = System.nanoTime();
        double x = player.getX();
        double z = player.getZ();
        double speed;

        if (_prevTime != 0) {
            double timeDelta = time - _prevTime;
            double xDelta = x - _prevX;
            double zDelta = z - _prevZ;
            double distance = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(zDelta, 2));

            speed = 1000000000.0 * distance / timeDelta;
            if (speed > _maxSpeed)
                speed = 0;
        } else {
            speed = 0;
        }

        _prevTime = time;
        _prevX = x;
        _prevZ = z;

        return speed;
    }

    public void render(PoseStack poseStack, int radarDisplayX, int radarDisplayY, int radarRadius) {
        final int yMargin = 2;

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int windowHeight = minecraft.getWindow().getGuiScaledHeight();

        float xOffset = -font.width(_speedText) / 2f;

        float yOffset = radarDisplayY + radarRadius + font.lineHeight + yMargin > windowHeight
                ? -radarRadius - font.lineHeight - yMargin // Show at the top of the radar
                : radarRadius + yMargin; // Show at the bottom of the radar

        poseStack.pushPose();
        poseStack.translate(radarDisplayX, radarDisplayY, 0);

        font.draw(poseStack, _speedText, xOffset, yOffset, Color.WHITE.getRGB());

        poseStack.popPose();
    }
}