package net.whispwriting.universes.en.utils;

import org.bukkit.inventory.Inventory;

public class Inventories {

    private Inventory inventory;

    public Inventories(Inventory inv){
        inventory = inv;
    }

    public Inventory getInventory(){
        return inventory;
    }

}
