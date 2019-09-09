package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.es.files.WorldListFile;
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
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }
        if (args.length != 1){
            sender.sendMessage(ChatColor.GOLD + "/universeunload " + ChatColor.YELLOW + "<nombre del mundo>");
            return true;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world == null){
            sender.sendMessage(ChatColor.RED + "No se ha podido encontrar ningún mundo con ese nombre.");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Descarga de mundo iniciada.");
        List<String> worlds = worldList.get().getStringList("worlds");
        worlds.remove(args[0]);
        worldList.get().set("worlds", worlds);
        worldList.save();
        worldList.reload();
        Bukkit.unloadWorld(world, true);
        sender.sendMessage(ChatColor.GREEN + "Descarga completada");
        return true;
    }
}
