package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.utils.PlayersWhoCanConfirm;
import net.whispwriting.universes.en.files.WorldListFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand implements CommandExecutor {

    private Universes plugin;
    private WorldListFile worldList;
    private WorldSettingsFile worldSettings;

    public DeleteCommand(Universes pl, WorldListFile wl, WorldSettingsFile ws){
        plugin = pl;
        worldList = wl;
        worldSettings = ws;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.delete")) {
            sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }
        if (sender instanceof Player) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.GOLD + "/universedelete " + ChatColor.YELLOW + "<world name>");
                return true;
            }
            World world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "Could not find a world by that name.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Are you sure you want to delete world " + ChatColor.DARK_RED + args[0] + ChatColor.RED + "?");
            sender.sendMessage(ChatColor.RED + "Type /confirm to confirm deletion, or /cancel to cancel.");
            plugin.players.add(new PlayersWhoCanConfirm(sender, args[0]));
            return true;
        }else{
            sender.sendMessage(ChatColor.RED + "Only players may execute that command.");
            return true;
        }
    }
}
