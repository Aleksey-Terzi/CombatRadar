package com.aleksey.combatradar.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import static com.mumfrey.liteloader.gl.GL.*;
import static com.mumfrey.liteloader.gl.GL.glDisableBlend;

/**
 * @author Aleksey Terzi
 */
public class CustomRadarEntity extends RadarEntity {
    private ResourceLocation _resourceLocation;

    public CustomRadarEntity(Entity entity, EntitySettings settings, String resourcePath) {
        super(entity, settings);

        _resourceLocation = new ResourceLocation("combatradar", resourcePath);
    }

    @Override
    protected void renderInternal(Minecraft minecraft, float displayX, float displayY) {
        float iconScale = getSettings().iconScale;

        minecraft.getTextureManager().bindTexture(_resourceLocation);
        glColor4f(1.0F, 1.0F, 1.0F, getSettings().iconOpacity);
        glEnableBlend();

        glPushMatrix();
        glTranslatef(displayX, displayY, 0);
        glRotatef(minecraft.player.rotationYaw, 0.0F, 0.0F, 1.0F);
        glScalef(iconScale, iconScale, iconScale);

        Gui.drawModalRectWithCustomSizedTexture(-8, -8, 0, 0, 16, 16, 16, 16);

        glPopMatrix();
        glDisableBlend();
    }
}
