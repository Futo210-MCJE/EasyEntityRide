package com.example.easyentityride;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(),
            "EasyEntityRide.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigData data = new ConfigData();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                data = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save(); // Create default
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getParticleName() {
        return data.particleName;
    }

    public static void setParticleName(String name) {
        data.particleName = name;
    }

    public static ParticleEffect getParticleType() {
        String name = data.particleName;
        // Default fallback
        if (name == null || name.isEmpty()) {
            return ParticleTypes.HAPPY_VILLAGER;
        }

        // Fix logic: Parse string (e.g. "minecraft:flame" or "flame") to ParticleType
        Identifier id = Identifier.tryParse(name);
        if (id == null)
            return ParticleTypes.HAPPY_VILLAGER;

        // If namespace missing, default to minecraft
        if (id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && !name.contains(":")) {
            id = Identifier.of("minecraft", name);
        }

        if (Registries.PARTICLE_TYPE.containsId(id)) {
            ParticleType<?> type = Registries.PARTICLE_TYPE.get(id);
            // Verify it is a simple particle type (complex ones need data, safe to cast for
            // simple ones usually?
            // Actually most simple particles are DefaultParticleType which implements
            // ParticleEffect)
            if (type instanceof ParticleEffect) {
                return (ParticleEffect) type;
            }
        }

        return ParticleTypes.HAPPY_VILLAGER; // Fallback
    }

    public static boolean isValidParticle(String name) {
        if (name == null || name.isEmpty())
            return false;
        Identifier id = Identifier.tryParse(name);
        if (id == null)
            return false;
        if (id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && !name.contains(":")) {
            id = Identifier.of("minecraft", name);
        }
        return Registries.PARTICLE_TYPE.containsId(id);
    }

    public static class ConfigData {
        public String particleName = "happy_villager";
    }
}
