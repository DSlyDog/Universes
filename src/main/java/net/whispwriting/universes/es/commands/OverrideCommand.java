package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.es.files.PlayerSettingsFile;
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
                if (args[0].equalsIgnoreCase("gameModeOverride") || args[0].equalsIgnoreCase("gmo")
                || args[0].equalsIgnoreCase("ignorarModoDeJuego") || args[0].equalsIgnoreCase("imdj")) {
                    if (player.hasPermission("Universes.override.gamemode")) {
                        playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                        boolean gmOverride = playerSettings.get().getBoolean("ignorarModoDeJuego");
                        if (gmOverride){
                            player.sendMessage(ChatColor.RED + "Ignorar Modo de Juego desactivado.");
                            playerSettings.get().set("ignorarModoDeJuego", false);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }else{
                            player.sendMessage(ChatColor.GREEN + "Ignorar Modo de Juego activado.");
                            playerSettings.get().set("ignorarModoDeJuego", true);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.DARK_RED + "No tienes permiso para cambiar ese ajuste.");
                        return true;
                    }
                }else if (args[0].equalsIgnoreCase("fullWorldOverride") || args[0].equalsIgnoreCase("fwo")
                || args[0].equalsIgnoreCase("puedeUnirseConMundoLleno") || args[0].equalsIgnoreCase("pucml")){
                    if (player.hasPermission("Universes.override.fullworld")){
                        playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                        boolean fwOverride = playerSettings.get().getBoolean("puedeUnirseConMundoLleno");
                        if (fwOverride){
                            player.sendMessage(ChatColor.RED + "Acceso a Mundo Lleno desactivado.");
                            playerSettings.get().set("puedeUnirseConMundoLleno", false);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }else{
                            player.sendMessage(ChatColor.GREEN + "Acceso a Mundo Lleno activado.");
                            playerSettings.get().set("puedeUnirseConMundoLleno", true);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.DARK_RED + "No tienes permiso para cambiar ese ajuste.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("flightOverride") || args[0].equalsIgnoreCase("flo")
                || args[0].equalsIgnoreCase("ignorarAjustesDeVuelo") || args[0].equalsIgnoreCase("iadv")){
                    if (sender.hasPermission("Universes.override.flight")) {
                        playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                        boolean flightOverride = playerSettings.get().getBoolean("ignorarAjustesDeVuelo");
                        if (flightOverride) {
                            player.sendMessage(ChatColor.RED + "Ignorar Ajustes de Vuelo desactivado.");
                            playerSettings.get().set("ignorarAjustesDeVuelo", false);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Ignorar Ajustes de Vuelo activado.");
                            playerSettings.get().set("ignorarAjustesDeVuelo", true);
                            playerSettings.save();
                            playerSettings.reload();
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.DARK_RED + "No tienes permiso para cambiar ese ajuste.");
                        return true;
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Ese no es un ajuste que puedas modificar.");
                    return true;
                }
            }else{
                player.sendMessage(ChatColor.GOLD + "/universeoverride " + ChatColor.YELLOW + "<ignorarModoDeJuego:puedeUnirseConMundoLleno:ignorarAjustesDeVuelo>");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Sólo los jugadores pueden usar ese comando.");
            return true;
        }
    }
}
