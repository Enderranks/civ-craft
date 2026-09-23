package com.civcraft.village;

import java.time.Instant;
import java.util.UUID;

public final class Village {
    private final UUID id;
    private final String name;
    private final UUID founderUuid;
    private final String founderName;
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final Instant createdAt;
    private final int claimRadiusChunks;
    private int population;
    private int housingCapacity;
    private String factionName;
    private final boolean independent;

    public Village(UUID id, String name, UUID founderUuid, String founderName, String worldName,
                   double x, double y, double z, Instant createdAt, int claimRadiusChunks, int population,
                   int housingCapacity, String factionName, boolean independent) {
        this.id = id;
        this.name = name;
        this.founderUuid = founderUuid;
        this.founderName = founderName;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createdAt = createdAt;
        this.claimRadiusChunks = claimRadiusChunks;
        this.population = population;
        this.housingCapacity = housingCapacity;
        this.factionName = factionName;
        this.independent = independent;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public UUID getFounderUuid() { return founderUuid; }
    public String getFounderName() { return founderName; }
    public String getWorldName() { return worldName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public Instant getCreatedAt() { return createdAt; }
    public int getClaimRadiusChunks() { return claimRadiusChunks; }
    public int getPopulation() { return population; }
    public int getHousingCapacity() { return housingCapacity; }
    public String getFactionName() { return factionName; }
    public boolean isIndependent() { return independent; }

    public void growPopulation() {
        if (population < housingCapacity) {
            population++;
        }
    }

    public void setFactionName(String factionName) { this.factionName = factionName; }

    public boolean contains(org.bukkit.Location location) {
        if (location.getWorld() == null || !worldName.equals(location.getWorld().getName())) {
            return false;
        }
        int centerChunkX = ((int) Math.floor(x)) >> 4;
        int centerChunkZ = ((int) Math.floor(z)) >> 4;
        return Math.abs(location.getChunk().getX() - centerChunkX) <= claimRadiusChunks
                && Math.abs(location.getChunk().getZ() - centerChunkZ) <= claimRadiusChunks;
    }

    public double distanceSquared(String worldName, double x, double z) {
        if (!this.worldName.equals(worldName)) {
            return Double.POSITIVE_INFINITY;
        }
        double dx = this.x - x;
        double dz = this.z - z;
        return dx * dx + dz * dz;
    }
}
