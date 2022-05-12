package com.aleksey.combatradar.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksey Terzi
 */
public class RadarConfigLoader {
    private static class Info {
        public boolean enabled;
        public float radarOpacity;
        public int radarColor;
        public float radarSize;
        public int radarDistance;
        public float radarX;
        public float radarY;
        public float iconScale;
        public float fontScale;
        public int neutralPlayerColor;
        public int allyPlayerColor;
        public int enemyPlayerColor;
        public boolean neutralPlayerPing;
        public boolean allyPlayerPing;
        public boolean enemyPlayerPing;
        public String neutralSoundEventName;
        public String allySoundEventName;
        public String enemySoundEventName;
        public Boolean logPlayerStatus;
        public List<String> disabledEntities;
        public List<String> disabledGroups;
        public List<String> allyPlayers;
        public List<String> enemyPlayers;
        public List<String> playersExcludedFromLog;
    }

    public static void save(RadarConfig config, File file) {
        Info info = new Info();
        info.enabled = config.getEnabled();
        info.radarOpacity = config.getRadarOpacity();
        info.radarColor = config.getRadarColor().getRGB();
        info.radarSize = config.getRadarSize();
        info.radarDistance = config.getRadarDistance();
        info.radarX = config.getRadarX();
        info.radarY = config.getRadarY();
        info.iconScale = config.getIconScale();
        info.fontScale = config.getFontScale();

        PlayerTypeInfo neutralPlayer = config.getPlayerTypeInfo(PlayerType.Neutral);
        info.neutralPlayerColor = neutralPlayer.color.getRGB();
        info.neutralPlayerPing = neutralPlayer.ping;
        info.neutralSoundEventName = neutralPlayer.soundEventName;

        PlayerTypeInfo allyPlayer = config.getPlayerTypeInfo(PlayerType.Ally);
        info.allyPlayerColor = allyPlayer.color.getRGB();
        info.allyPlayerPing = allyPlayer.ping;
        info.allySoundEventName = allyPlayer.soundEventName;

        PlayerTypeInfo enemyPlayer = config.getPlayerTypeInfo(PlayerType.Enemy);
        info.enemyPlayerColor = enemyPlayer.color.getRGB();
        info.enemyPlayerPing = enemyPlayer.ping;
        info.enemySoundEventName = enemyPlayer.soundEventName;

        info.logPlayerStatus = config.getLogPlayerStatus();
        
        info.disabledEntities = new ArrayList<String>();
        info.disabledGroups = new ArrayList<String>();

        for(RadarEntityInfo entityInfo : config.getEntityList()) {
            if(!entityInfo.getEnabled())
                info.disabledEntities.add(entityInfo.getName());
        }

        if(!config.isGroupEnabled(GroupType.Neutral))
            info.disabledGroups.add("Neutral");
        if(!config.isGroupEnabled(GroupType.Aggressive))
            info.disabledGroups.add("Aggressive");
        if(!config.isGroupEnabled(GroupType.Other))
            info.disabledGroups.add("Other");

        info.allyPlayers = config.getPlayers(PlayerType.Ally);
        info.enemyPlayers = config.getPlayers(PlayerType.Enemy);

        info.playersExcludedFromLog = config.getPlayersExcludedFromLog();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(info);
        FileWriter writer = null;

        try {
            writer = new FileWriter(file);
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean load(RadarConfig config, File file) {
        Info info = null;
        FileReader reader = null;

        try {
            reader = new FileReader(file);
            info = new Gson().fromJson(reader, Info.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(info == null)
            return false;

        config.setEnabled(info.enabled);
        config.setRadarOpacity(info.radarOpacity);
        config.setRadarColor(new Color(info.radarColor));
        config.setRadarSize(info.radarSize);
        config.setRadarDistance(info.radarDistance);
        config.setRadarX(info.radarX);
        config.setRadarY(info.radarY);
        config.setIconScale(info.iconScale);
        config.setFontScale(info.fontScale);

        PlayerTypeInfo neutralInfo = config.getPlayerTypeInfo(PlayerType.Neutral);
        neutralInfo.color = new Color(info.neutralPlayerColor);
        neutralInfo.ping = info.neutralPlayerPing;
        neutralInfo.soundEventName = SoundInfo.getByValue(info.neutralSoundEventName) == null ? "pling" : info.neutralSoundEventName;

        PlayerTypeInfo allyInfo = config.getPlayerTypeInfo(PlayerType.Ally);
        allyInfo.color = new Color(info.allyPlayerColor);
        allyInfo.ping = info.allyPlayerPing;
        allyInfo.soundEventName = SoundInfo.getByValue(info.allySoundEventName) == null ? "pling" : info.allySoundEventName;

        PlayerTypeInfo enemyInfo = config.getPlayerTypeInfo(PlayerType.Enemy);
        enemyInfo.color = new Color(info.enemyPlayerColor);
        enemyInfo.ping = info.enemyPlayerPing;
        enemyInfo.soundEventName = SoundInfo.getByValue(info.enemySoundEventName) == null ? "pling" : info.enemySoundEventName;

        config.setLogPlayerStatus(info.logPlayerStatus == null || info.logPlayerStatus);

        if(info.disabledEntities != null) {
            for(String entityName : info.disabledEntities) {
                config.setEntityEnabled(entityName, false);
            }
        }

        if(info.disabledGroups != null) {
            for(String groupName : info.disabledGroups) {
                if(groupName.equalsIgnoreCase("Neutral"))
                    config.setGroupEnabled(GroupType.Neutral, false);
                else if(groupName.equalsIgnoreCase("Aggressive"))
                    config.setGroupEnabled(GroupType.Aggressive, false);
                else if(groupName.equalsIgnoreCase("Other"))
                    config.setGroupEnabled(GroupType.Other, false);
            }
        }

        if(info.allyPlayers != null) {
            for(String playerName : info.allyPlayers) {
                config.setPlayerType(playerName, PlayerType.Ally);
            }
        }

        if(info.enemyPlayers != null) {
            for(String playerName : info.enemyPlayers) {
                config.setPlayerType(playerName, PlayerType.Enemy);
            }
        }

        if(info.playersExcludedFromLog != null) {
            config.setPlayersExcludedFromLog(info.playersExcludedFromLog);
        }

        return true;
    }
}