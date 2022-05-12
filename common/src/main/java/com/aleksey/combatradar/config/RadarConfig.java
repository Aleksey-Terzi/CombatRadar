package com.aleksey.combatradar.config;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;

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
    private KeyMapping _settingsKey;
    private boolean _enabled = true;
    private float _radarOpacity = 0.5f;
    private Color _radarColor = new Color(128, 128, 128);
    private float _radarSize = 0.4f;
    private int _radarDistance = 128;
    private float _radarX = 0;
    private float _radarY = 0;
    private float _iconScale = 0.8869566f;
    private float _fontScale = 1.4f;
    private boolean _showPlayerNames = true;
    private boolean _showExtraPlayerInfo = true;
    private boolean _logPlayerStatus = true;
    private List<RadarEntityInfo> _entityList;
    private Map<String, RadarEntityInfo> _entityMap;
    private Map<GroupType, Boolean> _groups;
    private Map<String, PlayerInfo> _players;
    private Map<PlayerType, PlayerTypeInfo> _playerTypes;
    private List<String> _playersExcludedFromLog;

    // Calculated settings
    private boolean _isJourneyMapEnabled;
    private boolean _isVoxelMapEnabled;

    public KeyMapping getSettingsKey() { return _settingsKey; }

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

    public List<RadarEntityInfo> getEntityList() { return _entityList; }

    public RadarEntityInfo getEntity(String name) {
        for(RadarEntityInfo info : _entityList) {
            if(info.getName().equalsIgnoreCase(name))
                return info;
        }

        return null;
    }

    public void setEntityEnabled(String name, boolean enabled) {
        getEntity(name).setEnabled(enabled);
    }

    public ResourceLocation getEnabledIcon(Entity entity) {
        String entityClass;

        if(entity instanceof ItemEntity) {
            entityClass = ItemEntity.class.getCanonicalName();
        } else if(entity instanceof Boat) {
            entityClass = Boat.class.getCanonicalName();
        } else if(entity instanceof AbstractMinecart) {
            entityClass = AbstractMinecart.class.getCanonicalName();
        } else if(entity instanceof Player) {
            var playerType = getPlayerType(entity.getName().getString());
            entityClass = Player.class.getCanonicalName() + "." + playerType;
        } else {
            entityClass = entity.getClass().getCanonicalName();
        }

        var info = _entityMap.getOrDefault(entityClass, null);

        return info != null && info.getEnabled() && _groups.get(info.getGroupType())
                ? info.getIcon(entity)
                : null;
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

    public List<String> getPlayersExcludedFromLog() { return _playersExcludedFromLog; }
    public void setPlayersExcludedFromLog(List<String> value) {
        _playersExcludedFromLog = value;

        for(int i = 0; i < _playersExcludedFromLog.size(); i++) {
            _playersExcludedFromLog.set(i, _playersExcludedFromLog.get(i).toUpperCase());
        }
    }

    public boolean isPlayerExcluded(String playerName) {
        String upperPlayerName = playerName.toUpperCase();

        for(String p : _playersExcludedFromLog) {
            if(upperPlayerName.startsWith(p)) {
                return true;
            }
        }

        return false;
    }

    public RadarConfig(File file, KeyMapping settingsKey) {
        _configFile = file;
        _settingsKey = settingsKey;

        _entityList = new ArrayList<>();
        _entityList.add(new RadarEntityInfo(Bat.class, "Bat", "icons/bat.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Chicken.class, "Chicken", "icons/chicken.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Cow.class, "Cow", "icons/cow/cow.png", GroupType.Neutral));
        _entityList.add(new RadarHorseInfo("Horse", "icons/horse/horse_chestnut.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Mule.class, "Mule", "icons/horse/mule.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Donkey.class, "Donkey", "icons/horse/donkey.png", GroupType.Neutral));
        _entityList.add(
                new RadarEntityInfo(Llama.class, "Llama", "icons/llama/llama.png", GroupType.Neutral)
                    .addEntity(TraderLlama.class, "icons/llama/llama_trader.png")
        );
        _entityList.add(new RadarEntityInfo(MushroomCow.class, "Mooshroom", "icons/cow/mooshroom.png", GroupType.Neutral));
        _entityList.add(
                new RadarEntityInfo(Ocelot.class, "Ocelot", "icons/cat/ocelot.png", GroupType.Neutral)
                    .addEntity(Cat.class, "icons/cat/black.png")
        );
        _entityList.add(new RadarEntityInfo(Pig.class, "Pig", "icons/pig/pig.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Rabbit.class, "Rabbit", "icons/rabbit/white.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Sheep.class, "Sheep", "icons/sheep/sheep.png", GroupType.Neutral));
        _entityList.add(
                new RadarEntityInfo(Squid.class, "Squid", "icons/squid.png", GroupType.Neutral)
                    .addEntity(GlowSquid.class, "icons/squid_glow.png")
        );
        _entityList.add(new RadarEntityInfo(Villager.class, "Villager", "icons/villager/villager.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Wolf.class, "Wolf", "icons/wolf/wolf.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Blaze.class, "Blaze", "icons/blaze.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(CaveSpider.class, "Cave Spider", "icons/spider/cave_spider.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Creeper.class, "Creeper", "icons/creeper.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(EnderMan.class, "Enderman", "icons/enderman/enderman.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Ghast.class, "Ghast", "icons/ghast/ghast.png", GroupType.Aggressive));
        _entityList.add(
                new RadarEntityInfo(Guardian.class, "Guardian", "icons/guardian.png", GroupType.Aggressive)
                    .addEntity(ElderGuardian.class, "icons/elder_guardian.png")
        );
        _entityList.add(new RadarEntityInfo(IronGolem.class, "Iron Golem", "icons/iron_golem.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(MagmaCube.class, "Magma Cube", "icons/slime/magmacube.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Silverfish.class, "Silverfish", "icons/silverfish.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Skeleton.class, "Skeleton", "icons/skeleton/skeleton.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Slime.class, "Slime", "icons/slime/slime.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(SnowGolem.class, "Snow Golem", "icons/snowman.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Spider.class, "Spider", "icons/spider/spider.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Witch.class, "Witch", "icons/witch.png", GroupType.Aggressive));
        _entityList.add(
                new RadarEntityInfo(Zombie.class, "Zombie", "icons/zombie/zombie.png", GroupType.Aggressive)
                    .addEntity(Drowned.class, "icons/zombie/drowned.png")
                    .addEntity(Husk.class, "icons/zombie/husk.png")
        );
        _entityList.add(new RadarEntityInfo(ItemEntity.class, "Item", "icons/item.png", GroupType.Other));
        _entityList.add(new RadarEntityInfo(Boat.class, "Boat", "icons/boat.png", GroupType.Other));
        _entityList.add(new RadarEntityInfo(AbstractMinecart.class, "Minecart", "icons/minecart.png", GroupType.Other));
        _entityList.add(new PlayerRadarEntityInfo(PlayerType.Neutral, "Player (Neutral)", "icons/player.png", GroupType.Other));
        _entityList.add(new PlayerRadarEntityInfo(PlayerType.Ally, "Player (Ally)", "icons/player.png", GroupType.Other));
        _entityList.add(new PlayerRadarEntityInfo(PlayerType.Enemy, "Player (Enemy)", "icons/player.png", GroupType.Other));
        _entityList.add(new RadarEntityInfo(PolarBear.class, "Polar Bear", "icons/bear/polarbear.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Shulker.class, "Shulker", "icons/shulker/shulker.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Stray.class, "Stray", "icons/skeleton/stray.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(ExperienceOrb.class, "XP Orb", "icons/xp_orb.png", GroupType.Other));
        _entityList.add(new RadarEntityInfo(WitherBoss.class, "Wither", "icons/wither/wither.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(WitherSkeleton.class, "Wither Skeleton", "icons/skeleton/wither_skeleton.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Parrot.class, "Parrot", "icons/parrot/parrot.png", GroupType.Neutral));

        _entityList.add(
                new RadarEntityInfo(Evoker.class, "Illager", "icons/illager/evoker.png", GroupType.Aggressive)
                    .addEntity(Illusioner.class, "icons/illager/illusioner.png")
                    .addEntity(Vex.class, "icons/illager/vex.png")
                    .addEntity(Vindicator.class, "icons/illager/vindicator.png")
                    .addEntity(Pillager.class, "icons/illager/pillager.png")
                    .addEntity(Ravager.class, "icons/illager/ravager.png")
        );

        // Update to 1.18.2
        _entityList.add(new RadarEntityInfo(Axolotl.class, "Axolotl", "icons/axolotl.png", GroupType.Neutral));
        _entityList.add(
                new RadarEntityInfo(Salmon.class, "Fish", "icons/salmon.png", GroupType.Neutral)
                    .addEntity(Cod.class, "icons/cod.png")
                    .addEntity(Pufferfish.class, "icons/pufferfish.png")
                    .addEntity(TropicalFish.class, "icons/tropical_fish.png")
        );
        _entityList.add(new RadarEntityInfo(Fox.class, "Fox", "icons/fox.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Strider.class, "Strider", "icons/strider.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Turtle.class, "Turtle", "icons/turtle.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Turtle.class, "Trader", "icons/wandering_trader.png", GroupType.Neutral));

        _entityList.add(new RadarEntityInfo(Bee.class, "Bee", "icons/bee.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Dolphin.class, "Dolphin", "icons/dolphin.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Goat.class, "Goat", "icons/goat.png", GroupType.Neutral));
        _entityList.add(new RadarEntityInfo(Panda.class, "Panda", "icons/panda.png", GroupType.Neutral));
        _entityList.add(
                new RadarEntityInfo(Piglin.class, "Piglin", "icons/piglin.png", GroupType.Neutral)
                    .addEntity(ZombifiedPiglin.class, "icons/zombie_pigman.png")
        );

        _entityList.add(new RadarEntityInfo(Endermite.class, "Endermite", "icons/endermite.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Endermite.class, "Hoglin", "icons/hoglin.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Phantom.class, "Phantom", "icons/phantom.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(PiglinBrute.class, "Piglin Brute", "icons/piglin_brute.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(Zoglin.class, "Zoglin", "icons/zoglin.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(ZombieVillager.class, "Zombie Villager", "icons/zombie_villager.png", GroupType.Aggressive));
        _entityList.add(new RadarEntityInfo(EnderDragon.class, "Ender Dragon", "icons/dragon.png", GroupType.Aggressive));

        Collections.sort(_entityList, new RadarEntityInfo.EntityComparator());

        _entityMap = new HashMap<>();

        for(int i = 0; i < _entityList.size(); i++)
            _entityList.get(i).addToMap(_entityMap);

        _groups = new HashMap<GroupType, Boolean>();
        _groups.put(GroupType.Neutral, true);
        _groups.put(GroupType.Aggressive, true);
        _groups.put(GroupType.Other, true);

        _players = new HashMap<String, PlayerInfo>();

        _playerTypes = new HashMap<PlayerType, PlayerTypeInfo>();
        _playerTypes.put(PlayerType.Neutral, new PlayerTypeInfo(Color.WHITE));
        _playerTypes.put(PlayerType.Ally, new PlayerTypeInfo(Color.GREEN));
        _playerTypes.put(PlayerType.Enemy, new PlayerTypeInfo(Color.YELLOW));

        _playersExcludedFromLog = new ArrayList<String>();
        _playersExcludedFromLog.add("~BTLP SLOT");
    }

    public void save() {
        RadarConfigLoader.save(this, _configFile);
    }

    public boolean load() {
        return RadarConfigLoader.load(this, _configFile);
    }
}