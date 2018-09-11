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
        public List<String> disabledEntities;
        public List<String> disabledGroups;
        public List<String> allyPlayers;
        public List<String> enemyPlayers;
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
        info.neutralPlayerColor = config.getPlayerTypeInfo(PlayerType.Neutral).color.getRGB();
        info.allyPlayerColor = config.getPlayerTypeInfo(PlayerType.Ally).color.getRGB();
        info.enemyPlayerColor = config.getPlayerTypeInfo(PlayerType.Enemy).color.getRGB();
        info.neutralPlayerPing = config.getPlayerTypeInfo(PlayerType.Neutral).ping;
        info.allyPlayerPing = config.getPlayerTypeInfo(PlayerType.Ally).ping;
        info.enemyPlayerPing = config.getPlayerTypeInfo(PlayerType.Enemy).ping;
        info.disabledEntities = new ArrayList<String>();
        info.disabledGroups = new ArrayList<String>();
        info.allyPlayers = new ArrayList<String>();
        info.enemyPlayers = new ArrayList<String>();

        for(RadarEntityInfo entityInfo : config.getEntities()) {
            if(!entityInfo.getEnabled())
                info.disabledEntities.add(entityInfo.getName());
        }

        if(!config.isGroupEnabled(GroupType.Neutral))
            info.disabledGroups.add("Neutral");
        if(!config.isGroupEnabled(GroupType.Aggressive))
            info.disabledGroups.add("Aggressive");
        if(!config.isGroupEnabled(GroupType.Other))
            info.disabledGroups.add("Other");

        for(String playerName : config.getPlayers(PlayerType.Ally)) {
            info.allyPlayers.add(playerName);
        }

        for(String playerName : config.getPlayers(PlayerType.Enemy)) {
            info.enemyPlayers.add(playerName);
        }

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

        PlayerTypeInfo allyInfo = config.getPlayerTypeInfo(PlayerType.Ally);
        allyInfo.color = new Color(info.allyPlayerColor);
        allyInfo.ping = info.allyPlayerPing;

        PlayerTypeInfo enemyInfo = config.getPlayerTypeInfo(PlayerType.Enemy);
        enemyInfo.color = new Color(info.enemyPlayerColor);
        enemyInfo.ping = info.enemyPlayerPing;

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

        return true;
    }
}