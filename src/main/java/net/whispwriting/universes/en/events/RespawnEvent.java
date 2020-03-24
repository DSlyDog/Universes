package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.*;
import net.whispwriting.universes.en.tasks.RespawnTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        Player player = event.getPlayer();
        String fromWorld = player.getLocation().getWorld().getName();
        String toWorld = event.getRespawnLocation().getWorld().getName();
        String respawnWorldString = worldSettings.get().getString("worlds."+fromWorld+".respawnWorld");


        ConfigFile config = new ConfigFile(plugin);
        boolean perWorldInventoriesEnabled = config.get().getBoolean("per-world-inventories");
        boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
        if (perWorldInventoriesEnabled) {
            boolean inventoryGrouping = config.get().getBoolean("per-world-inventory-grouping");
            if (!inventoryGrouping) {
                saveStats(event.getPlayer(), fromWorld);
                saveInventory(player, fromWorld);
                boolean useRespawnWorld = config.get().getBoolean(("use-respawnWorld"));
                if (useRespawnWorld)
                    getInventory(player, respawnWorldString);
                else
                    getInventory(player, toWorld);
            }else{
                PerWorldInventoryGroupsFile groupFile = new PerWorldInventoryGroupsFile(plugin);
                boolean useRespawnWorld = config.get().getBoolean(("use-respawnWorld"));
                String toGroup;
                String fromGroup = groupFile.get().getString(fromWorld + ".group");
                if (useRespawnWorld) {
                    toGroup = groupFile.get().getString(respawnWorldString + ".group");
                }else{
                    toGroup = groupFile.get().getString(toWorld + ".group");
                }
                saveStats(event.getPlayer(), fromGroup);
                saveInventoryGroup(player, fromGroup);
                getInventoryGroup(player, toGroup);
                if (perWorldStatsEnabled) {
                    getStats(event.getPlayer(), toGroup);
                }
            }
        }
        PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
        boolean gameModeOverride = playerSettings.get().getBoolean("gameModeOverride");
        boolean useRespawnWorld = config.get().getBoolean(("use-respawnWorld"));
        if (useRespawnWorld) {
            if (perWorldStatsEnabled) { // move this to if (!inventoryGrouping) statement above
                getStats(event.getPlayer(), respawnWorldString);
            }
            Bukkit.getScheduler().runTaskLater(plugin, new RespawnTask(toWorld, fromWorld, respawnWorldString, player, plugin, gameModeOverride), 5);
        }
        else {
            if (perWorldStatsEnabled) {
                getStats(event.getPlayer(), toWorld);
            }
            if (!gameModeOverride) {
                String gameModeStr = worldSettings.get().getString("worlds." + toWorld + ".gameMode");
                player.setGameMode(getGameModeValue(gameModeStr));
            }
        }
    }

    private void saveStats(Player player, String from){
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, player.getUniqueId().toString(), from);
        float xp = player.getExp();
        int level = player.getLevel();
        double health = player.getHealth();
        int hunger = player.getFoodLevel();
        playerInventory.get().set("xp", xp/2);
        playerInventory.get().set("level", level/2);
        if (health == 0) {
            playerInventory.get().set("health", 20);
            playerInventory.get().set("hunger", 20);
        }else{
            playerInventory.get().set("health", health);
            playerInventory.get().set("hunger", hunger);
        }
        playerInventory.save();
    }

    private void getStats(Player player, String to){
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, player.getUniqueId().toString(), to);
        float xp = (float) playerInventory.get().getDouble("xp");
        int level = playerInventory.get().getInt("level");
        double health = playerInventory.get().getDouble("health");
        int hunger = playerInventory.get().getInt("hunger");
        player.setExp(xp);
        player.setLevel(level);
        player.setHealth(health);
        player.setFoodLevel(hunger);
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

    public GameMode getGameModeValue(String gm){
        switch (gm){
            case "survival":
                return GameMode.SURVIVAL;
            case "creative":
                return GameMode.CREATIVE;
            case "adventure":
                return GameMode.ADVENTURE;
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

}
