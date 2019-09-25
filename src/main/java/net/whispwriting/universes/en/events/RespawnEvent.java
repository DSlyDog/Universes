package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.*;
import net.whispwriting.universes.en.tasks.RespawnTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RespawnEvent implements Listener {

    private Universes plugin;

    public RespawnEvent(Universes pl){
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerRespawnEvent event){
        Location loc = event.getRespawnLocation();
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        Player player = event.getPlayer();
        String world = player.getLocation().getWorld().getName();
        String respawnWorldString = worldSettings.get().getString("worlds."+world+".respawnWorld");

        ConfigFile config = new ConfigFile(plugin);
        boolean perWorldInventoriesEnabled = config.get().getBoolean("per-world-inventories");
        boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
        if (perWorldInventoriesEnabled) {
            boolean inventoryGrouping = config.get().getBoolean("per-world-inventory-grouping");
            if (!inventoryGrouping) {
                String uuid = player.getUniqueId().toString();
                PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, loc.getWorld().getName());
                if (world.contains("_the_end")){
                    float xp = event.getPlayer().getExp();
                    int level = event.getPlayer().getLevel();
                    double health = event.getPlayer().getHealth();
                    int hunger = event.getPlayer().getFoodLevel();
                    playerInventory.get().set("xp", xp);
                    playerInventory.get().set("level", level);
                    playerInventory.get().set("health", health);
                    playerInventory.get().set("hunger", hunger);
                    playerInventory.save();
                    saveInventory(player, world);
                }
                getInventory(player, respawnWorldString);
                if (perWorldStatsEnabled) {
                    float xp = (float) playerInventory.get().getDouble("xp");
                    int level = playerInventory.get().getInt("level");
                    double health = playerInventory.get().getDouble("health");
                    int hunger = playerInventory.get().getInt("hunger");
                    event.getPlayer().setExp(xp);
                    event.getPlayer().setLevel(level);
                    event.getPlayer().setHealth(health);
                    event.getPlayer().setFoodLevel(hunger);
                }
            }else{
                PerWorldInventoryGroupsFile groupFile = new PerWorldInventoryGroupsFile(plugin);
                String group = groupFile.get().getString(respawnWorldString + ".group");
                String group2 = groupFile.get().getString(world + ".group");
                if (world.contains("_the_end")){
                    float xp = event.getPlayer().getExp();
                    int level = event.getPlayer().getLevel();
                    double health = event.getPlayer().getHealth();
                    int hunger = event.getPlayer().getFoodLevel();
                    PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), group2);
                    playerInventory.get().set("xp", xp);
                    playerInventory.get().set("level", level);
                    playerInventory.get().set("health", health);
                    playerInventory.get().set("hunger", hunger);
                    playerInventory.save();
                    saveInventoryGroup(player, group2);
                }
                getInventoryGroup(player, group);
                String uuid = player.getUniqueId().toString();
                PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, group);
                if (perWorldStatsEnabled) {
                    float xp = (float) playerInventory.get().getDouble("xp");
                    int level = playerInventory.get().getInt("level");
                    double health = playerInventory.get().getDouble("health");
                    int hunger = playerInventory.get().getInt("hunger");
                    event.getPlayer().setExp(xp);
                    event.getPlayer().setLevel(level);
                    event.getPlayer().setHealth(health);
                    event.getPlayer().setFoodLevel(hunger);
                }
            }
        }
        boolean useRespawnWorld = config.get().getBoolean(("use-respawnWorld"));
        Bukkit.getScheduler().runTaskLater(plugin, new RespawnTask(world, respawnWorldString, player), 1);
    }

    private void getInventory(Player player, String world) {
        String uuid = player.getUniqueId().toString();
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, world);
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, world);
        player.getEnderChest().clear();
        player.getInventory().clear();
        for (int x=0; x<player.getInventory().getSize(); x++){
            String serializedItem = playerInventory.get().getString("item"+x);
            if (serializedItem != null) {
                ItemStack item = itemStackFromBase64(serializedItem);
                player.getInventory().setItem(x, item);
            }
        }
        player.updateInventory();
        for (int i=0; i<player.getEnderChest().getSize(); i++){
            String serializedItem = enderChestInventory.get().getString("item"+i);
            if (serializedItem != null) {
                ItemStack item = itemStackFromBase64(serializedItem);
                player.getEnderChest().setItem(i, item);
            }
        }
        String serializedItem = playerInventory.get().getString("offhand_item");
        if (serializedItem != null) {
            ItemStack item = itemStackFromBase64(serializedItem);
            player.getInventory().setItemInOffHand(item);
        }
        player.updateInventory();
    }

    private void getInventoryGroup(Player player, String group) {
        String uuid = player.getUniqueId().toString();
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, group);
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, group);
        player.getEnderChest().clear();
        player.getInventory().clear();
        for (int x=0; x<player.getInventory().getSize(); x++){
            String serializedItem = playerInventory.get().getString("item"+x);
            ItemStack item = itemStackFromBase64(serializedItem);
            player.getInventory().setItem(x, item);
        }
        player.updateInventory();
        for (int i=0; i<player.getEnderChest().getSize(); i++){
            String serializedItem = enderChestInventory.get().getString("item"+i);
            ItemStack item = itemStackFromBase64(serializedItem);
            player.getEnderChest().setItem(i, item);
        }
        String serializedItem = playerInventory.get().getString("offhand_item");
        ItemStack item = itemStackFromBase64(serializedItem);
        player.getInventory().setItemInOffHand(item);
        player.updateInventory();

    }

    private void saveInventoryGroup(Player player, String group) {
        String uuid = player.getUniqueId().toString();
        Inventory inventory = player.getInventory();
        Inventory enderChest = player.getEnderChest();
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, group);
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, group);
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

    public ItemStack itemStackFromBase64(String data) {
        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(Base64Coder.decodeLines(data)));) {

            // Read the serialized item
            ItemStack item = (ItemStack) dataInput.readObject();

            return item;
        } catch (IOException | ClassNotFoundException | NullPointerException ex) {
            return null;
        }
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
