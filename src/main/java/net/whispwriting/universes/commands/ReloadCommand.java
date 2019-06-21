package net.whispwriting.universes.commands;

import net.whispwriting.universes.Universes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private Universes plugin;
    public ReloadCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.worlds.reload();
        plugin.worldListFile.reload();
        sender.sendMessage(ChatColor.GREEN + "reloaded config.");
        return true;
    }
}
