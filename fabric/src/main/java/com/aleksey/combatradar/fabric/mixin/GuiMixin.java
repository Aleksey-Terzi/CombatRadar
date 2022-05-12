package com.aleksey.combatradar.fabric.mixin;

import com.aleksey.combatradar.fabric.ChatCallback;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(
            at = @At("HEAD"),
            method = "handleChat(Lnet/minecraft/network/chat/ChatType;Lnet/minecraft/network/chat/Component;Ljava/util/UUID;)V",
            cancellable = true
    )
    private void onHandleChat(ChatType chatType, Component component, UUID uUID, CallbackInfo callbackInfo) {
        if (ChatCallback.EVENT.invoker().interact(component))
            callbackInfo.cancel();
    }
}
