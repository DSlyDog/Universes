package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.utils.PlayersWhoCanConfirm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CancelCommand implements CommandExecutor {

    private Universes plugin;

    public CancelCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.delete")){
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }
        for (Object o : plugin.playersEs){
            PlayersWhoCanConfirm p = (PlayersWhoCanConfirm) o;
            if (p.getSender() == sender){
                sender.sendMessage(ChatColor.RED + "Eliminación de mundo cancelada.");
                plugin.playersEs.remove(p);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "No tienes nada que cancelar.");
        return true;
    }
}
