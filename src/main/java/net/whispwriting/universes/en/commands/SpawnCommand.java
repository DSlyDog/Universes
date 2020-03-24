package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.PlayerInventoryFile;
import net.whispwriting.universes.en.files.WorldListFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand implements CommandExecutor {

    private Universes plugin;

    public SpawnCommand(Universes plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("Universes.spawn")) {
                if (args.length == 1) {
                    WorldListFile worldList = new WorldListFile(plugin);
                    List<String> worlds = worldList.get().getStringList("worlds");
                    if (worlds.contains(args[0])) {
                        World world = Bukkit.getWorld(args[0]);
                        player.sendMessage(ChatColor.GREEN + "Teleporting...");
                        double x = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.x");
                        double y = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.y");
                        double z = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.z");
                        double yaw = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.yaw");
                        double pitch = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.pitch");
                        Location loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
                        Location previous = player.getLocation();
                        String fromWorld = previous.getWorld().getName();
                        PlayerInventoryFile playerInventoryFile = new PlayerInventoryFile(plugin, player.getUniqueId().toString(), fromWorld);
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
                    sender.sendMessage(ChatColor.GOLD + "/universespawn " + ChatColor.YELLOW + "<world>");
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
