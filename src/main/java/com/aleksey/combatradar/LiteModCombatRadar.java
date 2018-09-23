package com.aleksey.combatradar;

import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.gui.GuiLocationAndColorScreen;
import com.aleksey.combatradar.gui.GuiMainScreen;
import com.aleksey.combatradar.gui.GuiPlayerSettingsScreen;
import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;

/**
 * @author Aleksey Terzi
 */
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="combatradar.json")
public class LiteModCombatRadar implements Tickable, ChatFilter
{
    private RadarConfig _config;
    private Radar _radar;

    @Override
    public String getName()
    {
        return "Combat Radar";
    }
    
    @Override
    public String getVersion()
    {
        return "1.1.0";
    }
    
    @Override
    public void init(File configPath)
    {
        File configDir = new File(LiteLoader.getGameDirectory(), "/combatradar/");
        if(!configDir.isDirectory()) {
            configDir.mkdir();
        }

        File configFile = new File(configDir, "config.json");
        KeyBinding settingsKey = new KeyBinding("Combat Radar Settings", Keyboard.KEY_R, "Combat Radar");

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

        LiteLoader.getInput().registerKeyBinding(settingsKey);

        LiteLoaderLogger.info("[CombatRadar]: mod enabled");
    }

    private static boolean isJourneyMapEnabled() {
        try {
            Class.forName("journeymap.common.Journeymap");
        } catch (ClassNotFoundException ex) {
            LiteLoaderLogger.info("[CombatRadar]: JourneyMap is NOT found");
            return false;
        }

        LiteLoaderLogger.info("[CombatRadar]: JourneyMap is found");

        return true;
    }

    private static boolean isVoxelMapEnabled() {
        try {
            Class.forName("com.mamiyaotaru.voxelmap.litemod.LiteModVoxelMap");
        } catch (ClassNotFoundException ex) {
            LiteLoaderLogger.info("[CombatRadar]: VoxelMap is NOT found");
            return false;
        }

        LiteLoaderLogger.info("[CombatRadar]: VoxelMap is found");

        return true;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }
    
    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
    {
        if (!inGame) {
            return;
        }

        if(clock) {
            _radar.calcSettings(minecraft);
            _radar.scanEntities(minecraft);
            _radar.playSounds(minecraft);
            _radar.sendMessages(minecraft);
        }

        if (!Minecraft.isGuiEnabled()
                || minecraft.currentScreen != null
                    && !(minecraft.currentScreen instanceof GuiChat)
                    && !(minecraft.currentScreen instanceof GuiMainScreen)
                    && !(minecraft.currentScreen instanceof GuiLocationAndColorScreen)
                    && !(minecraft.currentScreen instanceof GuiPlayerSettingsScreen)
                )
        {
            return;
        }

        if (minecraft.currentScreen == null && _config.getSettingsKey().isPressed()) {
            if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                if(Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
                    _config.setEnabled(!_config.getEnabled());
                    _config.save();
                } else {
                    _config.revertNeutralAggressive();
                    _config.save();
                }
            } else {
                minecraft.displayGuiScreen(new GuiMainScreen(minecraft.currentScreen, _config));
            }
        }

        _radar.render(minecraft);
    }

    @Override
    public boolean onChat(ITextComponent chat, String message, LiteLoaderEventBroker.ReturnValue<ITextComponent> newMessage) {
        if(!_config.getLogPlayerStatus()) {
            return true;
        }

        if(chat != null && message != null) {
            List<ITextComponent> siblings = chat.getSiblings();
            TextFormatting color = siblings != null && siblings.size() > 0 ? siblings.get(0).getStyle().getColor() : null;

            if (color == TextFormatting.YELLOW) {
                if (message.contains(" joined the game") || message.contains(" left the game")) {
                    return false;
                }
            }
        }

        return true;
    }
}