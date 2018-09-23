package com.aleksey.combatradar.config;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author Aleksey Terzi
 */
public class RadarConfig {
    private static class PlayerInfo {
        public String name;
        public PlayerType type;

        public PlayerInfo(String name, PlayerType type) {
            this.name = name;
            this.type = type;
        }
    }

    private File _configFile;
    private KeyBinding _settingsKey;
    private boolean _enabled = true;
    private float _radarOpacity = 0.5f;
    private Color _radarColor = new Color(128, 128, 128);
    private float _radarSize = 0.4f;
    private int _radarDistance = 128;
    private float _radarX = 0;
    private float _radarY = 0;
    private float _iconScale = 0.5f;
    private float _fontScale = 1.4f;
    private boolean _showPlayerNames = true;
    private boolean _showExtraPlayerInfo = true;
    private boolean _logPlayerStatus = true;
    private List<RadarEntityInfo> _entities;
    private Map<GroupType, Boolean> _groups;
    private Map<String, PlayerInfo> _players;
    private Map<PlayerType, PlayerTypeInfo> _playerTypes;

    // Calculated settings
    private boolean _isJourneyMapEnabled;
    private boolean _isVoxelMapEnabled;

    public KeyBinding getSettingsKey() { return _settingsKey; }

    public boolean getEnabled() { return _enabled; }
    public void setEnabled(boolean value) { _enabled = value; }

    public float getRadarOpacity() { return _radarOpacity; }
    public boolean setRadarOpacity(float value) {
        if(_radarOpacity == value)
            return false;

        _radarOpacity = value;

        return true;
    }

    public Color getRadarColor() { return _radarColor; }
    public boolean setRadarColor(Color value) {
        if(_radarColor == value)
            return false;

        _radarColor = value;

        return true;
    }

    public float getRadarSize() { return _radarSize; }
    public boolean setRadarSize(float value) {
        if(_radarSize == value)
            return false;

        _radarSize = value;

        return true;
    }

    public int getRadarDistance() { return _radarDistance; }
    public boolean setRadarDistance(int value) {
        if(_radarDistance == value)
            return false;

        _radarDistance = value;

        return true;
    }

    public float getRadarX() { return _radarX; }
    public void setRadarX(float value) { _radarX = value; }

    public float getRadarY() { return _radarY; }
    public void setRadarY(float value) { _radarY = value; }

    public float getIconScale() { return _iconScale; }
    public boolean setIconScale(float value) {
        if(_iconScale == value)
            return false;

        _iconScale = value;

        return true;
    }

    public float getFontScale() { return _fontScale; }
    public boolean setFontScale(float value) {
        if(_fontScale == value)
            return false;

        _fontScale = value;

        return true;
    }

    public PlayerTypeInfo getPlayerTypeInfo(PlayerType playerType) { return _playerTypes.get(playerType); }

    public boolean getShowPlayerNames() { return _showPlayerNames; }
    public void setShowPlayerNames(boolean value) { _showPlayerNames = value; }

    public boolean getShowExtraPlayerInfo() { return _showExtraPlayerInfo; }
    public void setShowExtraPlayerInfo(boolean value) { _showExtraPlayerInfo = value; }

    public boolean getLogPlayerStatus() { return _logPlayerStatus; }
    public void setLogPlayerStatus(boolean value) { _logPlayerStatus = value; }

    public List<RadarEntityInfo> getEntities() { return _entities; }

    public void setEntityEnabled(String name, boolean enabled) {
        for(RadarEntityInfo info : _entities) {
            if(info.getName().equalsIgnoreCase(name)) {
                info.setEnabled(enabled);
                return;
            }
        }
    }

    public boolean isEntityEnabled(Entity entity) {
        Class<? extends Entity> entityClass;
        PlayerType playerType = null;

        if(entity instanceof EntityItem) {
            entityClass = EntityItem.class;
        } else if(entity instanceof EntityBoat) {
            entityClass = EntityBoat.class;
        } else if(entity instanceof EntityMinecart) {
            entityClass = EntityMinecart.class;
        } else if(entity instanceof EntityPlayer) {
            entityClass = EntityPlayer.class;
            playerType = getPlayerType(entity.getName());
        } else {
            entityClass = entity.getClass();
        }

        for(RadarEntityInfo info : _entities) {
            if(info.getEntityClass().equals(entityClass)) {
                if(playerType == null || ((PlayerRadarEntityInfo)info).getPlayerType() == playerType) {
                    return info.getEnabled() && _groups.get(info.getGroupType());
                }
            }
        }

        return false;
    }

    public boolean isGroupEnabled(GroupType groupType) {
        return _groups.get(groupType);
    }

    public void setGroupEnabled(GroupType groupType, boolean value) {
        _groups.put(groupType, value);
    }

    public PlayerType getPlayerType(String playerName) {
        String key = playerName.toLowerCase();
        PlayerInfo info = _players.get(key);

        return info != null ? info.type : PlayerType.Neutral;
    }

    public void revertNeutralAggressive() {
        boolean enabled = _groups.get(GroupType.Neutral) || _groups.get(GroupType.Aggressive);

        _groups.put(GroupType.Neutral, !enabled);
        _groups.put(GroupType.Aggressive, !enabled);
    }

    public void setPlayerType(String playerName, PlayerType playerType) {
        String key = playerName.toLowerCase();

        if(playerType == PlayerType.Neutral) {
            _players.remove(key);
            return;
        }

        PlayerInfo info = _players.get(key);

        if(info == null)
            _players.put(key, new PlayerInfo(playerName, playerType));
        else
            info.type = playerType;
    }

    public List<String> getPlayers(PlayerType playerType) {
        List<String> result = new ArrayList<String>();

        for(PlayerInfo info : _players.values()) {
            if(info.type == playerType)
                result.add(info.name);
        }

        Collections.sort(result);

        return result;
    }

    public boolean getIsJourneyMapEnabled() { return _isJourneyMapEnabled; }
    public void setIsJourneyMapEnabled(boolean value) { _isJourneyMapEnabled = value; }

    public boolean getIsVoxelMapEnabled() { return _isVoxelMapEnabled; }
    public void setIsVoxelMapEnabled(boolean value) { _isVoxelMapEnabled = value; }

    public RadarConfig(File file, KeyBinding settingsKey) {
        _configFile = file;
        _settingsKey = settingsKey;

        _entities = new ArrayList<RadarEntityInfo>();
        _entities.add(new RadarEntityInfo(EntityBat.class, "Bat", "icons/bat.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityChicken.class, "Chicken", "icons/chicken.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityCow.class, "Cow", "icons/cow/cow.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityHorse.class, "Horse", "icons/horse/horse_chestnut.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityMooshroom.class, "Mooshroom", "icons/cow/mooshroom.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityOcelot.class, "Ocelot", "icons/cat/ocelot.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityPig.class, "Pig", "icons/pig/pig.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityRabbit.class, "Rabbit", "icons/rabbit/white.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntitySheep.class, "Sheep", "icons/sheep/sheep.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntitySquid.class, "Squid", "icons/squid.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityVillager.class, "Villager", "icons/villager/villager.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityWolf.class, "Wolf", "icons/wolf/wolf.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityBlaze.class, "Blaze", "icons/blaze.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityCaveSpider.class, "Cave Spider", "icons/spider/cave_spider.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityCreeper.class, "Creeper", "icons/creeper/creeper.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityEnderman.class, "Enderman", "icons/enderman/enderman.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityGhast.class, "Ghast", "icons/ghast/ghast.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityGuardian.class, "Guardian", "icons/guardian.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityIronGolem.class, "Iron Golem", "icons/iron_golem.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityMagmaCube.class, "Magma Cube", "icons/slime/magmacube.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityPigZombie.class, "Pig Zombie", "icons/zombie_pigman.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntitySilverfish.class, "Silverfish", "icons/silverfish.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntitySkeleton.class, "Skeleton", "icons/skeleton/skeleton.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntitySlime.class, "Slime", "icons/slime/slime.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntitySnowman.class, "Snow Golem", "icons/snowman.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntitySpider.class, "Spider", "icons/spider/spider.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityWitch.class, "Witch", "icons/witch.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityZombie.class, "Zombie", "icons/zombie/zombie.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityItem.class, "Item", "icons/item.png", GroupType.Other));
        _entities.add(new RadarEntityInfo(EntityBoat.class, "Boat", "icons/boat.png", GroupType.Other));
        _entities.add(new RadarEntityInfo(EntityMinecart.class, "Minecart", "icons/minecart.png", GroupType.Other));
        _entities.add(new PlayerRadarEntityInfo(PlayerType.Neutral, "Player (Neutral)", "icons/player.png", GroupType.Other));
        _entities.add(new PlayerRadarEntityInfo(PlayerType.Ally, "Player (Ally)", "icons/player.png", GroupType.Other));
        _entities.add(new PlayerRadarEntityInfo(PlayerType.Enemy, "Player (Enemy)", "icons/player.png", GroupType.Other));
        _entities.add(new RadarEntityInfo(EntityMule.class, "Mule", "icons/horse/mule.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityDonkey.class, "Donkey", "icons/horse/donkey.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityPolarBear.class, "Polar Bear", "icons/bear/polarbear.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityShulker.class, "Shulker", "icons/shulker/shulker.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityHusk.class, "Husk", "icons/zombie/husk.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityStray.class, "Stray", "icons/skeleton/stray.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityXPOrb.class, "XP Orb", "icons/xp_orb.png", GroupType.Other));
        _entities.add(new RadarEntityInfo(EntityWither.class, "Wither", "icons/wither/wither.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityWitherSkeleton.class, "Wither Skeleton", "icons/skeleton/wither_skeleton.png", GroupType.Aggressive));
        //V1.11+
        _entities.add(new RadarEntityInfo(EntityLlama.class, "Llama", "icons/llama/llama.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityParrot.class, "Parrot", "icons/parrot/parrot.png", GroupType.Neutral));
        _entities.add(new RadarEntityInfo(EntityEvoker.class, "Evoker", "icons/illager/evoker.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityIllusionIllager.class, "Illusion Illager", "icons/illager/illusioner.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityVex.class, "Vex", "icons/illager/vex.png", GroupType.Aggressive));
        _entities.add(new RadarEntityInfo(EntityVindicator.class, "Vindicator", "icons/illager/vindicator.png", GroupType.Aggressive));

        Collections.sort(_entities, new RadarEntityInfo.EntityComparator());

        for(int i = 0; i < _entities.size(); i++)
            _entities.get(i).setId(i);

        _groups = new HashMap<GroupType, Boolean>();
        _groups.put(GroupType.Neutral, true);
        _groups.put(GroupType.Aggressive, true);
        _groups.put(GroupType.Other, true);

        _players = new HashMap<String, PlayerInfo>();

        _playerTypes = new HashMap<PlayerType, PlayerTypeInfo>();
        _playerTypes.put(PlayerType.Neutral, new PlayerTypeInfo(Color.WHITE));
        _playerTypes.put(PlayerType.Ally, new PlayerTypeInfo(Color.GREEN));
        _playerTypes.put(PlayerType.Enemy, new PlayerTypeInfo(Color.YELLOW));
    }

    public void save() {
        RadarConfigLoader.save(this, _configFile);
    }

    public boolean load() {
        return RadarConfigLoader.load(this, _configFile);
    }
}