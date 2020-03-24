package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.ConfigFile;
import net.whispwriting.universes.en.files.PlayerInventoryFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportHistoryEvent implements Listener {

    private Universes plugin;

    public TeleportHistoryEvent(Universes plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        ConfigFile config = new ConfigFile(plugin);
        boolean trackAllTp = config.get().getBoolean("track-all-teleports");
        if (trackAllTp){
            if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())){
                PlayerInventoryFile playerInventoryFile = new PlayerInventoryFile(plugin,
                        event.getPlayer().getUniqueId().toString(), event.getFrom().getWorld().getName());
                playerInventoryFile.get().set("previous.x", event.getFrom().getX());
                playerInventoryFile.get().set("previous.y", event.getFrom().getY());
                playerInventoryFile.get().set("previous.z", event.getFrom().getZ());
                playerInventoryFile.get().set("previous.pitch", event.getFrom().getPitch());
                playerInventoryFile.get().set("previous.yaw", event.getFrom().getYaw());
                playerInventoryFile.save();
            }
        }
    }

}
