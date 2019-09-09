package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PVPEvent implements Listener {

    private WorldSettingsFile worldSettings;
    private Universes plugin;

    public PVPEvent(Universes pl){
        plugin = pl;
    }


    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            String worldName = damager.getLocation().getWorld().getName();
            if (event.getEntity() instanceof Player) {
                worldSettings = new WorldSettingsFile(plugin);
                boolean allowPVP = worldSettings.get().getBoolean("worlds." + worldName + ".pvp");
                if (!allowPVP) {
                    event.setCancelled(true);
                    damager.sendMessage(ChatColor.RED + "PVP is not allowed in this world.");
                }
            }
        }
    }

}
