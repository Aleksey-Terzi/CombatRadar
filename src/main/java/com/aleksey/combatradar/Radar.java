package com.aleksey.combatradar;

import static com.mumfrey.liteloader.gl.GL.*;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.PlayerTypeInfo;
import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.entities.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
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
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * @author Aleksey Terzi
 */
public class Radar
{
    private static class PlayerInfo {
        public String playerName;
        public double posX;
        public double posY;
        public double posZ;

        public PlayerInfo(AbstractClientPlayer player) {
            this.playerName = player.getName();
            this.posX = player.posX;
            this.posY = player.posY;
            this.posZ = player.posZ;
        }
    }

    private enum MessageReason { Login, Logout, Appeared, Disappeared}
    private static class MessageInfo {
        public String playerName;
        public PlayerInfo playerInfo;
        public MessageReason reason;
        public boolean log;

        public MessageInfo(String playerName, MessageReason reason) {
            this.playerName = playerName;
            this.playerInfo = null;
            this.reason = reason;
            this.log = true;
        }

        public MessageInfo(PlayerInfo playerInfo, MessageReason reason, boolean log) {
            this.playerName = playerInfo.playerName;
            this.playerInfo = playerInfo;
            this.reason = reason;
            this.log = log;
        }
    }

    private static class PlayerSoundInfo {
        public String soundEventName;
        public UUID playerKey;

        public PlayerSoundInfo(String soundEventName, UUID playerKey) {
            this.soundEventName = soundEventName;
            this.playerKey = playerKey;
        }
    }

    private RadarConfig _config;

    // Calculated settings
    private int _radarRadius;
    private float _radarScale;
    private int _radarDisplayX;
    private int _radarDisplayY;

    private List<RadarEntity> _entities = new ArrayList<>();
    private Map<UUID, PlayerInfo> _radarPlayers;
    private Map<UUID, String> _onlinePlayers;

    private HashMap<UUID, MessageInfo> _messages = new HashMap<>();
    private List<PlayerSoundInfo> _sounds = new ArrayList<>();

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
        if(!_config.getEnabled() || _radarRadius == 0)
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
        _entities.clear();
        _sounds.clear();
        _messages.clear();

        scanRadarEntities(minecraft);

        if(_config.getLogPlayerStatus()) {
            scanOnlinePlayers(minecraft);
        }

        return _entities.size();
    }

    private void scanRadarEntities(Minecraft minecraft) {
        Map<UUID, PlayerInfo> oldPlayers = _radarPlayers;

        _radarPlayers = new HashMap<UUID, PlayerInfo>();

        EntitySettings settings = createEntitySettings();

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

                UUID playerKey = entity.getUniqueID();
                PlayerInfo playerInfo = new PlayerInfo((EntityOtherPlayerMP)entity);

                _radarPlayers.put(playerKey, playerInfo);

                if(oldPlayers == null || !oldPlayers.containsKey(playerKey)) {
                    PlayerTypeInfo playerTypeInfo = _config.getPlayerTypeInfo(playerType);

                    if(playerTypeInfo.ping) {
                        _sounds.add(new PlayerSoundInfo(playerTypeInfo.soundEventName, playerKey));
                    }

                    if(playerTypeInfo.ping || _config.getLogPlayerStatus()) {
                       _messages.put(playerKey, new MessageInfo(playerInfo, MessageReason.Appeared, playerTypeInfo.ping));
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

        if(oldPlayers != null) {
            for(UUID playerKey : oldPlayers.keySet()) {
                PlayerInfo playerInfo = oldPlayers.get(playerKey);
                PlayerType playerType = _config.getPlayerType(playerInfo.playerName);
                PlayerTypeInfo playerTypeInfo = _config.getPlayerTypeInfo(playerType);

                if(playerTypeInfo.ping || _config.getLogPlayerStatus()) {
                    _messages.put(playerKey, new MessageInfo(playerInfo, MessageReason.Disappeared, playerTypeInfo.ping));
                }
            }
        }
    }

    private EntitySettings createEntitySettings() {
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

        return settings;
    }

    private void scanOnlinePlayers(Minecraft minecraft) {
        Collection<NetworkPlayerInfo> players = minecraft.getConnection().getPlayerInfoMap();
        Map<UUID, String> oldOnlinePlayers = _onlinePlayers;

        _onlinePlayers = new HashMap<UUID, String>();

        for(NetworkPlayerInfo p : players) {
            UUID playerKey = p.getGameProfile().getId();

            if(playerKey.equals(minecraft.player.getUniqueID())) {
                continue;
            }

            String playerName = TextFormatting.getTextWithoutFormattingCodes(p.getGameProfile().getName());

            _onlinePlayers.put(playerKey, playerName);

            if(oldOnlinePlayers == null || !oldOnlinePlayers.containsKey(playerKey)) {
                MessageInfo message = _messages.get(playerKey);

                if(message != null) {
                    message.reason = MessageReason.Login;
                    message.log = true;
                } else {
                    _messages.put(playerKey, new MessageInfo(playerName, MessageReason.Login));
                }
            } else {
                oldOnlinePlayers.remove(playerKey);
            }
        }

        if(oldOnlinePlayers != null) {
            for (UUID playerKey : oldOnlinePlayers.keySet()) {
                MessageInfo message = _messages.get(playerKey);

                if (message != null) {
                    message.reason = MessageReason.Logout;
                    message.log = true;
                } else {
                    _messages.put(playerKey, new MessageInfo(oldOnlinePlayers.get(playerKey), MessageReason.Logout));
                }
            }
        }
    }

    public void playSounds(Minecraft minecraft) {
        for(PlayerSoundInfo sound : _sounds) {
            SoundHelper.playSound(minecraft, sound.soundEventName, sound.playerKey);
        }
    }

    public void sendMessages(Minecraft minecraft) {
        for(MessageInfo message : _messages.values()) {
            if(message.log) {
                sendMessage(minecraft, message);
            }
        }
    }

    private void sendMessage(Minecraft minecraft, MessageInfo messageInfo) {
        ITextComponent text = new TextComponentString("[CombatRadar] ").setStyle(new Style().setColor(TextFormatting.DARK_AQUA));

        TextFormatting playerColor;
        PlayerType playerType = _config.getPlayerType(messageInfo.playerName);

        switch(playerType) {
            case Ally:
                playerColor = TextFormatting.GREEN;
                break;
            case Enemy:
                playerColor = TextFormatting.DARK_RED;
                break;
            default:
                playerColor = TextFormatting.WHITE;
                break;
        }

        text = text.appendSibling(new TextComponentString(messageInfo.playerName).setStyle(new Style().setColor(playerColor)));

        String actionText;
        TextFormatting actionColor;

        switch(messageInfo.reason) {
            case Login:
                actionText = " joined the game";
                actionColor = messageInfo.playerInfo != null ? TextFormatting.YELLOW : TextFormatting.DARK_GREEN;
                break;
            case Logout:
                actionText = " left the game";
                actionColor = TextFormatting.DARK_GREEN;
                break;
            case Appeared:
                actionText = " appeared on radar";
                actionColor = TextFormatting.YELLOW;
                break;
            case Disappeared:
                actionText = " disappeared from radar";
                actionColor = TextFormatting.YELLOW;
                break;
            default:
                return;
        }

        text = text.appendSibling(new TextComponentString(actionText).setStyle(new Style().setColor(actionColor)));

        if(messageInfo.playerInfo != null) {
            ITextComponent coordText;

            if(_config.getIsJourneyMapEnabled()) {
                coordText = getJourneyMapCoord(messageInfo.playerInfo);
            } else if(_config.getIsVoxelMapEnabled()) {
                coordText = getVoxelMapCoord(messageInfo.playerInfo);
            } else {
                coordText = new TextComponentString(getChatCoordText(messageInfo.playerInfo, false, true)).setStyle(new Style().setColor(actionColor));
            }

            text = text
                    .appendSibling(new TextComponentString(" at ").setStyle(new Style().setColor(actionColor)))
                    .appendSibling(coordText);
        }

        minecraft.player.sendMessage(text);
    }

    private static ITextComponent getJourneyMapCoord(PlayerInfo playerInfo) {
        ITextComponent hover = new TextComponentString("JourneyMap: ").setStyle(new Style().setColor(TextFormatting.YELLOW));
        hover = hover.appendSibling(new TextComponentString("Click to create Waypoint.\nCtrl+Click to view on map.").setStyle(new Style().setColor(TextFormatting.AQUA)));

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/jm wpedit " + getChatCoordText(playerInfo, true, true));

        Style coordStyle = new Style()
                .setClickEvent(clickEvent)
                .setHoverEvent(hoverEvent)
                .setColor(TextFormatting.AQUA);

        return new TextComponentString(getChatCoordText(playerInfo, false, true)).setStyle(coordStyle);
    }

    private static ITextComponent getVoxelMapCoord(PlayerInfo playerInfo) {
        ITextComponent hover = new TextComponentString("Click to highlight coordinate,\nor control-click to add/edit waypoint.")
                .setStyle(new Style().setColor(TextFormatting.WHITE));

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/newWaypoint " + getChatCoordText(playerInfo, true, false));

        Style coordStyle = new Style()
                .setClickEvent(clickEvent)
                .setHoverEvent(hoverEvent)
                .setColor(TextFormatting.AQUA);

        return new TextComponentString(getChatCoordText(playerInfo, false, true)).setStyle(coordStyle);
    }

    private static String getChatCoordText(PlayerInfo playerInfo, boolean includeName, boolean includeBrackets) {
        StringBuilder coordText = new StringBuilder();

        if(includeBrackets) {
            coordText.append("[");
        }

        coordText.append("x:");
        coordText.append((int)playerInfo.posX);
        coordText.append(", y:");
        coordText.append((int)playerInfo.posY);
        coordText.append(", z:");
        coordText.append((int)playerInfo.posZ);

        if(includeName) {
            coordText.append(", name:");
            coordText.append(playerInfo.playerName);
        }

        if(includeBrackets) {
            coordText.append("]");
        }

        return coordText.toString();
    }
}