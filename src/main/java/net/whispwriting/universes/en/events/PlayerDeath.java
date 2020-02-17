package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.ConfigFile;
import net.whispwriting.universes.en.files.EnderChestInventoryFile;
import net.whispwriting.universes.en.files.PerWorldInventoryGroupsFile;
import net.whispwriting.universes.en.files.PlayerInventoryFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PlayerDeath implements Listener {

    private Universes plugin = (Universes) Bukkit.getPluginManager().getPlugin("Universes");

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        ConfigFile config = new ConfigFile(plugin);
        boolean perWorldInventoriesEnabled = config.get().getBoolean("per-world-inventories");
        if (perWorldInventoriesEnabled) {
            boolean inventoryGrouping = config.get().getBoolean("per-world-inventory-grouping");
            if (!inventoryGrouping) {
                String uuid = event.getEntity().getUniqueId().toString();
                PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, event.getEntity().getLocation().getWorld().getName());
                boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
                boolean saveInvOnDeath = config.get().getBoolean("save-inventory-on-death");
                if (!saveInvOnDeath) {
                    for (int i = 0; i < event.getEntity().getInventory().getSize(); i++) {
                        playerInventory.get().set("item" + i, "");
                    }
                    playerInventory.get().set("offhand_item", "");
                    playerInventory.save();
                    float xp = event.getEntity().getExp();
                    int level = event.getEntity().getLevel();
                    playerInventory.get().set("xp", xp);
                    playerInventory.get().set("level", level/2);
                    playerInventory.get().set("health", 20);
                    playerInventory.get().set("hunger", 20);
                    playerInventory.save();
                    saveECInv(event.getEntity(), event.getEntity().getLocation().getWorld().getName());
                } else {
                    float xp = event.getEntity().getExp();
                    int level = event.getEntity().getLevel();
                    playerInventory.get().set("xp", xp);
                    playerInventory.get().set("level", level);
                    playerInventory.get().set("health", 20);
                    playerInventory.get().set("hunger", 20);
                    playerInventory.save();
                    saveInventory(event.getEntity(), event.getEntity().getLocation().getWorld().getName());
                    saveECInv(event.getEntity(), event.getEntity().getLocation().getWorld().getName());
                }
            }else{
                boolean saveInvOnDeath = config.get().getBoolean("save-inventory-on-death");
                boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
                PerWorldInventoryGroupsFile groupFile = new PerWorldInventoryGroupsFile(plugin);
                String group = groupFile.get().getString(event.getEntity().getLocation().getWorld().getName() + ".group");
                String uuid = event.getEntity().getUniqueId().toString();
                PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, group);
                if (!saveInvOnDeath){
                    for (int i = 0; i < event.getEntity().getInventory().getSize(); i++) {
                        playerInventory.get().set("item" + i, "");
                    }
                    playerInventory.get().set("offhand_item", "");
                    playerInventory.save();
                    float xp = event.getEntity().getExp();
                    int level = event.getEntity().getLevel();
                    playerInventory.get().set("xp", xp);
                    playerInventory.get().set("level", level/2);
                    playerInventory.get().set("health", 20);
                    playerInventory.get().set("hunger", 20);
                    playerInventory.save();
                    saveECInvGroup(event.getEntity(), group);
                }else{
                    float xp = event.getEntity().getExp();
                    int level = event.getEntity().getLevel();
                    playerInventory.get().set("xp", xp);
                    playerInventory.get().set("level", level);
                    playerInventory.get().set("health", 20);
                    playerInventory.get().set("hunger", 20);
                    playerInventory.save();
                    System.out.println("saving inventory");
                    saveInventoryGroup(event.getEntity(), group);
                    saveECInvGroup(event.getEntity(), group);
                }
            }
        }
    }

    private void saveInventoryGroup(Player player, String group) {
        String uuid = player.getUniqueId().toString();
        Inventory inventory = player.getInventory();
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, group);
        int i=0;
        while (i < inventory.getContents().length){
            try{
                ItemStack item = inventory.getItem(i);
                String itemAsString = itemStackToBase64(item);
                playerInventory.get().set("item" + i, itemAsString);
            }catch(NullPointerException e){
                playerInventory.get().set("item" + i, "");
            }
            i++;
        }
        playerInventory.save();
        try{
            ItemStack item = player.getInventory().getItemInOffHand();
            String itemAsString = itemStackToBase64(item);
            playerInventory.get().set("offhand_item", itemAsString);
        }catch(NullPointerException e){
            playerInventory.get().set("offhand_item", "");
        }
        playerInventory.save();
    }

    public void saveECInvGroup(Player player, String group){
        String uuid = player.getUniqueId().toString();
        Inventory enderChest = player.getEnderChest();
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, group);
        int x = 0;
        while (x < enderChest.getContents().length){
            try{
                ItemStack item = enderChest.getItem(x);
                String itemAsString = itemStackToBase64(item);
                enderChestInventory.get().set("item" + x, itemAsString);
            }catch(NullPointerException e){
                enderChestInventory.get().set("item" + x, "");
            }
            x++;
        }
        enderChestInventory.save();
    }

    private void saveInventory(Player player, String world) {
        String uuid = player.getUniqueId().toString();
        Inventory inventory = player.getInventory();
        Inventory enderChest = player.getEnderChest();
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, world);
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, world);
        int i=0;
        int x = 0;
        while (x < enderChest.getContents().length){
            try{
                ItemStack item = enderChest.getItem(x);
                String itemAsString = itemStackToBase64(item);
                enderChestInventory.get().set("item" + x, itemAsString);
            }catch(NullPointerException e){
                enderChestInventory.get().set("item" + x, "");
            }
            x++;
        }
        enderChestInventory.save();
        while (i < inventory.getContents().length){
            try{
                ItemStack item = inventory.getItem(i);
                String itemAsString = itemStackToBase64(item);
                playerInventory.get().set("item" + i, itemAsString);
            }catch(NullPointerException e){
                playerInventory.get().set("item" + i, "");
            }
            i++;
        }
        playerInventory.save();
        try{
            ItemStack item = player.getInventory().getItemInOffHand();
            String itemAsString = itemStackToBase64(item);
            playerInventory.get().set("offhand_item", itemAsString);
        }catch(NullPointerException e){
            playerInventory.get().set("offhand_item", "");
        }
        playerInventory.save();
    }

    public void saveECInv(Player player, String world){
        String uuid = player.getUniqueId().toString();
        Inventory enderChest = player.getEnderChest();
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, world);
        int x = 0;
        while (x < enderChest.getContents().length){
            try{
                ItemStack item = enderChest.getItem(x);
                String itemAsString = itemStackToBase64(item);
                enderChestInventory.get().set("item" + x, itemAsString);
            }catch(NullPointerException e){
                enderChestInventory.get().set("item" + x, "");
            }
            x++;
        }
        enderChestInventory.save();
    }

    public String itemStackToBase64(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

}
