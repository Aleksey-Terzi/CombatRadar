package com.aleksey.combatradar.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class CheckButton extends Button {
    private static final ResourceLocation _texture = new ResourceLocation("combatradar", "textures/gui/checkbox.png");
    public static final int BUTTON_HEIGHT = 14;

    private static final int TEXTURE_SIZE = 7;
    private static final int CHECKED_TEXTURE_X = 8;
    private static final int UNCHECKED_TEXTURE_X = 0;
    private static final int INDENT = 9;

    private boolean _checked;

    public void setChecked(boolean value) { _checked = value; }

    public CheckButton(int x, int y, int width, String name, Button.OnPress onPress) {
        super(x, y, width, BUTTON_HEIGHT, new TextComponent(name), onPress);
    }

    @Override
    public void playDownSound(SoundManager p_93665_) {
    }

    @Override
    public void renderButton(PoseStack poseStack, int xPos, int yPos, float p_93846_) {
        Minecraft minecraft = Minecraft.getInstance();

        int textureX = _checked ? CHECKED_TEXTURE_X : UNCHECKED_TEXTURE_X;

        RenderSystem.setShaderTexture(0, _texture);
        RenderSystem.enableDepthTest();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        blit(poseStack, this.x, this.y + (this.height - TEXTURE_SIZE) / 2, textureX, 0, TEXTURE_SIZE, TEXTURE_SIZE);

        int textColor = this.isHovered ? 16777120 : Color.LIGHT_GRAY.getRGB();

        minecraft.font.draw(poseStack, this.getMessage(), this.x + INDENT, this.y + (this.height - 8) / 2, textColor);
    }
}
