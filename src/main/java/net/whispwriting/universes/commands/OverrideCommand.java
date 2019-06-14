package net.whispwriting.universes.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.files.PlayerSettingsFile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OverrideCommand implements CommandExecutor {

    private Universes plugin;
    private PlayerSettingsFile playerSettings;

    public OverrideCommand(Universes pl, PlayerSettingsFile ps){
        plugin = pl;
        playerSettings = ps;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("gameModeOverride") || args[0].equalsIgnoreCase("gmo")) {
                    if (player.hasPermission("Universes.override.gamemode")) {
                        playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                        boolean gmOverride = playerSettings.get().getBoolean("gameModeOverride");
                        if (gmOverride){
                            player.sendMessage(ChatColor.RED + "GameMode Override disabled.");
                            playerSettings.get().set("gameModeOverride", false);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }else{
                            player.sendMessage(ChatColor.GREEN + "GameMode Override enabled.");
                            playerSettings.get().set("gameModeOverride", true);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
                        return true;
                    }
                }else if (args[0].equalsIgnoreCase("fullWorldOverride") || args[0].equalsIgnoreCase("fwo")){
                    if (player.hasPermission("Universes.override.fullworld")){
                        playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                        boolean fwOverride = playerSettings.get().getBoolean("canJoinFullWorlds");
                        if (fwOverride){
                            player.sendMessage(ChatColor.RED + "Full World Override disabled.");
                            playerSettings.get().set("canJoinFullWorlds", false);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }else{
                            player.sendMessage(ChatColor.GREEN + "Full World Override enabled.");
                            playerSettings.get().set("canJoinFullWorlds", true);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("flightOverride") || args[0].equalsIgnoreCase("flo")){
                    if (sender.hasPermission("Universes.override.flight")) {
                        playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                        boolean flightOverride = playerSettings.get().getBoolean("flightOverride");
                        if (flightOverride) {
                            player.sendMessage(ChatColor.RED + "Flight Override disabled.");
                            playerSettings.get().set("flightOverride", false);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Flight Override enabled.");
                            playerSettings.get().set("flightOverride", true);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
                        return true;
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "That is not a setting you can modify.");
                    return true;
                }
            }else{
                player.sendMessage(ChatColor.GOLD + "/universeoverride " + ChatColor.YELLOW + "<gameModeOverride:fullWorldOverride:flightOverride>");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Only players may execute that command.");
            return true;
        }
    }
}
