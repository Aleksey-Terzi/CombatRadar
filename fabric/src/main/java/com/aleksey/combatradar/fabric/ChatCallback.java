package com.aleksey.combatradar.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;

public interface ChatCallback {
    Event<ChatCallback> EVENT = EventFactory.createArrayBacked(ChatCallback.class,
            (listeners) -> (component) -> {
                for (ChatCallback listener : listeners) {
                    if (listener.interact(component))
                        return true;
                }

                return false;
            });

    boolean interact(Component component);
}
