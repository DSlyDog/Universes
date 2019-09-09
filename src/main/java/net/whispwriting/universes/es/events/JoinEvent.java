package net.whispwriting.universes.es.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.es.files.PlayerSettingsFile;
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
            plugin.playerSettingsEs = new PlayerSettingsFile(plugin, uuid);
            plugin.playerSettingsEs.setup();
        }
    }

}
