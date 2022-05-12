package com.aleksey.combatradar.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * @author Aleksey Terzi
 */
public class SmallButton extends Button {
    public SmallButton(int x, int y, int width, int height, Component text, Button.OnPress onPress) {
        super(x, y, width, height, text, onPress);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int textureIndex = this.getYImage(this.isHoveredOrFocused());
        int textureY = 46 + textureIndex * 20;
        int textureLeftWidth = this.width / 2;
        int textureRightWidth = this.width - textureLeftWidth;
        int textureXOffset = 200 - textureRightWidth;
        int textureTopHeight = this.height / 2;
        int textureBottomHeight = this.height - textureTopHeight;

        this.blit(poseStack, this.x, this.y, 0, textureY, textureLeftWidth, textureTopHeight);
        this.blit(poseStack, this.x + textureLeftWidth, this.y, textureXOffset, textureY, textureRightWidth, textureTopHeight);
        this.blit(poseStack, this.x, this.y + textureTopHeight, 0, textureY + 20 - textureBottomHeight, textureLeftWidth, textureBottomHeight);
        this.blit(poseStack, this.x + textureLeftWidth, this.y + textureTopHeight, textureXOffset, textureY + 20 - textureBottomHeight, textureRightWidth, textureBottomHeight);

        int textColor = this.active ? 16777215 : 10526880;
        drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
