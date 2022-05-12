package com.aleksey.combatradar.fabric;

import com.aleksey.combatradar.ModHelper;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;

public class FabricModCombatRadar implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ModHelper _modHelper;

    @Override
    public void onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register(e -> init());
    }

    private void init() {
        _modHelper = new ModHelper();

        var config = _modHelper.init(LOGGER);

        KeyBindingHelper.registerKeyBinding(config.getSettingsKey());

        ClientTickEvents.START_CLIENT_TICK.register(e -> _modHelper.tick());
        HudRenderCallback.EVENT.register((poseStack, partialTicks) -> _modHelper.render(poseStack, partialTicks));
        ChatCallback.EVENT.register((component) -> _modHelper.processChat(component));

        LOGGER.info("[CombatRadar]: mod enabled");
    }
}
