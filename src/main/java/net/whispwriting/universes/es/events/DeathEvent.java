package net.whispwriting.universes.es.events;

import net.whispwriting.universes.es.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {

    private WorldSettingsFile worldSettings;

    public DeathEvent(WorldSettingsFile ws){
        worldSettings = ws;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = (Player) event.getEntity();
        String world = player.getLocation().getWorld().getName();
        String respawnWorldString = worldSettings.get().getString("worlds."+world+".mundoDeReaparición");

        World respawnWorld = Bukkit.getWorld(respawnWorldString);

        Location loc = respawnWorld.getSpawnLocation();
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());
        player.teleport(loc);

    }

}
