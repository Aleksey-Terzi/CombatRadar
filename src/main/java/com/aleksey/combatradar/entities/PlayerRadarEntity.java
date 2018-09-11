package com.aleksey.combatradar.entities;

import com.aleksey.combatradar.config.PlayerType;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Map;

import static com.mumfrey.liteloader.gl.GL.*;

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
    protected void renderInternal(Minecraft minecraft, float displayX, float displayY) {
        EntityOtherPlayerMP player = (EntityOtherPlayerMP)getEntity();

        glColor4f(1.0F, 1.0F, 1.0F, getSettings().iconOpacity);
        glEnableBlend();

        glPushMatrix();
        glTranslatef(displayX, displayY, 0);
        glRotatef(minecraft.player.rotationYaw, 0.0F, 0.0F, 1.0F);

        glPushMatrix();
        glScalef(getSettings().iconScale, getSettings().iconScale, getSettings().iconScale);

        try {
            GameProfile gameProfile = player.getGameProfile();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> texMap = minecraft.getSkinManager().loadSkinFromCache(gameProfile);

            if (texMap.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture profileTexture = texMap.get(MinecraftProfileTexture.Type.SKIN);
                minecraft.getTextureManager().bindTexture(minecraft.getSkinManager().loadSkin(profileTexture, MinecraftProfileTexture.Type.SKIN));
                Gui.drawScaledCustomSizeModalRect(-8, -8, 8, 8, 8, 8, 16, 16, 64, 64);
            } else {
                minecraft.getTextureManager().bindTexture(new ResourceLocation("icons/player.png"));
                Gui.drawScaledCustomSizeModalRect(-8, -8, 0, 0, 8, 8, 16, 16, 8, 8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        glDisableBlend();
        glPopMatrix();

        if (getSettings().showPlayerNames) {
            Color color = _playerType == PlayerType.Ally ? getSettings().allyPlayerColor : (_playerType == PlayerType.Enemy ? getSettings().enemyPlayerColor : getSettings().neutralPlayerColor);

            glPushMatrix();
            glScalef(getSettings().fontScale, getSettings().fontScale, getSettings().fontScale);

            String playerName = player.getName();
            if (getSettings().showExtraPlayerInfo) {
                playerName += " (" + (int)getDistanceToEntity(minecraft.player, player) + "m)(Y" + (int) player.posY + ")";
            }
            int yOffset = -4 + (int) ((getSettings().iconScale * getSettings().radarScale + 8));
            drawCenteredString(minecraft.fontRenderer, playerName, 0, yOffset, color.getRGB());

            glPopMatrix();
        }

        glPopMatrix();
    }

    private static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color)
    {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color, true);
    }

    private static float getDistanceToEntity(Entity e1, Entity e2)
    {
        float f = (float)(e1.posX - e2.posX);
        float f1 = (float)(e1.posY - e2.posY);
        float f2 = (float)(e1.posZ - e2.posZ);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }
}
