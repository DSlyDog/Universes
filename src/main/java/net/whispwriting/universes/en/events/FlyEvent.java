package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyEvent implements Listener {

    private PlayerSettingsFile playerSettings;
    private Universes plugin;

    public FlyEvent(Universes pl, PlayerSettingsFile ps){
        plugin = pl;
        playerSettings = ps;
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event){
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        Location loc = event.getPlayer().getLocation();
        String worldName = loc.getWorld().getName();

        playerSettings = new PlayerSettingsFile(plugin, event.getPlayer().getUniqueId().toString());
        boolean flightOverride = playerSettings.get().getBoolean("flightOverride");
        if (flightOverride){
            return;
        }

        boolean allowFlight = worldSettings.get().getBoolean("worlds."+worldName+".allowFlight");
        if (!allowFlight){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Flight is not allowed in that world.");
        }

    }

}
