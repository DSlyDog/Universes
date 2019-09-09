package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    private Universes plugin;

    public TeleportCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("Universes.teleport")) {
                if (args.length == 1) {
                    World world = Bukkit.getWorld(args[0]);
                    if (world != null) {
                        player.sendMessage(ChatColor.GREEN + "Teleporting...");
                        String name = worldSettings.get().getString("worlds." + args[0] + ".spawn.world");
                        double x = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.x");
                        double y = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.y");
                        double z = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.z");
                        Location loc = new Location(world, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
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
