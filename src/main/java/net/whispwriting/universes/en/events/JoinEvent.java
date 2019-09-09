package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private Universes plugin;

    public JoinEvent(Universes pl){
        plugin = pl;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (!event.getPlayer().hasPlayedBefore()){
            String uuid = event.getPlayer().getUniqueId().toString();
            plugin.playerSettings = new PlayerSettingsFile(plugin, uuid);
            plugin.playerSettings.setup();
        }
    }

}
