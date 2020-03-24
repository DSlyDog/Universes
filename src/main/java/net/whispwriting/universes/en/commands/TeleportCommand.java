package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportCommand implements CommandExecutor {

    private Universes plugin;

    public TeleportCommand(Universes plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("Universes.teleport")) {
                if (args.length == 1) {
                    WorldListFile worldList = new WorldListFile(plugin);
                    List<String> worlds = worldList.get().getStringList("worlds");
                    if (worlds.contains(args[0])) {
                        World world = Bukkit.getWorld(args[0]);
                        PlayerInventoryFile playerInventoryFile = new PlayerInventoryFile(plugin, player.getUniqueId().toString(), args[0]);
                        double x = playerInventoryFile.get().getDouble("previous.x");
                        double y = playerInventoryFile.get().getDouble("previous.y");
                        double z = playerInventoryFile.get().getDouble("previous.z");
                        double pitch = playerInventoryFile.get().getDouble("previous.pitch");
                        double yaw = playerInventoryFile.get().getDouble("previous.yaw");
                        Location loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
                        if (x == 0 && y == 0 && z == 0){
                            x = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.x");
                            y = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.y");
                            z = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.z");
                            loc = new Location(world, x, y, z);
                        }
                        Location previous = player.getLocation();
                        String fromWorld = previous.getWorld().getName();
                        playerInventoryFile = new PlayerInventoryFile(plugin, player.getUniqueId().toString(), fromWorld);
                        playerInventoryFile.get().set("previous.x", previous.getX());
                        playerInventoryFile.get().set("previous.y", previous.getY());
                        playerInventoryFile.get().set("previous.z", previous.getZ());
                        playerInventoryFile.get().set("previous.pitch", previous.getPitch());
                        playerInventoryFile.get().set("previous.yaw", previous.getYaw());
                        playerInventoryFile.save();
                        player.teleport(loc);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Could not find a world by that name.");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD + "/universeteleport " + ChatColor.YELLOW + "<world>");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Only players may use that command.");
            return true;
        }
    }
}
