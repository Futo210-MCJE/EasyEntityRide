package com.example.easyentityride;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class InputHandler {

    private static KeyBinding rideKey;
    private static UUID selectedEntityUuid = null;
    private static boolean wasKeyPressed = false;

    public static void register() {
        rideKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.easyentityride.ride", // Translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R, // Default key
                "category.easyentityride.title" // Category translation key
        ));

        ClientTickEvents.END_CLIENT_TICK.register(InputHandler::onTick);
    }

    private static void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null)
            return;

        // Creative mode check
        if (!client.player.isCreative())
            return;

        if (rideKey.isPressed() && !wasKeyPressed) {
            handleKeyPress(client);
            wasKeyPressed = true;
        } else if (!rideKey.isPressed()) {
            wasKeyPressed = false;
        }
    }

    private static void handleKeyPress(MinecraftClient client) {
        HitResult hit = client.crosshairTarget;

        // 1. Deselect if sneaking and looking at air (or nothing helpful)
        if (client.player.isSneaking()) {
            if (hit == null || hit.getType() == HitResult.Type.MISS || hit.getType() == HitResult.Type.BLOCK) {
                clearSelection(client);
                return;
            }
        }

        // 2. Entity Interaction
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity target = entityHit.getEntity();

            // Prevent selecting self? (Usually not possible with crosshair but good safety)
            if (target == client.player)
                return;

            if (selectedEntityUuid == null) {
                // CASE: Nothing selected -> Select this entity (if not a minecart, optionally)
                if (target instanceof MinecartEntity) {
                    // Maybe allow selecting minecart? Spec says "Select Entity (not minecart)".
                    client.player.sendMessage(
                            Text.literal("Cannot select a Minecart directly.").formatted(Formatting.RED), true);
                    return;
                }
                selectEntity(client, target);
            } else {
                // CASE: Something is already selected
                if (target.getUuid().equals(selectedEntityUuid)) {
                    // Clicking the SAME entity -> Deselect
                    clearSelection(client);
                } else if (target instanceof MinecartEntity) {
                    // Clicking a Minecart with an entity selected -> MOUNT
                    mountEntityToMinecart(client, target);
                } else {
                    // Clicking a DIFFERENT entity -> Switch selection
                    selectEntity(client, target);
                }
            }
        }
    }

    private static void selectEntity(MinecraftClient client, Entity entity) {
        selectedEntityUuid = entity.getUuid();
        Text name = entity.getDisplayName();
        if (name == null)
            name = Text.literal("Entity");

        client.player.sendMessage(Text.literal("Selected: ").formatted(Formatting.GREEN).append(name), true);
    }

    private static void clearSelection(MinecraftClient client) {
        if (selectedEntityUuid != null) {
            selectedEntityUuid = null;
            client.player.sendMessage(Text.literal("Selection Cleared").formatted(Formatting.YELLOW), true);
        }
    }

    private static void mountEntityToMinecart(MinecraftClient client, Entity minecart) {
        if (selectedEntityUuid == null)
            return;

        // Find the actual entity object from UUID (need it to check name and vehicle
        // status)
        Entity selectedEntity = null;
        for (Entity e : client.world.getEntities()) {
            if (e.getUuid().equals(selectedEntityUuid)) {
                selectedEntity = e;
                break;
            }
        }

        // If entity is not loaded/found, we can't do the smart checks, so fallback to
        // just sending mount.
        // But if found:
        if (selectedEntity != null) {
            if (selectedEntity.hasVehicle()) {
                // Set flag to suppress the NEXT dismount message from vanilla
                EasyEntityRideClient.shouldSuppressDismount = true;

                // Dismount first
                client.player.networkHandler
                        .sendChatCommand(String.format("ride %s dismount", selectedEntityUuid.toString()));

                // Send custom styled message
                // [EasyEntityRide] (Gold) remounted (Green) [Name] (Yellow) to the minecart!
                // (Green)
                Text name = selectedEntity.getDisplayName();
                if (name == null)
                    name = Text.literal("Entity");

                Text styledMessage = Text.literal("")
                        .append(Text.literal("[EasyEntityRide] ").formatted(Formatting.GOLD))
                        .append(Text.literal("remounted ").formatted(Formatting.GREEN))
                        .append(Text.literal("").append(name).formatted(Formatting.YELLOW))
                        .append(Text.literal(" to the minecart!").formatted(Formatting.GREEN));

                client.player.sendMessage(styledMessage, false);
            } else {
                client.player.sendMessage(Text.literal("Entity Mounted!").formatted(Formatting.GREEN), true);
            }
        } else {
            // Fallback success message
            client.player.sendMessage(Text.literal("Entity Mounted!").formatted(Formatting.GREEN), true);
        }

        // Execute command: /ride <selectedUuid> mount <minecartUuid>
        String command = String.format("ride %s mount %s", selectedEntityUuid.toString(),
                minecart.getUuid().toString());

        // Client-side command execution (sending to server)
        client.player.networkHandler.sendChatCommand(command);

        // Reset state
        selectedEntityUuid = null;
    }

    public static void resetState() {
        selectedEntityUuid = null;
    }

    public static UUID getSelectedEntityUuid() {
        return selectedEntityUuid;
    }
}
