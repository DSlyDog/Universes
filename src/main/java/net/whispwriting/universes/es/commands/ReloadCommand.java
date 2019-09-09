package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private Universes plugin;
    public ReloadCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.reload")){
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }
        plugin.worldSettingsEs.reload();
        plugin.worldListFileEs.reload();
        sender.sendMessage(ChatColor.GREEN + "Configuración actualizada");
        return true;
    }
}
