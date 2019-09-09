package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.guis.OverrideUI;
import net.whispwriting.universes.en.guis.UIItemData;
import net.whispwriting.universes.en.guis.Utils;
import net.whispwriting.universes.en.guis.WorldSettingsUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ModifyInventoryClick implements Listener {

    private Universes plugin;
    private String uuid;
    public ModifyInventoryClick(Universes pl, String uid){
        plugin = pl;
        uuid = uid;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (!uuid.equals(e.getWhoClicked().getUniqueId().toString())){
            return;
        }
        try {
            ItemStack item = e.getCurrentItem();
            UIItemData itemData = WorldSettingsUI.items.get(item.getType());
            if (item.getItemMeta().getLocalizedName().equals(itemData.getID())) {
                e.setCancelled(true);
                WorldSettingsUI.clicked((Player) e.getWhoClicked(), plugin, e.getCurrentItem(), e.getWhoClicked().getLocation().getWorld().getName());
            }
        }catch(NullPointerException err){
            // do nothing
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (!uuid.equals(e.getPlayer().getUniqueId().toString())){
            return;
        }
        HandlerList.unregisterAll(ModifyInventoryClick.this);
    }

}
