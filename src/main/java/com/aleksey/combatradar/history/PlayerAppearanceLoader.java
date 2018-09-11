package com.aleksey.combatradar.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Aleksey Terzi
 */
public class PlayerAppearanceLoader {
    private static class Info {
        public String savedAt;
        public Collection<PlayerAppearanceInfo> appearances;
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void save(Map<String, PlayerAppearanceInfo> appearances, File file) {
        Info info = new Info();
        info.savedAt = dateFormat.format(new Date());
        info.appearances = appearances.values();

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

    public static boolean load(Map<String, PlayerAppearanceInfo> appearances, File file) {
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

        appearances.clear();

        if(info.appearances != null) {
            for(PlayerAppearanceInfo appearance : info.appearances) {
                appearances.put(appearance.playerName.toLowerCase(), appearance);
            }
        }

        return true;
    }
}