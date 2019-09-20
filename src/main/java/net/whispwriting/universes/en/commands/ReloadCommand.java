package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReloadCommand implements CommandExecutor {

    private Universes plugin;
    public ReloadCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.reload")){
            sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }
        plugin.worlds.reload();
        plugin.worldListFile.reload();
        sender.sendMessage(ChatColor.GREEN + "reloaded config.");
        if (sender instanceof Player){
            Player player = (Player) sender;
            player.getInventory().addItem(new ItemStack(Material.END_PORTAL, 1));
            player.updateInventory();
        }
        return true;
    }
}
