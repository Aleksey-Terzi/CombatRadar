package com.aleksey.combatradar.config;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class PlayerTypeInfo {
    public Color color;
    public String soundEventName;
    public boolean ping;

    public PlayerTypeInfo(Color color) {
        this.color = color;
        this.soundEventName = "pling";
        this.ping = true;
    }
}
