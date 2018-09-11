package com.aleksey.combatradar;

import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.gui.GuiLocationAndColorScreen;
import com.aleksey.combatradar.gui.GuiMainScreen;
import com.aleksey.combatradar.gui.GuiPlayerSettingsScreen;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * @author Aleksey Terzi
 */
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="combatradar.json")
public class LiteModCombatRadar implements Tickable
{
    private static final int SCAN_TICKS = 10;

    private RadarConfig _config;
    private Radar _radar;
    private int _ticks = 0;

    public LiteModCombatRadar()
    {
    }
    
    @Override
    public String getName()
    {
        return "Combat Radar";
    }
    
    @Override
    public String getVersion()
    {
        return "1.0.0";
    }
    
    @Override
    public void init(File configPath)
    {
        File configDir = new File(LiteLoader.getGameDirectory(), "/combatradar/");
        if(!configDir.isDirectory()) {
            configDir.mkdir();
        }

        File configFile = new File(configDir, "config.json");
        File playerAppearanceFile = new File(configDir, "player_appearance.json");
        KeyBinding settingsKey = new KeyBinding("Combat Radar Settings", Keyboard.KEY_R, "Combat Radar");

        _config = new RadarConfig(configFile, playerAppearanceFile, settingsKey);

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

        _radar = new Radar(_config);

        LiteLoader.getInput().registerKeyBinding(settingsKey);
    }
    
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }
    
    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
    {
        if (!inGame
                || !Minecraft.isGuiEnabled()
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

            return;
        }

        _radar.calcSettings(minecraft);

        if(_ticks == 0) {
            _radar.scanEntities(minecraft);
        }

        if(_ticks >= SCAN_TICKS)
            _ticks = 0;
        else
            _ticks++;

        _radar.render(minecraft);
    }
}