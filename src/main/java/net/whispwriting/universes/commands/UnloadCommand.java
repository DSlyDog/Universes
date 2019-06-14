package net.whispwriting.universes.commands;

import net.whispwriting.universes.files.WorldListFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UnloadCommand implements CommandExecutor {

    private WorldListFile worldList;

    public UnloadCommand(WorldListFile wl){
        worldList = wl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.unload")){
            sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }
        if (args.length != 1){
            sender.sendMessage(ChatColor.GOLD + "/universeunload " + ChatColor.YELLOW + "<world name>");
            return true;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world == null){
            sender.sendMessage(ChatColor.RED + "No world could be found by that name.");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Started unloading world.");
        List<String> worlds = worldList.get().getStringList("worlds");
        worlds.remove(args[0]);
        worldList.get().set("worlds", worlds);
        worldList.save();
        worldList.reload();
        Bukkit.unloadWorld(world, true);
        sender.sendMessage(ChatColor.GREEN + "Unload complete.");
        return true;
    }
}
