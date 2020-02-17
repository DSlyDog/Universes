package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.en.files.SpawnFile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FirstJoinSpawnCommand implements CommandExecutor {

    private SpawnFile spawnFile;

    public FirstJoinSpawnCommand(SpawnFile spawnFile){
        this.spawnFile = spawnFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Only a player may use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("Universes.usetspawn")){
            player.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }

        Location loc = player.getLocation();
        spawnFile.get().set("x", loc.getX());
        spawnFile.get().set("y", loc.getY());
        spawnFile.get().set("z", loc.getZ());
        spawnFile.get().set("pitch", loc.getPitch());
        spawnFile.get().set("yaw", loc.getYaw());
        spawnFile.get().set("world", loc.getWorld().getName());
        spawnFile.save();
        player.sendMessage(ChatColor.GREEN + "First join spawn has been set.");
        return true;
    }
}
