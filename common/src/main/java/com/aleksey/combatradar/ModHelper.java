package com.aleksey.combatradar;

import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.gui.screens.MainScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

public class ModHelper {
    private Logger _logger;
    private RadarConfig _config;
    private Radar _radar;

    public RadarConfig init(Logger logger) {
        _logger = logger;

        File gameDirectory = Minecraft.getInstance().gameDirectory;
        File configDir = new File(gameDirectory, "/combatradar/");
        if(!configDir.isDirectory())
            configDir.mkdir();

        File configFile = new File(configDir, "config.json");
        KeyMapping settingsKey = new KeyMapping("Combat Radar Settings", GLFW.GLFW_KEY_R, "Combat Radar");

        _config = new RadarConfig(configFile, settingsKey);

        if(!configFile.isFile()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _config.save();
        } else {
            if(!_config.load())
                _config.save();
        }

        _config.setIsJourneyMapEnabled(isJourneyMapEnabled());
        _config.setIsVoxelMapEnabled(isVoxelMapEnabled());

        _radar = new Radar(_config);

        return _config;
    }

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();

        if(minecraft.level == null)
            return;

        if (_config.getEnabled()) {
            _radar.calcSettings();
            _radar.scanEntities();
            _radar.playSounds();
            _radar.sendMessages();
        }

        if (!minecraft.options.hideGui && minecraft.screen == null && _config.getSettingsKey().consumeClick()) {
            var windowId = minecraft.getWindow().getWindow();

            if(InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_LEFT_CONTROL)
                    || InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_RIGHT_CONTROL)
            ) {
                if(InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_LEFT_ALT)
                        || InputConstants.isKeyDown(windowId, GLFW.GLFW_KEY_RIGHT_ALT)
                ) {
                    _config.setEnabled(!_config.getEnabled());
                    _config.save();
                } else {
                    _config.revertNeutralAggressive();
                    _config.save();
                }
            } else {
                minecraft.setScreen(new MainScreen(minecraft.screen, _config));
            }
        }
    }

    public void render(PoseStack poseStack, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!_config.getEnabled()
                || minecraft.level == null
                || minecraft.options.hideGui
        ) {
            return;
        }

        _radar.render(poseStack, partialTicks);

    }

    public boolean processChat(Component message) {
        if(!_config.getLogPlayerStatus() || message == null)
            return false;

        TextColor color1 = message.getStyle().getColor();

        List<Component> siblings = message.getSiblings();
        TextColor color2 = siblings != null && siblings.size() > 1 ? siblings.get(1).getStyle().getColor() : null;

        TextColor yellow = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);

        if (yellow.equals(color1) || yellow.equals(color2)) {
            String messageText = message.getString();
            if (messageText.contains(" joined the game") || messageText.contains(" left the game"))
                return true;
        }

        return false;
    }

    private boolean isJourneyMapEnabled() {
        try {
            Class.forName("journeymap.common.Journeymap");
        } catch (ClassNotFoundException ex) {
            _logger.info("[CombatRadar]: JourneyMap is NOT found");
            return false;
        }

        _logger.info("[CombatRadar]: JourneyMap is found");

        return true;
    }

    private boolean isVoxelMapEnabled() {
        try {
            Class.forName("com.mamiyaotaru.voxelmap.VoxelMap");
        } catch (ClassNotFoundException ex) {
            _logger.info("[CombatRadar]: VoxelMap is NOT found");
            return false;
        }

        _logger.info("[CombatRadar]: VoxelMap is found");

        return true;
    }
}
