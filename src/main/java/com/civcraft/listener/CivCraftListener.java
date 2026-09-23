package com.civcraft.listener;

import com.civcraft.CivCraftPlugin;
import com.civcraft.village.Village;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class CivCraftListener implements Listener {
    private final CivCraftPlugin plugin;
    private final NamespacedKey foundingStoneKey;

    public CivCraftListener(CivCraftPlugin plugin) {
        this.plugin = plugin;
        this.foundingStoneKey = new NamespacedKey(plugin, "founding_stone");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        boolean foundingStone = item.getType() == Material.LODESTONE
                && item.getItemMeta() != null
                && item.getItemMeta().getPersistentDataContainer().has(foundingStoneKey, PersistentDataType.BYTE);

        if (foundingStone) {
            if (plugin.getVillageStore().overlaps(event.getBlock().getLocation())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "This location is too close to another village.");
                return;
            }
            Village village = plugin.getVillageStore().create(
                    event.getPlayer().getName() + "'s Village", event.getPlayer(), event.getBlock().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Village founded: " + ChatColor.GOLD + village.getName());
            return;
        }

        Village village = villageAt(event.getBlock());
        if (village != null && !event.getPlayer().hasPermission("civcraft.admin")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot build inside " + village.getName() + "'s claim.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Village village = villageAt(event.getBlock());
        if (village != null && !event.getPlayer().hasPermission("civcraft.admin")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks inside " + village.getName() + "'s claim.");
        }
    }

    private Village villageAt(Block block) {
        return plugin.getVillageStore().at(block.getLocation());
    }
}
