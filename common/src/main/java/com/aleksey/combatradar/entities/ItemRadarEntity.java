package com.aleksey.combatradar.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

/**
 * @author Aleksey Terzi
 */
public class ItemRadarEntity extends RadarEntity {
    private ItemStack _item;

    public ItemRadarEntity(Entity entity, EntitySettings settings) {
        super(entity, settings);

        _item = ((ItemEntity)getEntity()).getItem();
    }

    public ItemRadarEntity(Entity entity, EntitySettings settings, ItemStack item) {
        super(entity, settings);

        _item = item;
    }

    @Override
    protected void renderInternal(PoseStack poseStack, double displayX, double displayY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        float iconScale = getSettings().iconScale;
        float rotationYaw = minecraft.player.getViewYRot(partialTicks);;

        poseStack.pushPose();
        poseStack.translate(displayX, displayY, 0);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotationYaw));
        poseStack.scale(iconScale, iconScale, iconScale);

        renderGuiItem(poseStack, -8, -8);

        poseStack.popPose();
    }

    // The original method is renderGuiItem.renderGuiItem(ItemStack p_115128_, int p_115129_, int p_115130_, BakedModel p_115131_)
    private void renderGuiItem(PoseStack poseStack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        BakedModel bakedModel = renderer.getModel(_item, null, null, 0);

        minecraft.getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.translate(x, y, 100.0F + renderer.blitOffset);
        poseStack.translate(8.0D, 8.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(16.0F, 16.0F, 16.0F);


        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean notUseBlockLight = !bakedModel.usesBlockLight();

        if (notUseBlockLight)
            Lighting.setupForFlatItems();

        renderer.render(_item, ItemTransforms.TransformType.GUI, false, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();

        if (notUseBlockLight)
            Lighting.setupFor3DItems();
    }
}
