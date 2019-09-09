package net.whispwriting.universes.es.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "---------- " + ChatColor.YELLOW + "La Ayuda de Universes " + ChatColor.GOLD + "----------");
        sender.sendMessage(ChatColor.GOLD + "/universehelp " + ChatColor.YELLOW + "muestra la ayuda de Universes");
        if (sender.hasPermission("Universes.listworlds")){
            sender.sendMessage(ChatColor.GOLD + "/universelist " + ChatColor.YELLOW + "muestra la lista de mundos disponibles");
        }
        if (sender.hasPermission("Universes.teleport")){
            sender.sendMessage(ChatColor.GOLD + "/universeteleport " + ChatColor.YELLOW + "teletransportarte a otros mundos");
        }
        if (sender.hasPermission("Universes.createworld")) {
            sender.sendMessage(ChatColor.GOLD + "/universecreate " + ChatColor.YELLOW + "crea un nuevo mundo");
        }
        if (sender.hasPermission("Universes.importworld")) {
            sender.sendMessage(ChatColor.GOLD + "/universeimport " + ChatColor.YELLOW + "importa un mundo existente");
        }
        if (sender.hasPermission("Universes.unload")){
            sender.sendMessage(ChatColor.GOLD + "/universeunload " + ChatColor.YELLOW + "descarga un mundo existente");
        }
        if (sender.hasPermission("Universes.delete")) {
            sender.sendMessage(ChatColor.GOLD + "/universedelete " + ChatColor.YELLOW + "elimina un mundo existente");
        }
        if (sender.hasPermission("Universes.modify")){
            sender.sendMessage(ChatColor.GOLD + "/universemodify " + ChatColor.YELLOW + "modifica los ajustes de un mundo existente");
        }
        if (sender.hasPermission("Universes.override.gamemode") || sender.hasPermission("Universes.override.fullworld") || sender.hasPermission("Universes.override.flight")){
            sender.sendMessage(ChatColor.GOLD + "/universeoverride " + ChatColor.YELLOW + "permite o denega ignorar algunos ajustes ");
        }
        if (sender.hasPermission("Universes.reload")){
            sender.sendMessage(ChatColor.GOLD + "/universesreload " + ChatColor.YELLOW + "actualiza la configuración del plugin.");
        }
        return true;
    }
}
