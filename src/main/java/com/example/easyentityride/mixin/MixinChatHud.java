package com.example.easyentityride.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator,
            CallbackInfo ci) {
        if (message.getContent() instanceof TranslatableTextContent translatable) {
            String key = translatable.getKey();

            // Always suppress Vanilla "Mounted" message
            if ("commands.ride.mount.success".equals(key)) {
                ci.cancel();
                return;
            }

            // Conditionally suppress Vanilla "Dismounted" message
            if ("commands.ride.dismount.success".equals(key)
                    && com.example.easyentityride.EasyEntityRideClient.shouldSuppressDismount) {
                ci.cancel();
                // We consume the flag here (or we could let InputHandler logic handle timing,
                // but consuming here is safer to avoid suppressing legit messages later)
                // However, there might be multiple messages? Usually just one.
                com.example.easyentityride.EasyEntityRideClient.shouldSuppressDismount = false;
            }
        }
    }
}
