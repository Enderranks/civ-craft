package com.civcraft;

import com.civcraft.command.CivCraftCommand;
import com.civcraft.village.VillageStore;
import com.civcraft.listener.CivCraftListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CivCraftPlugin extends JavaPlugin {
    private VillageStore villageStore;

    @Override
    public void onEnable() {
        villageStore = new VillageStore(this);
        villageStore.load();
        getServer().getPluginManager().registerEvents(new CivCraftListener(this), this);
        getServer().getScheduler().runTaskTimer(this, villageStore::simulatePopulation, 20L * 60L * 5L, 20L * 60L * 5L);

        PluginCommand command = getCommand("civcraft");
        if (command == null) {
            getLogger().severe("The civcraft command is missing from paper-plugin.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CivCraftCommand civCraftCommand = new CivCraftCommand(this);
        command.setExecutor(civCraftCommand);
        command.setTabCompleter(civCraftCommand);
        getLogger().info("Civ-Craft is ready. Loaded " + villageStore.getVillages().size() + " villages.");
    }

    @Override
    public void onDisable() {
        if (villageStore != null) {
            villageStore.save();
        }
    }

    public VillageStore getVillageStore() {
        return villageStore;
    }
}
