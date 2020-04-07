package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.ConfigFile;
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

    public JoinEvent(Universes plugin){
        this.plugin = plugin;
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
        plugin.config = new ConfigFile(plugin);
        boolean useFirstJoin = plugin.config.get().getBoolean("use-first-join-spawn");
        if (useFirstJoin) {
            SpawnFile spawnFile = new SpawnFile(plugin);
            Player player = event.getPlayer();
            if (!player.hasPlayedBefore()) {
                double x = spawnFile.get().getDouble("x");
                double y = spawnFile.get().getDouble("y");
                double z = spawnFile.get().getDouble("z");
                float pitch = (float) spawnFile.get().getDouble("pitch");
                float yaw = (float) spawnFile.get().getDouble("yaw");
                String worldStr = spawnFile.get().getString("world");
                World world;
                try {
                    world = Bukkit.getWorld(worldStr);
                } catch (IllegalArgumentException e) {
                    return;
                }

                Location loc = new Location(world, x, y, z, yaw, pitch);

                player.teleport(loc);
            }
        }
    }

}
