package com.aleksey.combatradar;

import static com.mumfrey.liteloader.gl.GL.*;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.entities.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * @author Aleksey Terzi
 */
public class Radar
{
    private RadarConfig _config;

    // Calculated settings
    private int _radarRadius;
    private float _radarScale;
    private int _radarDisplayX;
    private int _radarDisplayY;

    private List<RadarEntity> _entities = new ArrayList<RadarEntity>();
    private Map<String, String> _players;

    public Radar(RadarConfig config) {
        _config = config;
    }

    public void calcSettings(Minecraft minecraft) {
        ScaledResolution res = new ScaledResolution(minecraft);
        int radarDiameter = (int) ((res.getScaledHeight() - 2) * _config.getRadarSize());

        _radarRadius = radarDiameter / 2;

        int windowInnerWidth = res.getScaledWidth() - radarDiameter;
        int windowInnerHeight = res.getScaledHeight() - radarDiameter;

        _radarDisplayX = _radarRadius + 1 + (int) (_config.getRadarX() * (windowInnerWidth - 2));
        _radarDisplayY = _radarRadius + 1 + (int) (_config.getRadarY() * (windowInnerHeight - 2));

        _radarScale = (float) _radarRadius / _config.getRadarDistance();
    }

    public void render(Minecraft minecraft)
    {
        if(!_config.getEnabled())
            return;

        glPushMatrix();
        glTranslatef(_radarDisplayX, _radarDisplayY, 0);
        glRotatef(-minecraft.player.rotationYaw, 0.0F, 0.0F, 1.0F);

        renderCircle(_radarRadius, true);

        glLineWidth(2.0f);
        renderCircle(_radarRadius, false);
        glLineWidth(1.0f);

        renderLines(_radarRadius);
        renderNonPlayerEntities(minecraft);

        glRotatef(minecraft.player.rotationYaw, 0.0F, 0.0F, 1.0F);
        renderTriangle();

        glRotatef(-minecraft.player.rotationYaw, 0.0F, 0.0F, 1.0F);
        renderPlayerEntities(minecraft);

        glPopMatrix();
    }

    private void renderNonPlayerEntities(Minecraft minecraft) {
        glPushMatrix();
        glScalef(_radarScale, _radarScale, _radarScale);

        for(RadarEntity radarEntity : _entities) {
            if(!(radarEntity instanceof PlayerRadarEntity))
                radarEntity.render(minecraft);
        }

        glPopMatrix();
    }

    private void renderPlayerEntities(Minecraft minecraft) {
        glPushMatrix();
        glScalef(_radarScale, _radarScale, _radarScale);

        for(RadarEntity radarEntity : _entities) {
            if(radarEntity instanceof PlayerRadarEntity)
                radarEntity.render(minecraft);
        }

        glPopMatrix();
    }

    private void renderTriangle() {
        glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        glColor4f(1f, 1f, 1f, _config.getRadarOpacity() + 0.5F);
        glEnableBlend();
        glDisableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
        buffer.pos(0, 3, 0.0D).endVertex();
        buffer.pos(3, - 3, 0.0D).endVertex();
        buffer.pos(-3, -3, 0.0D).endVertex();
        tessellator.draw();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        glEnableTexture2D();
        glDisableBlend();
        glRotatef(-180.0F, 0.0F, 0.0F, 1.0F);
    }


    private void renderLines(float radius) {
        glLineWidth(2.0f);
        glDisableTexture2D();
        glDisableLighting();

        final float cos45 = 0.7071f;
        float diagonalInner = cos45 * _radarScale;
        float diagonalOuter = cos45 * radius;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_LINES, DefaultVertexFormats.POSITION);

        buffer.pos(0, -radius, 0f).endVertex();
        buffer.pos(0, -_radarScale, 0f).endVertex();
        buffer.pos(0, _radarScale, 0f).endVertex();
        buffer.pos(0, radius, 0f).endVertex();

        buffer.pos(-radius, 0, 0f).endVertex();
        buffer.pos(-_radarScale, 0, 0f).endVertex();
        buffer.pos(_radarScale, 0, 0f).endVertex();
        buffer.pos(radius, 0, 0f).endVertex();

        buffer.pos(-diagonalOuter, -diagonalOuter, 0f).endVertex();
        buffer.pos(-diagonalInner, -diagonalInner, 0f).endVertex();
        buffer.pos(diagonalInner, diagonalInner, 0f).endVertex();
        buffer.pos(diagonalOuter, diagonalOuter, 0f).endVertex();

        buffer.pos(-diagonalOuter, diagonalOuter, 0f).endVertex();
        buffer.pos(-diagonalInner, diagonalInner, 0f).endVertex();
        buffer.pos(diagonalInner, -diagonalInner, 0f).endVertex();
        buffer.pos(diagonalOuter, -diagonalOuter, 0f).endVertex();

        tessellator.draw();

        glDisableBlend();
        glEnableTexture2D();
    }

    private void renderCircle(double radius, boolean fill) {
        float opacity = fill ? _config.getRadarOpacity() : _config.getRadarOpacity() + 0.5f;
        int bufferType = fill ? GL_TRIANGLE_FAN : GL_LINE_LOOP;

        glEnableBlend();
        glDisableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(_config.getRadarColor().getRed() / 255.0f, _config.getRadarColor().getGreen() / 255.0f, _config.getRadarColor().getBlue() / 255.0f, opacity);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(bufferType, DefaultVertexFormats.POSITION);

        for (int i = 0; i <= 360; i++) {
            double x = Math.sin(i * Math.PI / 180.0D) * radius;
            double y = Math.cos(i * Math.PI / 180.0D) * radius;
            buffer.pos(x, y, 0.0D).endVertex();
        }
        tessellator.draw();

        GL11.glDisable(GL_LINE_SMOOTH);
        glEnableTexture2D();
        glDisableBlend();
    }

    public int scanEntities(Minecraft minecraft) {
        Map<String, String> oldPlayers = _players;

        _entities.clear();
        _players = new HashMap<String, String>();

        EntitySettings settings = new EntitySettings();
        settings.radarDistanceSq = _config.getRadarDistance() * _config.getRadarDistance();
        settings.iconScale = _config.getIconScale();
        settings.iconOpacity = 1;
        settings.radarScale = _radarScale;
        settings.fontScale = _config.getFontScale();
        settings.neutralPlayerColor = _config.getPlayerTypeInfo(PlayerType.Neutral).color;
        settings.allyPlayerColor = _config.getPlayerTypeInfo(PlayerType.Ally).color;
        settings.enemyPlayerColor = _config.getPlayerTypeInfo(PlayerType.Enemy).color;
        settings.showPlayerNames = _config.getShowPlayerNames();
        settings.showExtraPlayerInfo = _config.getShowExtraPlayerInfo();

        List<Entity> entities = minecraft.world.loadedEntityList;

        for(Entity entity : entities) {
            if(entity == minecraft.player || !_config.isEntityEnabled(entity))
                continue;

            RadarEntity radarEntity;

            if (entity instanceof EntityXPOrb) {
                radarEntity = new CustomRadarEntity(entity, settings, "icons/xp_orb.png");
            } else if (entity instanceof EntityItem) {
                radarEntity = new ItemRadarEntity(entity, settings);
            } else if (entity instanceof EntityOtherPlayerMP) {
                PlayerType playerType = _config.getPlayerType(entity.getName());
                radarEntity = new PlayerRadarEntity(entity, settings, playerType);

                String playerKey = entity.getName().toLowerCase();

                _players.put(playerKey, entity.getName());

                if(oldPlayers == null || !oldPlayers.containsKey(playerKey)) {
                    if(_config.getPlayerTypeInfo(playerType).ping) {
                        float playerPitch = .5f + 1.5f * new Random(playerKey.hashCode()).nextFloat();
                        minecraft.player.playSound(new SoundEvent(new ResourceLocation("block.note.pling")), 1, playerPitch);
                    }
                } else {
                    oldPlayers.remove(playerKey);
                }
            } else if (entity instanceof EntityBoat) {
                radarEntity = new ItemRadarEntity(entity, settings, new ItemStack(Items.BOAT));
            } else if (entity instanceof EntityMinecart) {
                radarEntity = new ItemRadarEntity(entity, settings, new ItemStack(Items.MINECART));
            } else {
                radarEntity = new LiveRadarEntity(entity, settings);
            }

            _entities.add(radarEntity);
        }

        savePlayerCords(oldPlayers);

        return _entities.size();
    }

    private void savePlayerCords(Map<String, String> oldPlayers) {
        if(oldPlayers == null)
            return;

        for(String playerName : oldPlayers.values()) {
        }
    }
}
