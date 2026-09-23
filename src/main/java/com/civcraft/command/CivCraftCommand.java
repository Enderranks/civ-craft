package com.civcraft.command;

import com.civcraft.CivCraftPlugin;
import com.civcraft.village.Village;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CivCraftCommand implements CommandExecutor, TabCompleter {
    private final CivCraftPlugin plugin;

    public CivCraftCommand(CivCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Civ-Craft: /civcraft village create <name>, give <player>, adopt <name>, info, list");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("civcraft.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to reload Civ-Craft.");
                return true;
            }
            plugin.getVillageStore().load();
            sender.sendMessage(ChatColor.GREEN + "Civ-Craft data reloaded.");
            return true;
        }

        if (!args[0].equalsIgnoreCase("village")) {
            sender.sendMessage(ChatColor.RED + "Unknown command. Try /civcraft village.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.GOLD + "Village commands: create <name>, give <player>, adopt <name>, info, list");
            return true;
        }

        if (args[1].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can found a village.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /civcraft village create <name>");
                return true;
            }
            String name = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)).trim();
            if (!name.matches("[A-Za-z0-9][A-Za-z0-9 _-]{1,31}")) {
                sender.sendMessage(ChatColor.RED + "Village names must be 2–32 characters and use letters, numbers, spaces, _ or -.");
                return true;
            }
            if (plugin.getVillageStore().overlaps(player.getLocation())) {
                sender.sendMessage(ChatColor.RED + "This location is too close to an existing village.");
                return true;
            }
            Village village = plugin.getVillageStore().create(name, player);
            sender.sendMessage(ChatColor.GREEN + "Village founded: " + ChatColor.GOLD + village.getName());
            sender.sendMessage(ChatColor.GRAY + "Starting claim: 3x3 chunks | Population: 8");
            return true;
        }

        if (args[1].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("civcraft.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to give founding stones.");
                return true;
            }
            Player target = args.length >= 3 ? plugin.getServer().getPlayerExact(args[2])
                    : sender instanceof Player player ? player : null;
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Usage: /civcraft village give <player>");
                return true;
            }
            ItemStack stone = new ItemStack(Material.LODESTONE);
            ItemMeta meta = stone.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Village Founding Stone");
            meta.setLore(List.of(ChatColor.GRAY + "Place this to found a small village."));
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "founding_stone"), PersistentDataType.BYTE, (byte) 1);
            stone.setItemMeta(meta);
            target.getInventory().addItem(stone);
            sender.sendMessage(ChatColor.GREEN + "Gave a Village Founding Stone to " + target.getName() + ".");
            return true;
        }

        if (args[1].equalsIgnoreCase("adopt")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can adopt a village.");
                return true;
            }
            if (plugin.getVillageStore().overlaps(player.getLocation())) {
                sender.sendMessage(ChatColor.RED + "This location is too close to an existing village.");
                return true;
            }
            String name = args.length >= 3 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "Independent Village";
            Village village = plugin.getVillageStore().adoptIndependent(name, player.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Registered " + ChatColor.GOLD + village.getName() + ChatColor.GREEN + " as an independent village.");
            return true;
        }

        if (args[1].equalsIgnoreCase("info")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use village info without a village name.");
                return true;
            }
            Village village = plugin.getVillageStore().nearest(player.getLocation());
            if (village == null) {
                sender.sendMessage(ChatColor.YELLOW + "No villages have been founded yet.");
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + village.getName() + ChatColor.GRAY + " — founded by " + village.getFounderName());
            String affiliation = village.isIndependent() ? "independent" : (village.getFactionName() == null ? "unaffiliated" : village.getFactionName());
            sender.sendMessage(ChatColor.GRAY + "Population: " + village.getPopulation() + "/" + village.getHousingCapacity() + " | Claim: " + village.getClaimRadiusChunks() + " chunk radius");
            sender.sendMessage(ChatColor.GRAY + "Affiliation: " + affiliation);
            sender.sendMessage(ChatColor.GRAY + "Location: " + village.getWorldName() + " " + Math.round(village.getX()) + ", " + Math.round(village.getY()) + ", " + Math.round(village.getZ()));
            return true;
        }

        if (args[1].equalsIgnoreCase("list")) {
            if (plugin.getVillageStore().getVillages().isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "No villages have been founded yet.");
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "Civ-Craft villages:");
            for (Village village : plugin.getVillageStore().getVillages()) {
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.WHITE + village.getName() + ChatColor.GRAY + " (population " + village.getPopulation() + ")");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown village command. Try /civcraft village.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return partial(args[0], List.of("village", "reload"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("village")) {
            return partial(args[1], List.of("create", "give", "adopt", "info", "list"));
        }
        return Collections.emptyList();
    }

    private List<String> partial(String input, List<String> options) {
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.startsWith(input.toLowerCase())) {
                matches.add(option);
            }
        }
        return matches;
    }
}
