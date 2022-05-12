package com.aleksey.combatradar.entities;

import com.aleksey.combatradar.config.PlayerType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class PlayerRadarEntity extends RadarEntity {
    private PlayerType _playerType;

    public PlayerRadarEntity(Entity entity, EntitySettings settings, PlayerType playerType) {
        super(entity, settings);
        _playerType = playerType;
    }

    @Override
    protected void renderInternal(PoseStack poseStack, double displayX, double displayY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RemotePlayer player = (RemotePlayer)getEntity();
        float rotationYaw = minecraft.player.getViewYRot(partialTicks);
        float scale = getSettings().iconScale * 1.7f;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, getSettings().iconOpacity);
        RenderSystem.enableBlend();

        poseStack.pushPose();
        poseStack.translate(displayX, displayY, 0);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotationYaw));

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        renderPlayerIcon(poseStack, player);
        poseStack.popPose();

        RenderSystem.disableBlend();

        if (getSettings().showPlayerNames)
            renderPlayerName(poseStack, player);

        poseStack.popPose();
    }

    private void renderPlayerIcon(PoseStack poseStack, RemotePlayer player) {
        ResourceLocation skin = player.getSkinTextureLocation();

        RenderSystem.setShaderTexture(0, skin);

        Gui.blit(poseStack, -4, -4, 8, 8, 8, 8, 8, 8, 64, 64);
    }

    private void renderPlayerName(PoseStack poseStack, RemotePlayer player) {
        Minecraft minecraft = Minecraft.getInstance();

        Color color = _playerType == PlayerType.Ally
                ? getSettings().allyPlayerColor
                : (_playerType == PlayerType.Enemy ? getSettings().enemyPlayerColor : getSettings().neutralPlayerColor);

        poseStack.pushPose();
        poseStack.scale(getSettings().fontScale, getSettings().fontScale, getSettings().fontScale);

        String playerName = player.getScoreboardName();
        if (getSettings().showExtraPlayerInfo) {
            playerName += " (" + (int)minecraft.player.distanceTo(player) + "m)(Y" + player.getBlockY() + ")";
        }

        Font font = minecraft.font;
        float yOffset = -4 + (int) ((getSettings().iconScale * getSettings().radarScale + 8));
        float xOffset = -font.width(playerName) / 2;

        font.draw(poseStack, playerName, xOffset, yOffset, color.getRGB());

        poseStack.popPose();
    }
}
