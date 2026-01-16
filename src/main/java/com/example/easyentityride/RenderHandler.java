package com.example.easyentityride;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import java.util.UUID;

public class RenderHandler {

    public static void register() {
        // Using ClientTick meant logic runs 20 times a second.
        // For particles, this might be infrequent enough to not spam,
        // but frequent enough to look like a persistent effect.
        // If we want smoother particles, we could use ClientTickEvents.END_WORLD_TICK
        // or similar.
        ClientTickEvents.END_CLIENT_TICK.register(RenderHandler::onTick);
    }

    private static void onTick(MinecraftClient client) {
        if (client.world == null || client.player == null)
            return;

        // Creative mode check - strictly required for Mod functionality,
        // implies visuals also only appear in creative.
        if (!client.player.isCreative())
            return;

        UUID targetUuid = InputHandler.getSelectedEntityUuid();
        if (targetUuid == null)
            return;

        // Find entity by UUID
        // Looping through entities can be expensive if many entities, but usually fine
        // for client side.
        // client.world.getEntities() gives us an iterable.
        Entity targetEntity = null;
        for (Entity e : client.world.getEntities()) {
            if (e.getUuid().equals(targetUuid)) {
                targetEntity = e;
                break;
            }
        }

        if (targetEntity != null) {
            if (!targetEntity.isAlive()) {
                // If entity died or is gone, maybe we should clear selection?
                // But InputHandler handles specific interactions.
                // For now, just don't render.
                return;
            }

            // Spawn particles above head
            // HAPPY_VILLAGER is the green sparkles
            double x = targetEntity.getX();
            double y = targetEntity.getY() + targetEntity.getHeight() + 0.5; // Above head
            double z = targetEntity.getZ();

            // Spawn a random offset to make it look like a cloud/halo
            double offsetX = (client.world.random.nextDouble() - 0.5) * targetEntity.getWidth();
            double offsetZ = (client.world.random.nextDouble() - 0.5) * targetEntity.getWidth();

            client.world.addParticle(
                    ConfigHandler.getParticleType(),
                    x + offsetX,
                    y,
                    z + offsetZ,
                    0.0, 0.0, 0.0 // Velocity
            );
        }
    }
}
