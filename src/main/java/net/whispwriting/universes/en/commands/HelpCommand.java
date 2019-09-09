package net.whispwriting.universes.en.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "---------- " + ChatColor.YELLOW + "Universes Help " + ChatColor.GOLD + "----------");
        sender.sendMessage(ChatColor.GOLD + "/universehelp " + ChatColor.YELLOW + "shows the Universes help");
        if (sender.hasPermission("Universes.listworlds")){
            sender.sendMessage(ChatColor.GOLD + "/universelist " + ChatColor.YELLOW + "list available worlds");
        }
        if (sender.hasPermission("Universes.teleport")){
            sender.sendMessage(ChatColor.GOLD + "/universeteleport " + ChatColor.YELLOW + "teleport to other worlds");
        }
        if (sender.hasPermission("Universes.createworld")) {
            sender.sendMessage(ChatColor.GOLD + "/universecreate " + ChatColor.YELLOW + "create a new world");
        }
        if (sender.hasPermission("Universes.importworld")) {
            sender.sendMessage(ChatColor.GOLD + "/universeimport " + ChatColor.YELLOW + "import an existing world");
        }
        if (sender.hasPermission("Universes.unload")){
            sender.sendMessage(ChatColor.GOLD + "/universeunload " + ChatColor.YELLOW + "unload an existing world");
        }
        if (sender.hasPermission("Universes.delete")) {
            sender.sendMessage(ChatColor.GOLD + "/universedelete " + ChatColor.YELLOW + "delete an existing world");
        }
        if (sender.hasPermission("Universes.modify")){
            sender.sendMessage(ChatColor.GOLD + "/universemodify " + ChatColor.YELLOW + "modify settings in an existing world");
        }
        if (sender.hasPermission("Universes.override.gamemode") || sender.hasPermission("Universes.override.fullworld") || sender.hasPermission("Universes.override.flight")){
            sender.sendMessage(ChatColor.GOLD + "/universeoverride " + ChatColor.YELLOW + "enable or disable overrides");
        }
        if (sender.hasPermission("Universes.reload")){
            sender.sendMessage(ChatColor.GOLD + "/universesreload " + ChatColor.YELLOW + "reload the plugin config.");
        }
        return true;
    }
}
