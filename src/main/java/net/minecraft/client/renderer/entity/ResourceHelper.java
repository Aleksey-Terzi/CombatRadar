package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * @author Aleksey Terzi
 */
public class ResourceHelper {
    /**
     * gives us access to the protected getEntityTexture method
     */
    public static ResourceLocation getEntityTexture(Render r, Entity e) {
        return r.getEntityTexture(e);
    }
}
