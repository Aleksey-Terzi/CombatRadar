package com.aleksey.combatradar;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.Random;
import java.util.UUID;

/**
 * @author Aleksey Terzi
 */
public class SoundHelper {
    public static void playSound(Minecraft minecraft, String soundEventName, UUID playerKey) {
        if("none".equalsIgnoreCase(soundEventName)) {
            return;
        }

        float playerPitch = .5f + 1.5f * new Random(playerKey.hashCode()).nextFloat();

        minecraft.player.playSound(new SoundEvent(new ResourceLocation("block.note." + soundEventName)), 1, playerPitch);
    }
}
