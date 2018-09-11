package com.aleksey.combatradar.config;

import java.awt.*;

/**
 * @author Aleksey Terzi
 */
public class PlayerTypeInfo {
    public Color color;
    public boolean ping;

    public PlayerTypeInfo(Color color) {
        this.color = color;
        this.ping = true;
    }
}
