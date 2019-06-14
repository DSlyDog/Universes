package net.whispwriting.universes;

import net.whispwriting.universes.Utils.Generator;
import net.whispwriting.universes.Utils.PlayersWhoCanConfirm;
import net.whispwriting.universes.commands.*;
import net.whispwriting.universes.events.*;
import net.whispwriting.universes.files.PlayerSettingsFile;
import net.whispwriting.universes.files.WorldListFile;
import net.whispwriting.universes.files.WorldSettingsFile;
import net.whispwriting.universes.tasks.WorldGenTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Universes extends JavaPlugin {

    public WorldSettingsFile worlds;
    public WorldListFile worldListFile;
    public PlayerSettingsFile playerSettings;
    public List<PlayersWhoCanConfirm> players = new ArrayList<>();

    @Override
    public void onEnable() {
        worldListFile = new WorldListFile(this);
        loadWorlds();
        worlds = new WorldSettingsFile(this);
        createWorldConfig();

        this.getCommand("universecreate").setExecutor(new CreateCommand(this));
        this.getCommand("universeimport").setExecutor(new ImportCommand(this));
        this.getCommand("universelist").setExecutor(new ListWorldsCommand());
        this.getCommand("universeteleport").setExecutor(new TeleportCommand(worlds));
        this.getCommand("universeoverride").setExecutor(new OverrideCommand(this, playerSettings));
        this.getCommand("universemodify").setExecutor(new ModifyCommand(this, worlds, playerSettings));
        this.getCommand("universedelete").setExecutor(new DeleteCommand(this, worldListFile, worlds));
        this.getCommand("universeunload").setExecutor(new UnloadCommand(worldListFile));
        this.getCommand("confirm").setExecutor(new ConfirmCommand(this, worldListFile, worlds));
        this.getCommand("cancel").setExecutor(new CancelCommand(this));
        this.getCommand("universehelp").setExecutor(new HelpCommand());

        Bukkit.getPluginManager().registerEvents(new TeleportEvent(worlds, playerSettings, this), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(worlds), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new FlyEvent(this, worlds, playerSettings), this);
        Bukkit.getPluginManager().registerEvents(new PVPEvent(worlds), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadWorlds(){
        List<String> worldList = worldListFile.get().getStringList("worlds");

        for (String world : worldList){
            File file = new File(Bukkit.getWorldContainer() + "/" + world + "/");
            if (file.exists()){
                if (Bukkit.getWorld(world) == null){
                    Generator generator = new Generator(this, world);
                    generator.createWorld();
                }
            }
        }
    }

    private void createWorldConfig(){
        worlds.get().addDefault("worlds.world.name", "world");
        worlds.get().addDefault("worlds.world.type", "normal");
        worlds.get().addDefault("worlds.world.pvp", true);
        worlds.get().addDefault("worlds.world.spawn", Bukkit.getWorld("world").getSpawnLocation());
        worlds.get().addDefault("worlds.world.allowMonsters", true);
        worlds.get().addDefault("worlds.world.allowAnimals", true);
        worlds.get().addDefault("worlds.world.gameMode", "survival");
        worlds.get().addDefault("worlds.world.respawnWorld", "world");
        worlds.get().addDefault("worlds.world.playerLimit", -1);
        worlds.get().addDefault("worlds.world.allowFlight", true);

        worlds.get().addDefault("worlds.world_nether.name", "world_nether");
        worlds.get().addDefault("worlds.world_nether.type", "nether");
        worlds.get().addDefault("worlds.world_nether.pvp", true);
        worlds.get().addDefault("worlds.world_nether.spawn", Bukkit.getWorld("world_nether").getSpawnLocation());
        worlds.get().addDefault("worlds.world_nether.allowMonsters", true);
        worlds.get().addDefault("worlds.world_nether.allowAnimals", true);
        worlds.get().addDefault("worlds.world_nether.gameMode", "survival");
        worlds.get().addDefault("worlds.world_nether.respawnWorld", "world");
        worlds.get().addDefault("worlds.world_nether.playerLimit", -1);
        worlds.get().addDefault("worlds.world_nether.allowFlight", true);

        worlds.get().addDefault("worlds.world_the_end.name", "world_the_end");
        worlds.get().addDefault("worlds.world_the_end.type", "end");
        worlds.get().addDefault("worlds.world_the_end.pvp", true);
        worlds.get().addDefault("worlds.world_the_end.spawn", Bukkit.getWorld("world_the_end").getSpawnLocation());
        worlds.get().addDefault("worlds.world_the_end.allowMonsters", true);
        worlds.get().addDefault("worlds.world_the_end.allowAnimals", true);
        worlds.get().addDefault("worlds.world_the_end.gameMode", "survival");
        worlds.get().addDefault("worlds.world_the_end.respawnWorld", "world");
        worlds.get().addDefault("worlds.world_the_end.playerLimit", -1);
        worlds.get().addDefault("worlds.world_the_end.allowFlight", true);

        worlds.save();
        worldListFile.save();
    }
}
