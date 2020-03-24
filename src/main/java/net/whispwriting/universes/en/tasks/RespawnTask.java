package net.whispwriting.universes.en.tasks;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.events.TeleportEvent;
import net.whispwriting.universes.en.files.ConfigFile;
import net.whispwriting.universes.en.files.PlayerInventoryFile;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import sun.security.krb5.Config;

public class RespawnTask implements Runnable {

    private String to;
    private Player player;
    private Universes plugin;
    private boolean gameModeOverride;
    private String from;
    private String respawnWorld;

    public RespawnTask(String to, String from, String respawnWorld, Player p, Universes plugin, boolean gameModeOverride){
        this.to = to;
        player = p;
        this.plugin = plugin;
        this.gameModeOverride = gameModeOverride;
        this.from = from;
        this.respawnWorld = respawnWorld;
    }

    @Override
    public void run() {
        ConfigFile config = new ConfigFile(plugin);
        boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
        boolean useRespawnWorld = config.get().getBoolean(("use-respawnWorld"));
        if (useRespawnWorld) {
            if (perWorldStatsEnabled) {
                getStats(player, respawnWorld);
            }
            Location bedSpawn = player.getBedSpawnLocation();
            try {
                String bedSpawnWorld = bedSpawn.getWorld().getName();
                if (!(from.equals(respawnWorld) && respawnWorld.equals(bedSpawnWorld))) {
                    if (perWorldStatsEnabled) {
                        getStats(player, respawnWorld);
                    }
                    teleport(respawnWorld);
                }
            }catch(NullPointerException e){
                if (perWorldStatsEnabled) {
                    getStats(player, respawnWorld);
                }
                teleport(respawnWorld);
            }
        }
        else {
            if (perWorldStatsEnabled) {
                getStats(player, to);
            }
            teleport(to);
        }
    }

    public GameMode getGameModeValue(String gm){
        switch (gm){
            case "survival":
                return GameMode.SURVIVAL;
            case "creative":
                return GameMode.CREATIVE;
            case "adventure":
                return GameMode.ADVENTURE;
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

    private void getStats(Player player, String to){
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, player.getUniqueId().toString(), to);
        float xp = (float) playerInventory.get().getDouble("xp");
        int level = playerInventory.get().getInt("level");
        double health = playerInventory.get().getDouble("health");
        int hunger = playerInventory.get().getInt("hunger");
        if (health == 0) {
            health = 20;
            hunger = 20;
        }
        player.setExp(xp);
        player.setLevel(level);
        player.setHealth(health);
        player.setFoodLevel(hunger);
    }

    private void teleport(String toWorld){
        World respawn = Bukkit.getWorld(toWorld);
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        double x = worldSettings.get().getDouble("worlds." + toWorld + ".spawn.x");
        double y = worldSettings.get().getDouble("worlds." + toWorld + ".spawn.y");
        double z = worldSettings.get().getDouble("worlds." + toWorld + ".spawn.z");
        double yaw = worldSettings.get().getDouble("worlds." + toWorld + ".spawn.yaw");
        double pitch = worldSettings.get().getDouble("worlds." + toWorld + ".spawn.pitch");
        Location loc = new Location(respawn, x, y, z, (float) yaw, (float) pitch);
        TeleportEvent.setCancelEvent(true);
        player.teleport(loc);
        if (!gameModeOverride) {
            String gameModeStr = worldSettings.get().getString("worlds." + toWorld + ".gameMode");
            player.setGameMode(getGameModeValue(gameModeStr));
        }
        TeleportEvent.setCancelEvent(false);
    }
}
