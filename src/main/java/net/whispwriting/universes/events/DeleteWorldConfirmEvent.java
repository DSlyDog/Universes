package net.whispwriting.universes.events;

import net.whispwriting.universes.files.WorldListFile;
import net.whispwriting.universes.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.io.File;
import java.util.List;

public class DeleteWorldConfirmEvent implements Listener {

    private String worldName;
    private Player player;
    private WorldListFile worldList;
    private WorldSettingsFile worldSettings;

    public DeleteWorldConfirmEvent(String wn, Player p, WorldListFile wl, WorldSettingsFile ws){
        worldName = wn;
        player = p;
        worldList = wl;
        worldSettings = ws;
    }

    @EventHandler
    public void onConfirm(PlayerChatEvent event){
        if (event.getPlayer() == player) {
            if (!event.getMessage().equals("confirm")) {
                player.sendMessage(ChatColor.RED + "Canceling world deletion.");
                event.getHandlers().unregister(DeleteWorldConfirmEvent.this);
                return;
            }
            event.setCancelled(true);
            player.sendMessage(ChatColor.GREEN + "Deleting world.");
            World world = Bukkit.getWorld(worldName);
            world.getEntities().clear();
            Bukkit.getServer().unloadWorld(world, true);
            File file = new File(Bukkit.getWorldContainer() + "/"+worldName);
            deleteFolderContents(file);
            file.delete();

            List<String> worlds = worldList.get().getStringList("worlds");
            worlds.remove(worldName);
            worldList.get().set("worlds", worlds);
            worldList.save();
            worldList.reload();

            worldSettings.get().set("worlds."+worldName, null);
            worldSettings.save();
            worldSettings.reload();

            player.sendMessage(ChatColor.GREEN + "World deleted.");
        }
    }

    private void deleteFolderContents(File directory){
        String[] files = directory.list();
        for (String f : files){
            System.out.println(f);
            File current = new File(directory.getPath(), f);
            if (current.isDirectory()){
                deleteFolderContents(current);
                current.delete();
            }else{
                current.delete();
            }
        }
    }

}
