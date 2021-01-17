package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import org.bukkit.Bukkit;

public class PortalFired implements Runnable {
    private Universes plugin = (Universes) Bukkit.getPluginManager().getPlugin("Universes");

    @Override
    public void run() {
        plugin.log.info("Teleported Via Portal");
    }
}
