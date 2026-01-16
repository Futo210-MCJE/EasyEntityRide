package com.example.easyentityride;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class EasyEntityRideClient implements ClientModInitializer {

    // Flag for conditional log suppression
    public static boolean shouldSuppressDismount = false;

    @Override
    public void onInitializeClient() {
        // Load Config
        ConfigHandler.load();

        // Register Input Handler (KeyBinding, Selection Logic)
        InputHandler.register();

        // Register Render Handler (Particles, Action Bar)
        RenderHandler.register();

        // Register Event to clear state on disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            InputHandler.resetState();
            shouldSuppressDismount = false;
        });
    }
}
