package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.*;
import net.whispwriting.universes.en.tasks.GiveItemTask;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortalTeleport implements Listener {

    private PlayerSettingsFile playerSettings;
    private Universes plugin;
    private KitsFile kitsFile;
    private static boolean cancelEvent = false;

    public PortalTeleport(PlayerSettingsFile ps, Universes pl, KitsFile kf){
        playerSettings = ps;
        plugin = pl;
        kitsFile = kf;
    }

    @EventHandler
    public void playerTeleport(PlayerPortalEvent event){
        Bukkit.getScheduler().runTask(plugin, new TeleportFired());
        if (cancelEvent){
            return;
        }
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        Location from = event.getFrom();
        Location to = event.getTo();
        ConfigFile config = new ConfigFile(plugin);
        playerSettings = new PlayerSettingsFile(plugin, event.getPlayer().getUniqueId().toString());
        boolean perWorldInventoriesEnabled = config.get().getBoolean("per-world-inventories");
        if (to.getWorld() != from.getWorld()){
            int playerLimit = worldSettings.get().getInt("worlds."+to.getWorld().getName()+".playerLimit");
            if (to.getWorld().getPlayers().size() >= playerLimit && playerLimit != -1 && !playerSettings.get().getBoolean("canJoinFullWorlds")){
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Sorry, that world is full.");
                return;
            }
            PlayerInventoryFile playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), to.getWorld().getName());
            boolean inventoryGrouping = config.get().getBoolean("per-world-inventory-grouping");
            boolean visited = playerInventoryFile.get().getBoolean("visited");
            boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
            PerWorldInventoryGroupsFile groupFile;
            String kitName;
            String fromWorld;
            String fromGroup;
            String toWorld;
            String toGroup;
            if (!inventoryGrouping) {
                if (perWorldInventoriesEnabled) {
                    saveInventory(event.getPlayer(), from.getWorld().getName());
                    boolean perWorldInvOverride = playerSettings.get().getBoolean("perWorldInvOverride");
                    if (!perWorldInvOverride) {
                        if (visited) {
                            getInventory(event.getPlayer(), to.getWorld().getName());
                        }else{
                            event.getPlayer().getInventory().clear();
                            if (perWorldStatsEnabled){
                                event.getPlayer().setHealth(20);
                                event.getPlayer().setFoodLevel(20);
                                event.getPlayer().setExp(0);
                                event.getPlayer().setLevel(0);
                            }
                        }
                    }
                }
                float xp = event.getPlayer().getExp();
                int level = event.getPlayer().getLevel();
                double health = event.getPlayer().getHealth();
                int hunger = event.getPlayer().getFoodLevel();
                playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), from.getWorld().getName());
                playerInventoryFile.get().set("xp", xp);
                playerInventoryFile.get().set("level", level);
                playerInventoryFile.get().set("health", health);
                playerInventoryFile.get().set("hunger", hunger);
                playerInventoryFile.save();
                if (perWorldStatsEnabled) {
                    try {
                        String xpStr = playerInventoryFile.get().getString("xp");
                        String levelStr = playerInventoryFile.get().getString("level");
                        level = playerInventoryFile.get().getInt("level");
                        health = playerInventoryFile.get().getDouble("health");
                        hunger = playerInventoryFile.get().getInt("hunger");
                        if (!visited || health == 0) {
                            hunger = 20;
                        }
                        if (!visited || health == 0) {
                            health = 20;
                        }
                        if (!visited || xpStr == null) {
                            xp = 0;
                        }else{
                            xp = Float.parseFloat(xpStr);
                        }
                        if (!visited || levelStr == null) {
                            level = 0;
                        }else{
                            level = Integer.parseInt(levelStr);
                        }
                        event.getPlayer().setExp(xp);
                        event.getPlayer().setLevel(level);
                        event.getPlayer().setHealth(health);
                        event.getPlayer().setFoodLevel(hunger);
                    }catch(NullPointerException e){
                        event.getPlayer().setExp(0);
                        event.getPlayer().setLevel(0);
                        event.getPlayer().setHealth(20);
                        event.getPlayer().setFoodLevel(20);
                    }
                }
            }else {
                if (perWorldInventoriesEnabled) {
                    groupFile = new PerWorldInventoryGroupsFile(plugin);
                    fromWorld = event.getFrom().getWorld().getName();
                    fromGroup = groupFile.get().getString(fromWorld + ".group");
                    toWorld = event.getTo().getWorld().getName();
                    toGroup = groupFile.get().getString(toWorld + ".group");
                    if (toGroup == null){
                        groupFile.get().set(toWorld + ".group", toWorld);
                        groupFile.save();
                        toGroup = toWorld;
                    }
                    saveInventoryGroup(event.getPlayer(), fromGroup);
                    if (!fromGroup.equals(toGroup)) {
                        boolean perWorldInvOverride = playerSettings.get().getBoolean("perWorldInvOverride");
                        if (!perWorldInvOverride) {
                            if (visited) {
                                getInventoryGroup(event.getPlayer(), toGroup);
                            }else{
                                event.getPlayer().getInventory().clear();
                                if (perWorldStatsEnabled){
                                    event.getPlayer().setHealth(20);
                                    event.getPlayer().setFoodLevel(20);
                                    event.getPlayer().setExp(0);
                                    event.getPlayer().setLevel(0);
                                }
                            }
                        }
                        float xp = event.getPlayer().getExp();
                        int level = event.getPlayer().getLevel();
                        double health = event.getPlayer().getHealth();
                        int hunger = event.getPlayer().getFoodLevel();
                        playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), fromGroup);
                        playerInventoryFile.get().set("xp", xp);
                        playerInventoryFile.get().set("level", level);
                        playerInventoryFile.get().set("health", health);
                        playerInventoryFile.get().set("hunger", hunger);
                        playerInventoryFile.save();
                        if (perWorldStatsEnabled) {
                            try {
                                playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), toGroup);
                                String xpStr = playerInventoryFile.get().getString("xp");
                                String levelStr = playerInventoryFile.get().getString("level");
                                health = playerInventoryFile.get().getDouble("health");
                                hunger = playerInventoryFile.get().getInt("hunger");
                                if (!visited || health == 0) {
                                    hunger = 20;
                                }
                                if (!visited || health == 0) {
                                    health = 20;
                                }
                                if (!visited || xpStr == null) {
                                    xp = 0;
                                }else{
                                    xp = Float.parseFloat(xpStr);
                                }
                                if (!visited || levelStr == null) {
                                    level = 0;
                                }else{
                                    level = Integer.parseInt(levelStr);
                                }
                                event.getPlayer().setExp(xp);
                                event.getPlayer().setLevel(level);
                                event.getPlayer().setHealth(health);
                                event.getPlayer().setFoodLevel(hunger);
                            }catch(NullPointerException e){
                                event.getPlayer().setExp(0);
                                event.getPlayer().setLevel(0);
                                event.getPlayer().setHealth(20);
                                event.getPlayer().setFoodLevel(20);
                            }
                        }
                    }
                }
            }
            boolean perGroupKits = config.get().getBoolean("per-world-kit-grouping");
            if (!perGroupKits) {
                playerInventoryFile = new PlayerInventoryFile(this.plugin, event.getPlayer().getUniqueId().toString(), to.getWorld().getName());
                visited = playerInventoryFile.get().getBoolean("visited");
                if (!visited) {
                    playerInventoryFile.get().set("visited", true);
                    playerInventoryFile.save();
                    KitsFile kitsFile = new KitsFile(this.plugin);
                    kitName = kitsFile.get().getString(to.getWorld().getName() + ".starting-kit");
                    if (kitName != null) {
                        Bukkit.getScheduler().runTask(this.plugin, new GiveItemTask(kitsFile, kitName, event.getPlayer()));
                    }
                }
            } else {
                groupFile = new PerWorldInventoryGroupsFile(this.plugin);
                fromWorld = event.getFrom().getWorld().getName();
                fromGroup = groupFile.get().getString(fromWorld + ".group");
                toWorld = event.getTo().getWorld().getName();
                toGroup = groupFile.get().getString(toWorld + ".group");
                if (!fromGroup.equals(toGroup)) {
                    PlayerInventoryFile playerInventoryFileTwo = new PlayerInventoryFile(this.plugin, event.getPlayer().getUniqueId().toString(), toGroup);
                    visited = playerInventoryFileTwo.get().getBoolean("visited");
                    if (!visited) {
                        playerInventoryFileTwo.get().set("visited", true);
                        playerInventoryFileTwo.save();
                        KitsFile kitsFile = new KitsFile(this.plugin);
                        kitName = kitsFile.get().getString(toGroup + ".starting-kit");
                        if (kitName != null) {
                            Bukkit.getScheduler().runTask(this.plugin, new GiveItemTask(kitsFile, kitName, event.getPlayer()));
                        }
                    }
                }
            }
            String gameModeString = worldSettings.get().getString("worlds."+to.getWorld().getName()+".gameMode");
            GameMode mode = getGameModeValue(gameModeString);
            if (playerSettings.get().getBoolean("gameModeOverride")){
                return;
            }
            if (mode == null){
                System.out.println("[Universes] The GameMode, "+gameModeString+" is an invalid GameMode type for the world, " +to.getWorld().getName()+ ". Please change it to either survival, creative, adventure, or spectator.");
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "There was an error updating your gamemode on entering this world. As a result, you have been put into the default GameMode of Survival. Please report this to an operator and tell them to check the console.");
            }else{
                event.getPlayer().setGameMode(mode);
            }
        }
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
        playerInventory.get().set("visited", true);
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
        playerInventory.get().set("visited", true);
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

    public String itemStackToBase64(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    public ItemStack itemStackFromBase64(String data) {
        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(Base64Coder.decodeLines(data)));) {

            // Read the serialized item
            ItemStack item = (ItemStack) dataInput.readObject();

            return item;
        } catch (IOException | ClassNotFoundException ex) {
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

    public static void setCancelEvent(boolean b){
        cancelEvent = b;
    }

}
