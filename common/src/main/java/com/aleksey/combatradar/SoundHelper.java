package com.aleksey.combatradar;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Random;
import java.util.UUID;

/**
 * @author Aleksey Terzi
 */
public class SoundHelper {
    public static void playSound(String soundEventName, UUID playerKey) {
        if("none".equalsIgnoreCase(soundEventName))
            return;

        Minecraft minecraft = Minecraft.getInstance();
        float playerPitch = .5f + 1.5f * new Random(playerKey.hashCode()).nextFloat();

        minecraft.player.playSound(new SoundEvent(new ResourceLocation("block.note_block." + soundEventName)), 1, playerPitch);
    }
}
