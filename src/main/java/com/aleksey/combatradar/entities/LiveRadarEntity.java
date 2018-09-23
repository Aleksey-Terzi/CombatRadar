package com.aleksey.combatradar.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

import static com.mumfrey.liteloader.gl.GL.*;

/**
 * @author Aleksey Terzi
 */
public class LiveRadarEntity extends RadarEntity {
    private static final String[] HORSE_VARIANTS = {"white", "creamy", "chestnut", "brown", "black", "gray", "darkbrown"};

    private ResourceLocation _resourceLocation;

    public LiveRadarEntity(Entity entity, EntitySettings settings) {
        super(entity, settings);
    }

    @Override
    protected void renderInternal(Minecraft minecraft, float displayX, float displayY) {
        ResourceLocation resourceLocation = getResourceLocation(minecraft);

        if (resourceLocation == null)
            return;

        float iconScale = getSettings().iconScale;

        minecraft.getTextureManager().bindTexture(resourceLocation);
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

    private ResourceLocation getResourceLocation(Minecraft minecraft) {
        if(_resourceLocation == null) {
            try {
                RenderManager renderManager = minecraft.getRenderManager();
                Render render = renderManager.getEntityRenderObject(getEntity());

                if (render instanceof RenderHorse) {
                    EntityHorse horseEntity = (EntityHorse) getEntity();
                    int horseVariant = (0xff & horseEntity.getHorseVariant()) % 7;

                    _resourceLocation = new ResourceLocation("combatradar", "icons/horse/horse_" + HORSE_VARIANTS[horseVariant] + ".png");
                } else if (render instanceof RenderLlama) {
                    _resourceLocation = new ResourceLocation("combatradar", "icons/llama/llama.png");
                } else if (render instanceof RenderParrot) {
                    _resourceLocation = new ResourceLocation("combatradar", "icons/parrot/parrot.png");
                } else if (render instanceof RenderShulker) {
                    _resourceLocation = new ResourceLocation("combatradar", "icons/shulker/shulker.png");
                } else if (render instanceof RenderGhast) {
                    _resourceLocation = new ResourceLocation("combatradar", "icons/ghast/ghast.png");
                } else {
                    ResourceLocation original = ResourceHelper.getEntityTexture(render, getEntity());
                    _resourceLocation = new ResourceLocation("combatradar", original.getResourcePath().replace("textures/entity/", "icons/"));
                }
            } catch (Throwable e) {
                System.out.println("Can't get entityTexture for " + getEntity().getName());
            }
        }

        return _resourceLocation;
    }
}
