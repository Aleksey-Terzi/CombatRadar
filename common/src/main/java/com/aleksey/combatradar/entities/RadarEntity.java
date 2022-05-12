package com.aleksey.combatradar.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;

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

    public final void render(PoseStack poseStack, float partialTicks) {
        Player player = Minecraft.getInstance().player;

        double displayX = getPartialX(player, partialTicks) - getPartialX(_entity, partialTicks);
        double displayZ = getPartialZ(player, partialTicks) - getPartialZ(_entity, partialTicks); // Convert to 2D where Z is Y
        double distanceSq = Mth.lengthSquared(displayX, displayZ);

        if(distanceSq > _settings.radarDistanceSq)
            return;

        renderInternal(poseStack, displayX, displayZ, partialTicks);
    }

    private static double getPartialX(Entity entity, float partialTicks) {
        return getPartial(entity.xOld, entity.getX(), partialTicks);
    }

    private static double getPartialZ(Entity entity, float partialTicks) {
        return getPartial(entity.zOld, entity.getZ(), partialTicks);
    }

    private static double getPartial(double oldValue, double newValue, float partialTicks) {
        return oldValue + (newValue - oldValue) * partialTicks;
    }

    protected abstract void renderInternal(PoseStack poseStack, double displayX, double displayY, float partialTicks);
}
