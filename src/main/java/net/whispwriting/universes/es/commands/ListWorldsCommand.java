package net.whispwriting.universes.es.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class ListWorldsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("Universes.listworlds")){
            Collection worldCollection = Bukkit.getWorlds();
            String worldListString = "";
            int count = 0;
            for (Object o : worldCollection){
                World world = (World) o;
                String name = world.getName();
                if (count == worldCollection.size()-1){
                    worldListString += name;
                }else{
                    worldListString += name + ", ";
                }
                count++;
            }

            sender.sendMessage(ChatColor.GREEN + "Mundos:");
            sender.sendMessage(worldListString);
            return true;
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }
    }
}
