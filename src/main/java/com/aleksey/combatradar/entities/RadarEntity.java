package com.aleksey.combatradar.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * @author Aleksey Terzi
 */
public abstract class RadarEntity {
    private Entity _entity;
    private EntitySettings _settings;

    public RadarEntity(Entity entity, EntitySettings settings) {
        _entity = entity;
        _settings = settings;
    }

    protected Entity getEntity() {
        return _entity;
    }

    protected EntitySettings getSettings() {
        return _settings;
    }

    public final void render(Minecraft minecraft) {
        double displayX = minecraft.player.posX - _entity.posX;
        double displayZ = minecraft.player.posZ - _entity.posZ;
        double distanceSq = displayX * displayX + displayZ * displayZ;

        if(distanceSq > _settings.radarDistanceSq)
            return;

        renderInternal(minecraft, (float)displayX, (float)displayZ);
    }

    protected abstract void renderInternal(Minecraft minecraft, float displayX, float displayY);
}
