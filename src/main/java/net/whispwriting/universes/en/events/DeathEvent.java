package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import net.whispwriting.universes.en.tasks.RespawnTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathEvent implements Listener {

    private Universes plugin;

    public DeathEvent(Universes pl){
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerRespawnEvent event){
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        Player player = event.getPlayer();
        player.sendMessage("Respawn Triggered");
        String world = player.getLocation().getWorld().getName();
        String respawnWorldString = worldSettings.get().getString("worlds."+world+".respawnWorld");
        Bukkit.getScheduler().runTaskLater(plugin, new RespawnTask(world, respawnWorldString, player), 1);
    }

}
