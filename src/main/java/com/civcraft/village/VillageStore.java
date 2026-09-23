package com.civcraft.village;

import com.civcraft.CivCraftPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class VillageStore {
    private final CivCraftPlugin plugin;
    private final Map<UUID, Village> villages = new LinkedHashMap<>();
    private File file;

    public VillageStore(CivCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        villages.clear();
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create the Civ-Craft data folder.");
        }
        file = new File(plugin.getDataFolder(), "villages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("villages");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                String path = "villages." + key;
                Village village = new Village(
                        id,
                        section.getString(key + ".name", "Unnamed Village"),
                        parseUuid(section.getString(key + ".founder-uuid")),
                        section.getString(key + ".founder-name", "Unknown"),
                        section.getString(key + ".world", "world"),
                        section.getDouble(key + ".x"),
                        section.getDouble(key + ".y"),
                        section.getDouble(key + ".z"),
                        Instant.ofEpochMilli(section.getLong(key + ".created-at", System.currentTimeMillis())),
                        section.getInt(key + ".claim-radius-chunks", 1),
                        section.getInt(key + ".population", 8),
                        section.getInt(key + ".housing-capacity", 16),
                        section.getString(key + ".faction", null),
                        section.getBoolean(key + ".independent", false));
                villages.put(id, village);
            } catch (RuntimeException ex) {
                plugin.getLogger().warning("Skipping invalid village " + key + ": " + ex.getMessage());
            }
        }
    }

    private UUID parseUuid(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }

    public void save() {
        if (file == null) {
            return;
        }
        YamlConfiguration config = new YamlConfiguration();
        for (Village village : villages.values()) {
            String path = "villages." + village.getId();
            config.set(path + ".name", village.getName());
            config.set(path + ".founder-uuid", village.getFounderUuid() == null ? null : village.getFounderUuid().toString());
            config.set(path + ".founder-name", village.getFounderName());
            config.set(path + ".world", village.getWorldName());
            config.set(path + ".x", village.getX());
            config.set(path + ".y", village.getY());
            config.set(path + ".z", village.getZ());
            config.set(path + ".created-at", village.getCreatedAt().toEpochMilli());
            config.set(path + ".claim-radius-chunks", village.getClaimRadiusChunks());
            config.set(path + ".population", village.getPopulation());
            config.set(path + ".housing-capacity", village.getHousingCapacity());
            config.set(path + ".faction", village.getFactionName());
            config.set(path + ".independent", village.isIndependent());
        }
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save villages.yml: " + ex.getMessage());
        }
    }

    public Village create(String name, Player founder) {
        return create(name, founder, founder.getLocation());
    }

    public Village create(String name, Player founder, Location location) {
        Village village = new Village(
                UUID.randomUUID(), name, founder.getUniqueId(), founder.getName(),
                location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
                Instant.now(), 1, 8, 16, null, false);
        villages.put(village.getId(), village);
        save();
        return village;
    }

    public Village adoptIndependent(String name, Location location) {
        Village village = new Village(
                UUID.randomUUID(), name, null, "Independent villagers",
                location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
                Instant.now(), 1, 6, 12, null, true);
        villages.put(village.getId(), village);
        save();
        return village;
    }

    public boolean overlaps(Location location) {
        for (Village village : villages.values()) {
            if (village.distanceSquared(location.getWorld().getName(), location.getX(), location.getZ()) < 48 * 48) {
                return true;
            }
        }
        return false;
    }

    public void simulatePopulation() {
        boolean changed = false;
        for (Village village : villages.values()) {
            int before = village.getPopulation();
            village.growPopulation();
            changed |= before != village.getPopulation();
        }
        if (changed) {
            save();
        }
    }

    public Collection<Village> getVillages() {
        return villages.values();
    }

    public Village nearest(Location location) {
        Village nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Village village : villages.values()) {
            double distance = village.distanceSquared(location.getWorld().getName(), location.getX(), location.getZ());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = village;
            }
        }
        return nearest;
    }

    public Village at(Location location) {
        for (Village village : villages.values()) {
            if (village.contains(location)) {
                return village;
            }
        }
        return null;
    }
}
