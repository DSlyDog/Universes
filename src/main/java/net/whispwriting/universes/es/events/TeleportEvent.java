package net.whispwriting.universes.es.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.es.files.PlayerSettingsFile;
import net.whispwriting.universes.es.files.WorldSettingsFile;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportEvent implements Listener {

    private WorldSettingsFile worldSettings;
    private PlayerSettingsFile playerSettings;
    private Universes plugin;

    public TeleportEvent(WorldSettingsFile ws, PlayerSettingsFile ps, Universes pl){
        worldSettings = ws;
        playerSettings = ps;
        plugin = pl;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Location from = event.getFrom();
        Location to = event.getTo();
        playerSettings = new PlayerSettingsFile(plugin, event.getPlayer().getUniqueId().toString());
        if (to.getWorld() != from.getWorld()){
            int playerLimit = worldSettings.get().getInt("worlds."+to.getWorld().getName()+".límiteDeJugadores");
            if (to.getWorld().getPlayers().size() >= playerLimit && playerLimit != -1 && !playerSettings.get().getBoolean("puedeUnirseConMundoLleno")){
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Lo siento, ese mundo está lleno.");
                return;
            }
            String gameModeString = worldSettings.get().getString("worlds."+to.getWorld().getName()+".modoDeJuego");
            GameMode mode = getGameModeValue(gameModeString);
            if (playerSettings.get().getBoolean("ignorarModoDeJuego")){
                return;
            }
            if (mode == null){
                System.out.println("[Universes] El Modo de Juego, "+gameModeString+" no es un Modo de Juego válido para el mundo, " +to.getWorld().getName()+ ". Por favor, cámbialo a supervivencia, creativo, aventura o espectador.");
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Se ha cometido un error actualizando el modo de juego al entrar a este mundo. Como consecuencia, se ha establecido tu Modo de Juego predeterminado a supervivencia. Por favor, reporta esto a un operador y dile que compruebe la consola.");
            }else{
                event.getPlayer().setGameMode(mode);
            }
        }
    }

    public GameMode getGameModeValue(String gm){
        switch (gm){
            case "supervivencia":
                return GameMode.SURVIVAL;
            case "survival":
                return GameMode.SURVIVAL;
            case "creativo":
                return GameMode.CREATIVE;
            case "creative":
                return GameMode.CREATIVE;
            case "aventura":
                return GameMode.ADVENTURE;
            case "adventure":
                return GameMode.ADVENTURE;
            case "espectador":
                return GameMode.SPECTATOR;
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

}
