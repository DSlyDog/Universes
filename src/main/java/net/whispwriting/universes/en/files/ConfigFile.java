package net.whispwriting.universes.en.files;

import net.whispwriting.universes.Universes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ConfigFile extends AbstractFile{

    public ConfigFile (Universes pl){
        super(pl, "config.yml", "");
    }

    public void createConfig() {
        config.addDefault("per-world-inventories", false);
        config.addDefault("per-world-stats", false);
        config.addDefault("per-world-inventory-grouping", false);
        config.addDefault("per-world-kit-grouping", false);
        config.addDefault("use-respawnWorld", false);
        config.addDefault("use-first-join-spawn", false);
        config.addDefault("track-all-teleports", true);
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins){
            if (plugin.getName().equals("Universe-Nethers")){
                config.addDefault("Universe-Nethers.nether-per-overworld", true);
                config.addDefault("Universe-Nethers.return-players-to-previous-world", true);
            }
            if (plugin.getName().equals("Universe-Ends")){
                config.addDefault("Universe-Ends.end-per-overworld", true);
            }
        }
    }
}
