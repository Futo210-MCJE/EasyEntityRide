package com.example.easyentityride;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class EasyEntityRideClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register Input Handler (KeyBinding, Selection Logic)
        InputHandler.register();

        // Register Render Handler (Particles, Action Bar)
        RenderHandler.register();

        // Register Event to clear state on disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            InputHandler.resetState();
        });
    }
}
