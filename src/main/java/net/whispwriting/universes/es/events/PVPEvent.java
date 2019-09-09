package net.whispwriting.universes.es.events;

import net.whispwriting.universes.es.files.WorldSettingsFile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PVPEvent implements Listener {

    private WorldSettingsFile worldSettings;

    public PVPEvent(WorldSettingsFile ws){
        worldSettings = ws;
    }


    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            String worldName = damager.getLocation().getWorld().getName();
            if (event.getEntity() instanceof Player) {
                boolean allowPVP = worldSettings.get().getBoolean("worlds." + worldName + ".pvp");
                if (!allowPVP) {
                    event.setCancelled(true);
                    damager.sendMessage(ChatColor.RED + "El PVP no estás permitido en este mundo.");
                }
            }
        }
    }

}
