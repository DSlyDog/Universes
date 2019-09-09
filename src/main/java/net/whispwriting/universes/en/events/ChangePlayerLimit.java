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

import java.util.InputMismatchException;

public class ChangePlayerLimit implements Listener {

    private String uuid;
    private WorldSettingsFile worldSettings;
    private String worldToModify;
    private Universes plugin;

    public ChangePlayerLimit(String u, WorldSettingsFile ws, String wtm, Universes pl){
        uuid = u;
        worldSettings = ws;
        worldToModify = wtm;
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event){
        String uid = event.getPlayer().getUniqueId().toString();
        if (!uid.equals(uuid)){
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();
        String[] messageArray = message.split(" ");
        if (messageArray[0].equals("cancel")){
            event.getPlayer().sendMessage(Utils.chat("&cChanging the player limit has been canceled."));
            HandlerList.unregisterAll(ChangePlayerLimit.this);
            return;
        }
        try {
            int newLimit = Integer.parseInt(messageArray[0]);
            if (!(newLimit > -2)){
                event.getPlayer().sendMessage(Utils.chat("&cPlease enter a number -1 or greater, or say \"cancel\" to cancel."));
                return;
            }
            worldSettings.get().set("worlds." + worldToModify + ".playerLimit", newLimit);
            worldSettings.save();
            worldSettings.reload();
            World worldToModifyWorld = Bukkit.getWorld(worldToModify);
            event.getPlayer().sendMessage(Utils.chat("&2playerLimit has been updated."));
            HandlerList.unregisterAll(ChangePlayerLimit.this);
        }catch(NumberFormatException e){
            event.getPlayer().sendMessage(Utils.chat("&cThat is not a valid number. Please enter a valid number or say \"cancel\" to cancel."));
            return;
        }
    }

}
