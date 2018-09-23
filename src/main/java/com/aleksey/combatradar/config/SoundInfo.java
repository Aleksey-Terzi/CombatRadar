package com.aleksey.combatradar.config;

/**
 * @author Aleksey Terzi
 */
public class SoundInfo {
    public String name;
    public String value;

    public SoundInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static final SoundInfo[] SOUND_LIST = new SoundInfo[]{
            new SoundInfo("None", "none"),
            new SoundInfo("Bass", "bass"),
            new SoundInfo("Snare", "snare"),
            new SoundInfo("Hat", "hat"),
            new SoundInfo("Base drum", "basedrum"),
            new SoundInfo("Bell", "bell"),
            new SoundInfo("Flute", "flute"),
            new SoundInfo("Chime", "chime"),
            new SoundInfo("Guitar", "guitar"),
            new SoundInfo("Xylophone", "xylophone"),
            new SoundInfo("Harp", "harp"),
            new SoundInfo("Pling", "pling"),
    };

    public static SoundInfo getByValue(String value) {
        for(SoundInfo soundInfo : SOUND_LIST) {
            if(soundInfo.value.equalsIgnoreCase(value)) {
                return soundInfo;
            }
        }

        return null;
    }
}
