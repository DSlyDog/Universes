package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.es.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    private WorldSettingsFile worldSettings;

    public TeleportCommand(WorldSettingsFile ws){
        worldSettings = ws;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("Universes.teleport")) {
                if (args.length == 1) {
                    World world = Bukkit.getWorld(args[0]);
                    if (world != null) {
                        player.sendMessage(ChatColor.GREEN + "Teletransportando...");
                        String name = worldSettings.get().getString("worlds." + args[0] + ".spawn.world");
                        double x = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.x");
                        double y = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.y");
                        double z = worldSettings.get().getDouble("worlds." + args[0] + ".spawn.z");
                        Location loc = new Location(world, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                        player.teleport(loc);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "No se ha encontrado ningún mundo con ese nombre.");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD + "/universeteleport " + ChatColor.YELLOW + "<mundo>");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Sólo los jugadores pueden usar ese comando.");
            return true;
        }
    }
}
