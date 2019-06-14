package net.whispwriting.universes.commands;

import net.whispwriting.universes.files.WorldSettingsFile;
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
            if (args.length == 1) {
                if (player.hasPermission("Universes.teleport")) {
                    World world = Bukkit.getWorld(args[0]);
                    if (world != null) {
                        player.sendMessage(ChatColor.GREEN + "Teleporting...");
                        Location loc = (Location) worldSettings.get().get("worlds."+args[0]+".spawn");
                        loc.setYaw(player.getLocation().getYaw());
                        loc.setPitch(player.getLocation().getPitch());
                        player.teleport(loc);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Could not find a world by that name.");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                    return true;
                }
            }else{
                player.sendMessage(ChatColor.GOLD + "/universeteleport " + ChatColor.YELLOW + "<world>");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Only players may use that command.");
            return true;
        }
    }
}
