package com.aleksey.combatradar.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class GuiCheckButton extends GuiButton {
    private static final ResourceLocation _texture = new ResourceLocation("combatradar", "textures/gui/checkbox.png");

    public static final int BUTTON_HEIGHT = 14;

    private static final int TEXTURE_SIZE = 7;
    private static final int CHECKED_TEXTURE_X = 8;
    private static final int UNCHECKED_TEXTURE_X = 0;
    private static final int INDENT = 9;

    private boolean _checked;

    public boolean isChecked() { return _checked; }
    public void setChecked(boolean value) { _checked = value; }

    public GuiCheckButton(int id, int x, int y, int width, String name) {
        super(id, x, y, width, BUTTON_HEIGHT, name);
    }

    @Override
    public int getHoverState(boolean hovered) {
        return 0;
    }

    @Override
    public void drawButton(Minecraft minecraft, int xPos, int yPos, float p_drawButton_4_) {
        if(!this.visible) {
            return;
        }

        this.hovered = xPos >= this.x && yPos >= this.y && xPos < this.x + this.width && yPos < this.y + this.height;

        int textureX = _checked ? CHECKED_TEXTURE_X : UNCHECKED_TEXTURE_X;

        minecraft.getTextureManager().bindTexture(_texture);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        drawTexturedModalRect(this.x, this.y + (this.height - TEXTURE_SIZE) / 2, textureX, 0, TEXTURE_SIZE, TEXTURE_SIZE);

        int textColor = this.hovered ? 16777120 : Color.LIGHT_GRAY.getRGB();

        minecraft.fontRenderer.drawString(this.displayString, this.x + INDENT, this.y + (this.height - 8) / 2, textColor);
    }

    @Override
    public void playPressSound(SoundHandler p_playPressSound_1_) {
    }
}
