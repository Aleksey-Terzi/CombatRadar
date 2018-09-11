package com.aleksey.combatradar.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import static com.mumfrey.liteloader.gl.GL.*;

/**
 * @author Aleksey Terzi
 */
public class ItemRadarEntity extends RadarEntity {
    private ItemStack _item;

    public ItemRadarEntity(Entity entity, EntitySettings settings) {
        super(entity, settings);

        _item = ((EntityItem)getEntity()).getItem();
    }

    public ItemRadarEntity(Entity entity, EntitySettings settings, ItemStack item) {
        super(entity, settings);

        _item = item;
    }

    @Override
    protected void renderInternal(Minecraft minecraft, float displayX, float displayY) {
        float iconScale = getSettings().iconScale;

        glPushMatrix();
        glTranslatef(displayX, displayY, 0);
        glRotatef(minecraft.player.rotationYaw, 0.0F, 0.0F, 1.0F);
        glScalef(iconScale, iconScale, iconScale);

        minecraft.getRenderItem().renderItemIntoGUI(_item, -8, -8);
        glDisableLighting();

        glPopMatrix();
    }
}
