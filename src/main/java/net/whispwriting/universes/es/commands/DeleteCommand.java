package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.es.files.WorldListFile;
import net.whispwriting.universes.es.files.WorldSettingsFile;
import net.whispwriting.universes.es.utils.PlayersWhoCanConfirm;
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
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }
        if (sender instanceof Player) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.GOLD + "/universedelete " + ChatColor.YELLOW + "<nombre del mundo>");
                return true;
            }
            World world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "No se ha podido encontrar ningún mundo con ese nombre.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "¿Estás seguro de que quieres eliminar el mundo " + ChatColor.DARK_RED + args[0] + ChatColor.RED + "?");
            sender.sendMessage(ChatColor.RED + "Escribe /confirm para confirmar la eliminación, o /cancel para cancelarla.");
            plugin.playersEs.add(new PlayersWhoCanConfirm(sender, args[0]));
            return true;
        }else{
            sender.sendMessage(ChatColor.RED + "Sólo los jugadores pueden usar ese comando.");
            return true;
        }
    }
}
