package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import net.whispwriting.universes.en.guis.Utils;
import net.whispwriting.universes.en.guis.WorldSettingsUI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.List;

public class ChangeRespawnWorld implements Listener {

    private String uuid;
    private String worldToModify;
    private Universes plugin;

    public ChangeRespawnWorld(String u, Universes pl, String wtm){
        uuid = u;
        worldToModify = wtm;
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        event.setCancelled(true);
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        String uid = event.getPlayer().getUniqueId().toString();
        if (!uid.equals(uuid)){
            event.setCancelled(false);
            return;
        }
        String message = event.getMessage();
        String[] messageArray = message.split(" ");
        if (messageArray[0].equals("cancel")){
            event.getPlayer().sendMessage(Utils.chat("&cChanging the respawn world has been canceled."));
            HandlerList.unregisterAll(ChangeRespawnWorld.this);
            return;
        }
        World world = Bukkit.getWorld(messageArray[0]);
        if (world == null){
            event.getPlayer().sendMessage(Utils.chat("&cCould not find a world by that name. Please try again, or say \"cancel\" to cancel."));
            return;
        }
        String worldName = world.getName();
        worldSettings.get().set("worlds."+worldToModify+".respawnWorld", worldName);
        worldSettings.save();
        worldSettings.reload();
        event.getPlayer().sendMessage(Utils.chat("&2respawnWorld has been updated."));
        World worldToModifyWorld = Bukkit.getWorld(worldToModify);
        HandlerList.unregisterAll(ChangeRespawnWorld.this);
    }

}
