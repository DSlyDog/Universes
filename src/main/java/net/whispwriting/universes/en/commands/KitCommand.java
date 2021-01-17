package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.events.KitsInventoryClick;
import net.whispwriting.universes.en.files.ConfigFile;
import net.whispwriting.universes.en.files.PerWorldInventoryGroupsFile;
import net.whispwriting.universes.en.guis.KitUI;
import net.whispwriting.universes.en.guis.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitCommand implements CommandExecutor {

    private Universes plugin;
    public KitCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.chat("&cOnly players may execute that command."));
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("Universes.kits")){
            player.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }
        ConfigFile config = new ConfigFile(plugin);
        boolean perGroupKits = config.get().getBoolean("per-world-kit-grouping");
        String worldName;
        boolean openUI = false;
        if (!perGroupKits) {
            worldName = player.getLocation().getWorld().getName();
            openUI = KitUI.init(worldName);
        }else{
            String world = player.getLocation().getWorld().getName();
            PerWorldInventoryGroupsFile groupFile = new PerWorldInventoryGroupsFile(plugin);
            worldName = groupFile.get().getString(world+".group");
            openUI = KitUI.init(worldName);
        }
        if (openUI){
            Inventory kitUI = KitUI.GUI(player);
            if (kitUI != null) {
                Bukkit.getPluginManager().registerEvents(new KitsInventoryClick(plugin, player.getUniqueId().toString()), plugin);
                player.openInventory(kitUI);
            }else{
                player.sendMessage(Utils.chat("&cThere was an error opening the kits UI. Please report this to a staff member and tell them to look over the logs and console."));
                plugin.log.warning(Utils.chat("[Universes] &cAttempt to open Kits UI in world, "+worldName+", failed. There is a kit listed that has not yet been created."));
            }
        }else{
            player.sendMessage(Utils.chat("&cThere was an error opening the kits UI. Please report this to a staff member and tell them to look over the logs and console."));
            plugin.log.warning(Utils.chat("[Universes] &cAttempt to open Kits UI in world, "+worldName+", failed. You have exceeded the kits capacity. You may not create more than 54 kits per world."));
        }
        return true;
    }
}
