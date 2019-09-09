package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.guis.OverrideUI;
import net.whispwriting.universes.en.guis.UIItemData;
import net.whispwriting.universes.en.guis.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class OverridesInventoryClick implements Listener {

    private Universes plugin;
    private String uuid;
    public OverridesInventoryClick(Universes pl, String uid){
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
            UIItemData itemData = OverrideUI.items.get(item.getType());
            if (item.getItemMeta().getLocalizedName().equals(itemData.getID())) {
                e.setCancelled(true);
                OverrideUI.clickItem((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory(), plugin);
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
        HandlerList.unregisterAll(OverridesInventoryClick.this);
    }

}
