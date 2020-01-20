package net.whispwriting.universes.en.tasks;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Iterator;
import java.util.List;

public class SetSpawnFlagsTask implements Runnable {
    @Override
    public void run() {
        WorldSettingsFile worldSettings = new WorldSettingsFile((Universes) Bukkit.getPluginManager().getPlugin("Universes"));
        List<World> worlds = Bukkit.getWorlds();
        Iterator<World> worldIterator = worlds.iterator();
        while (worldIterator.hasNext()){
            World world = worldIterator.next();
            boolean allowAnimals = worldSettings.get().getBoolean("worlds."+world.getName()+".allowAnimals");
            boolean allowMonsters = worldSettings.get().getBoolean("worlds."+world.getName()+".allowMonsters");
            world.setSpawnFlags(allowMonsters, allowAnimals);
        }
    }
}
