package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import net.whispwriting.universes.en.files.SpawnFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private Universes plugin;
    private SpawnFile spawnFile;

    public JoinEvent(Universes plugin, SpawnFile spawnFile){
        this.plugin = plugin;
        this.spawnFile = spawnFile;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (!event.getPlayer().hasPlayedBefore()){
            String uuid = event.getPlayer().getUniqueId().toString();
            plugin.playerSettings = new PlayerSettingsFile(plugin, uuid);
            plugin.playerSettings.setup();
        }
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()){
            int x = spawnFile.get().getInt("x");
            int y = spawnFile.get().getInt("y");
            int z = spawnFile.get().getInt("z");
            int pitch = spawnFile.get().getInt("pitch");
            int yaw = spawnFile.get().getInt("yaw");
            String worldStr = spawnFile.get().getString("world");
            World world;
            try {
                world = Bukkit.getWorld(worldStr);
            }catch(IllegalArgumentException e){
                return;
            }

            Location loc = new Location(world, x, y, z, yaw, pitch);

            player.teleport(loc);
        }
    }

}
