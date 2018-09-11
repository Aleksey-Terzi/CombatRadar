package com.aleksey.combatradar.history;

import com.aleksey.combatradar.config.RadarEntityInfo;

import java.util.Comparator;

/**
 * @author Aleksey Terzi
 */
public class PlayerAppearanceInfo {
    public static class PlayerAppearanceComparator implements Comparator<PlayerAppearanceInfo> {
        @Override
        public int compare(PlayerAppearanceInfo i1, PlayerAppearanceInfo i2) {
            return Long.compare(i2.time, i1.time);
        }
    }

    public long time;
    public String playerName;
    public int x;
    public int y;
    public int z;
}
