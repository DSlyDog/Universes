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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Universes extends JavaPlugin {

    public static WorldSettingsFile worlds;
    public static WorldListFile worldListFile;
    public PlayerSettingsFile playerSettings;
    public List<PlayersWhoCanConfirm> players = new ArrayList<>();

    @Override
    public void onEnable() {
        worldListFile = new WorldListFile(this);
        worlds = new WorldSettingsFile(this);
        loadWorlds();
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
        this.getCommand("ur").setExecutor(new ReloadCommand(this));

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
                    String type = worlds.get().getString("worlds."+world+".type");
                    World.Environment env = getTypeFromString(type);
                    generator.setEnvironment(env);
                    generator.createWorld();
                }
            }
        }
        worlds.reload();
    }

    private void createWorldConfig(){
        List<World> loadedWorlds = Bukkit.getWorlds();
        for (World loadedWorld : loadedWorlds) {
            System.out.println(loadedWorld.getName());
            Location worldSpawn = loadedWorld.getSpawnLocation();
            String world = loadedWorld.getName();
            String name = worlds.get().getString("worlds."+world+".name");
            System.out.println(name==null);
            if (name == null) {
                System.out.println("name was null. Setting defaults.");
                double x = worldSpawn.getX();
                double y = worldSpawn.getY();
                double z = worldSpawn.getZ();
                worlds.get().set("worlds." + world + ".name", world);
                worlds.get().set("worlds." + world + ".type", getStringFromType(loadedWorld));
                worlds.get().set("worlds." + world + ".pvp", true);
                worlds.get().set("worlds." + world + ".spawn.world", world);
                worlds.get().set("worlds." + world + ".spawn.x", x);
                worlds.get().set("worlds." + world + ".spawn.y", y);
                worlds.get().set("worlds." + world + ".spawn.z", z);
                worlds.get().set("worlds." + world + ".allowMonsters", true);
                worlds.get().set("worlds." + world + ".allowAnimals", true);
                worlds.get().set("worlds." + world + ".gameMode", "survival");
                worlds.get().set("worlds." + world + ".respawnWorld", world);
                worlds.get().set("worlds." + world + ".playerLimit", -1);
                worlds.get().set("worlds." + world + ".allowFlight", true);

                worlds.save();
                worldListFile.save();
            }
        }
    }

    public static void reload(){
        worlds.reload();
        worldListFile.reload();
        worldListFile.save();
        worlds.save();
    }

    private World.Environment getTypeFromString(String type){
        switch (type){
            case "normal":
                return World.Environment.NORMAL;
            case "nether":
                return World.Environment.NETHER;
            case "end":
                return World.Environment.THE_END;
            default:
                return World.Environment.NORMAL;
        }
    }

    private String getStringFromType(World world){
        if (world.getEnvironment() == World.Environment.NORMAL){
            return "normal";
        }else if (world.getEnvironment() == World.Environment.NETHER){
            return "nether";
        }else{
            return "end";
        }
    }
}
