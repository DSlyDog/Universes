package net.whispwriting.universes.en.tasks;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.utils.Generator;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;

public class WorldGenTask extends BukkitRunnable {

    private Universes plugin;

    public WorldGenTask(Universes pl){
        plugin = pl;
    }

    @Override
    public void run() {
        List<String> worldList = plugin.worldListFile.get().getStringList("worlds");

        for (String world : worldList){
            File file = new File(Bukkit.getWorldContainer() + "/" + world + "/");
            if (file.exists()){
				if (Bukkit.getWorld(world) == null){
					Generator generator = new Generator(plugin, world);
					generator.createWorld();
				}
            }
        }
    }
}
