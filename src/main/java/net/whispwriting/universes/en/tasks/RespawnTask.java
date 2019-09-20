package net.whispwriting.universes.en.tasks;

import net.whispwriting.universes.en.events.TeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RespawnTask implements Runnable {

    private String world;
    private String respawnWorldString;
    private Player player;

    public RespawnTask(String w, String rw, Player p){
        world = w;
        respawnWorldString = rw;
        player = p;
    }

    @Override
    public void run() {
        World respawnWorld = Bukkit.getWorld(respawnWorldString);
        Location loc = respawnWorld.getSpawnLocation();
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());
        TeleportEvent.setCancelEvent(true);
        player.teleport(loc);
        TeleportEvent.setCancelEvent(false);
    }
}
