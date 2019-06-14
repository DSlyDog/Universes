package net.whispwriting.universes.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.files.PlayerSettingsFile;
import net.whispwriting.universes.files.WorldSettingsFile;
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
            int playerLimit = worldSettings.get().getInt("worlds."+to.getWorld().getName()+".playerLimit");
            if (to.getWorld().getPlayers().size() >= playerLimit && playerLimit != -1 && !playerSettings.get().getBoolean("canJoinFullWorlds")){
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Sorry, that world is full.");
                return;
            }
            String gameModeString = worldSettings.get().getString("worlds."+to.getWorld().getName()+".gameMode");
            GameMode mode = getGameModeValue(gameModeString);
            if (playerSettings.get().getBoolean("gameModeOverride")){
                return;
            }
            if (mode == null){
                System.out.println("[Universes] The GameMode, "+gameModeString+" is an invalid GameMode type for the world, " +to.getWorld().getName()+ ". please change it to either survival, creative, adventure, or spectator.");
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "There was an error updating your gamemode on entering this world. As a result, you have been put into the default GameMode of Survival. Please report this to an operator and tell them to check the console.");
            }else{
                event.getPlayer().setGameMode(mode);
            }
        }
    }

    public GameMode getGameModeValue(String gm){
        switch (gm){
            case "survival":
                return GameMode.SURVIVAL;
            case "creative":
                return GameMode.CREATIVE;
            case "adventure":
                return GameMode.ADVENTURE;
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

}
