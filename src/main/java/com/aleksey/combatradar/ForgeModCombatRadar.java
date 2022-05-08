package com.aleksey.combatradar;

import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.gui.screens.MainScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

/**
 * @author Aleksey Terzi
 */

@Mod("combatradar")
public class ForgeModCombatRadar {
    private static final Logger LOGGER = LogUtils.getLogger();

    private RadarConfig _config;
    private Radar _radar;

    public ForgeModCombatRadar()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        init();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();

        if(event.phase != TickEvent.Phase.START || minecraft.level == null)
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

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!_config.getEnabled()
                || event.getType() != RenderGameOverlayEvent.ElementType.ALL
                || minecraft.level == null
                || minecraft.options.hideGui
        ) {
            return;
        }

        _radar.render(event.getMatrixStack(), event.getPartialTicks());
    }

    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event) {
        if(!_config.getLogPlayerStatus())
            return;

        Component message = event.getMessage();
        if (message == null)
            return;

        TextColor color1 = message.getStyle().getColor();

        List<Component> siblings = message.getSiblings();
        TextColor color2 = siblings != null && siblings.size() > 1 ? siblings.get(1).getStyle().getColor() : null;

        TextColor yellow = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);

        if (yellow.equals(color1) || yellow.equals(color2)) {
            String messageText = message.getString();
            if (messageText.contains(" joined the game") || messageText.contains(" left the game")) {
                event.setCanceled(true);
            }
        }
    }

    private void init() {
        File gameDiretory = Minecraft.getInstance().gameDirectory;
        File configDir = new File(gameDiretory, "/combatradar/");
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

        ClientRegistry.registerKeyBinding(settingsKey);

        LOGGER.info("[CombatRadar]: mod enabled");
    }

    private static boolean isJourneyMapEnabled() {
        try {
            Class.forName("journeymap.common.Journeymap");
        } catch (ClassNotFoundException ex) {
            LOGGER.info("[CombatRadar]: JourneyMap is NOT found");
            return false;
        }

        LOGGER.info("[CombatRadar]: JourneyMap is found");

        return true;
    }

    private static boolean isVoxelMapEnabled() {
        try {
            Class.forName("com.mamiyaotaru.voxelmap.VoxelMap");
        } catch (ClassNotFoundException ex) {
            LOGGER.info("[CombatRadar]: VoxelMap is NOT found");
            return false;
        }

        LOGGER.info("[CombatRadar]: VoxelMap is found");

        return true;
    }
}