package net.whispwriting.universes.en.events;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.*;
import net.whispwriting.universes.en.tasks.GiveItemTask;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeleportEvent implements Listener {

    private PlayerSettingsFile playerSettings;
    private Universes plugin;
    private KitsFile kitsFile;

    public TeleportEvent(PlayerSettingsFile ps, Universes pl, KitsFile kf){
        playerSettings = ps;
        plugin = pl;
        kitsFile = kf;
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event){
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
                        getInventory(event.getPlayer(), to.getWorld().getName());
                    }
                }
                boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
                if (perWorldStatsEnabled) {
                    float xp = event.getPlayer().getExp();
                    double health = event.getPlayer().getHealth();
                    int hunger = event.getPlayer().getFoodLevel();
                    playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), from.getWorld().getName());
                    playerInventoryFile.get().set("xp", xp);
                    playerInventoryFile.get().set("health", health);
                    playerInventoryFile.get().set("hunger", hunger);
                    playerInventoryFile.save();

                    playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), to.getWorld().getName());
                    xp = (float) playerInventoryFile.get().getDouble("xp");
                    health = playerInventoryFile.get().getDouble("health");
                    if (!visited || health == 0) {
                        health = 20;
                    }
                    hunger = playerInventoryFile.get().getInt("hunger");
                    if (!visited) {
                        hunger = 20;
                    }
                    event.getPlayer().setExp(xp);
                    event.getPlayer().setHealth(health);
                    event.getPlayer().setFoodLevel(hunger);
                }
            }else {
                if (perWorldInventoriesEnabled) {
                    groupFile = new PerWorldInventoryGroupsFile(plugin);
                    fromWorld = event.getFrom().getWorld().getName();
                    fromGroup = groupFile.get().getString(fromWorld + ".group");
                    toWorld = event.getTo().getWorld().getName();
                    toGroup = groupFile.get().getString(toWorld + ".group");
                    if (!fromGroup.equals(toGroup)) {
                        saveInventoryGroup(event.getPlayer(), fromGroup);
                        boolean perWorldInvOverride = playerSettings.get().getBoolean("perWorldInvOverride");
                        if (!perWorldInvOverride) {
                            getInventoryGroup(event.getPlayer(), toGroup);
                        }
                        boolean perWorldStatsEnabled = config.get().getBoolean(("per-world-stats"));
                        if (perWorldStatsEnabled) {
                            float xp = event.getPlayer().getExp();
                            double health = event.getPlayer().getHealth();
                            int hunger = event.getPlayer().getFoodLevel();
                            playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), fromGroup);
                            playerInventoryFile.get().set("xp", xp);
                            playerInventoryFile.get().set("health", health);
                            playerInventoryFile.get().set("hunger", hunger);
                            playerInventoryFile.save();

                            playerInventoryFile = new PlayerInventoryFile(plugin, event.getPlayer().getUniqueId().toString(), toGroup);
                            xp = (float) playerInventoryFile.get().getDouble("xp");
                            health = playerInventoryFile.get().getDouble("health");
                            if (!visited || health == 0) {
                                health = 20;
                            }
                            hunger = playerInventoryFile.get().getInt("hunger");
                            if (!visited) {
                                hunger = 20;
                            }
                            event.getPlayer().setExp(xp);
                            event.getPlayer().setHealth(health);
                            event.getPlayer().setFoodLevel(hunger);
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
        ItemStack[] ecItems = new ItemStack[27];
        player.getEnderChest().clear();
        int x = 0;
        while (x < 27){
            String mat = enderChestInventory.get().getString("slot" + x + ".type");
            int amount = enderChestInventory.get().getInt("slot" + x + ".amount");
            short durability = (short) enderChestInventory.get().getInt("slot" + x + ".durability");
            String displayName = enderChestInventory.get().getString("slot" + x + ".name");
            List<String> enchantments = enderChestInventory.get().getStringList("slot" + x + ".enchantments");
            List<String> lore = enderChestInventory.get().getStringList("slot" + x + ".lore");
            boolean unbreakable = enderChestInventory.get().getBoolean("slot" + x + ".unbreakable");
            boolean allowPlayerEnchant = enderChestInventory.get().getBoolean("slot" + x + ".allowPlayerEnchant");
            Material m = Material.getMaterial(mat);
            if (m != null) {
                ItemStack item = new ItemStack(m, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    List<String> metaLore = new ArrayList<>();
                    for (String ench : enchantments) {
                        String[] enchSplit = ench.split(" ");
                        int level = Integer.parseInt(enchSplit[1]);
                        enchSplit[1] = romanNumeral(enchSplit[1]);
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                        meta.addEnchant(enchantment, level, true);
                    }
                    for (String loreString : lore) {
                        loreString = loreString.replace("&", "§");
                        metaLore.add(loreString);
                    }
                    if (!allowPlayerEnchant) {
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.setLore(metaLore);
                    }
                    try
                    {
                        if (!displayName.equals(""))
                        {
                            meta.setDisplayName(displayName);
                        }
                    }catch(NullPointerException e){
                        //do nothing
                    }
                    if (unbreakable)
                    {
                        meta.setUnbreakable(unbreakable);
                    }
                    item.setItemMeta(meta);
                    item.setDurability(durability);
                }
                ecItems[x] = item;
            }
            x++;
        }
        player.getEnderChest().setContents(ecItems);
        if (playerInventory.get().getString("item_slot0.type") == null){
            player.getInventory().clear();
            return;
        }
        ItemStack[] items = player.getInventory().getContents();
        ItemStack[] inventoryItems = new ItemStack[36];
        ItemStack[] armorItems = new ItemStack[4];
        player.getInventory().clear();
        int count = 0;
        for (int i=0; i<items.length; i++){
            try {
                if (i < 36) {
                    String type = playerInventory.get().getString("item_slot" + i + ".type");
                    int amount = playerInventory.get().getInt("item_slot" + i + ".amount");
                    short durability = (short) playerInventory.get().getInt("item_slot" + i + ".durability");
                    String displayName = playerInventory.get().getString("item_slot" + i + ".name");
                    List<String> enchantments = playerInventory.get().getStringList("item_slot" + i + ".enchantments");
                    List<String> lore = playerInventory.get().getStringList("item_slot" + i + ".lore");
                    boolean unbreakable = playerInventory.get().getBoolean("item_slot" + i + ".unbreakable");
                    boolean allowPlayerEnchant = playerInventory.get().getBoolean("item_slot" + i + ".allowPlayerEnchant");
                    Material material = Material.getMaterial(type);
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> metaLore = new ArrayList<>();
                        for (String ench : enchantments) {
                            String[] enchSplit = ench.split(" ");
                            int level = Integer.parseInt(enchSplit[1]);
                            enchSplit[1] = romanNumeral(enchSplit[1]);
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                            meta.addEnchant(enchantment, level, true);
                        }
                        for (String loreString : lore) {
                            loreString = loreString.replace("&", "§");
                            metaLore.add(loreString);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            meta.setLore(metaLore);
                        }
                        try
                        {
                            if (!displayName.equals(""))
                            {
                                meta.setDisplayName(displayName);
                            }
                        }catch(NullPointerException e){
                            //do nothing
                        }
                        if (unbreakable)
                        {
                            meta.setUnbreakable(unbreakable);
                        }
                        item.setItemMeta(meta);
                        item.setDurability(durability);
                    }
                    inventoryItems[i] = item;
                } else {
                    String type = playerInventory.get().getString("armor_slot" + count + ".type");
                    String displayName = playerInventory.get().getString("armor_slot" + count + ".name");
                    short durability = (short) playerInventory.get().getInt("armor_slot" + count + ".durability");
                    List<String> enchantments = playerInventory.get().getStringList("armor_slot" + count + ".enchantments");
                    List<String> lore = playerInventory.get().getStringList("armor_slot" + count + ".lore");
                    boolean unbreakable = playerInventory.get().getBoolean("armor_slot" + count + ".unbreakable");
                    boolean allowPlayerEnchant = playerInventory.get().getBoolean("armor_slot" + count + ".allowPlayerEnchant");
                    Material material = Material.getMaterial(type);
                    ItemStack item = new ItemStack(material, 1);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> metaLore = new ArrayList<>();
                        for (String ench : enchantments) {
                            String[] enchSplit = ench.split(" ");
                            int level = Integer.parseInt(enchSplit[1]);
                            enchSplit[1] = romanNumeral(enchSplit[1]);
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                            meta.addEnchant(enchantment, level, true);
                        }
                        for (String loreString : lore) {
                            loreString = loreString.replace("&", "§");
                            metaLore.add(loreString);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            meta.setLore(metaLore);
                        }
                        try
                        {
                            if (!displayName.equals(""))
                            {
                                meta.setDisplayName(displayName);
                            }
                        }catch(NullPointerException e){
                            //do nothing
                        }
                        if (unbreakable)
                        {
                            meta.setUnbreakable(unbreakable);
                        }
                        item.setDurability(durability);
                        item.setItemMeta(meta);
                    }
                    if (count < 4) {
                        armorItems[count] = item;
                        count++;
                    }
                }
            }catch(IllegalArgumentException e){
                if (i < 36) {
                    ItemStack item = new ItemStack(Material.AIR);
                    inventoryItems[i] = item;
                }else if (i < 40){
                    ItemStack item = new ItemStack(Material.AIR);
                    armorItems[count] = item;
                    count++;
                }
            }
        }
        player.getInventory().setContents(inventoryItems);
        player.getInventory().setArmorContents(armorItems);
        player.updateInventory();
        String type = playerInventory.get().getString("offhand_slot.type");
        int amount = playerInventory.get().getInt("offhand_slot.amount");
        String displayName = playerInventory.get().getString("offhand_slot.name");
        short durability = (short) playerInventory.get().getInt("offhand_slot.durability");
        List<String> enchantments = playerInventory.get().getStringList("offhand_slot.enchantments");
        List<String> lore = playerInventory.get().getStringList("offhand_slot.lore");
        boolean unbreakable = playerInventory.get().getBoolean("offhand_slot.unbreakable");
        boolean allowPlayerEnchant = playerInventory.get().getBoolean("offhand_slot.allowPlayerEnchant");
        if (!type.equals("AIR")){
            ItemStack item = new ItemStack(Material.getMaterial(type), amount);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> metaLore = new ArrayList<>();
                for (String ench : enchantments) {
                    String[] enchSplit = ench.split(" ");
                    int level = Integer.parseInt(enchSplit[1]);
                    enchSplit[1] = romanNumeral(enchSplit[1]);
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                    meta.addEnchant(enchantment, level, true);
                }
                for (String loreString : lore) {
                    loreString = loreString.replace("&", "§");
                    metaLore.add(loreString);
                }
                if (!allowPlayerEnchant) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if (!allowPlayerEnchant) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.setLore(metaLore);
                }
                try
                {
                    if (!displayName.equals(""))
                    {
                        meta.setDisplayName(displayName);
                    }
                }catch(NullPointerException e){
                    //do nothing
                }
                if (unbreakable)
                {
                    meta.setUnbreakable(unbreakable);
                }
                item.setItemMeta(meta);
                item.setDurability(durability);
            }
            player.getInventory().setItemInOffHand(item);
        }
        player.updateInventory();
    }

    private void getInventoryGroup(Player player, String group) {
        String uuid = player.getUniqueId().toString();
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, group);
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, group);
        ItemStack[] ecItems = new ItemStack[27];
        player.getEnderChest().clear();
        player.getInventory().clear();
        int x = 0;
        while (x < 27){
            String mat = playerInventory.get().getString("slot" + x + ".type");
            int amount = playerInventory.get().getInt("slot" + x + ".amount");
            short durability = (short) playerInventory.get().getInt("slot" + x + ".durability");
            String displayName = playerInventory.get().getString("slot" + x + ".name");
            List<String> enchantments = playerInventory.get().getStringList("slot" + x + ".enchantments");
            List<String> lore = playerInventory.get().getStringList("slot" + x + ".lore");
            boolean unbreakable = playerInventory.get().getBoolean("slot" + x + ".unbreakable");
            boolean allowPlayerEnchant = playerInventory.get().getBoolean("slot" + x + ".allowPlayerEnchant");
            Material m = Material.getMaterial(mat);
            if (m != null) {
                ItemStack item = new ItemStack(m, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    List<String> metaLore = new ArrayList<>();
                    for (String ench : enchantments) {
                        String[] enchSplit = ench.split(" ");
                        int level = Integer.parseInt(enchSplit[1]);
                        enchSplit[1] = romanNumeral(enchSplit[1]);
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                        meta.addEnchant(enchantment, level, true);
                    }
                    for (String loreString : lore) {
                        loreString = loreString.replace("&", "§");
                        metaLore.add(loreString);
                    }
                    if (!allowPlayerEnchant) {
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.setLore(metaLore);
                    }
                    try
                    {
                        if (!displayName.equals(""))
                        {
                            meta.setDisplayName(displayName);
                        }
                    }catch(NullPointerException e){
                        //do nothing
                    }
                    if (unbreakable)
                    {
                        meta.setUnbreakable(unbreakable);
                    }
                    item.setItemMeta(meta);
                    item.setDurability(durability);
                }
                ecItems[x] = item;
            }
            x++;
        }
        player.getEnderChest().setContents(ecItems);
        if (playerInventory.get().getString("item_slot0.type") == null){
            player.getInventory().clear();
            return;
        }
        ItemStack[] items = player.getInventory().getContents();
        ItemStack[] inventoryItems = new ItemStack[36];
        ItemStack[] armorItems = new ItemStack[4];
        player.getInventory().clear();
        int count = 0;
        for (int i=0; i<items.length; i++){
            try {
                if (i < 36) {
                    String type = playerInventory.get().getString("item_slot" + i + ".type");
                    int amount = playerInventory.get().getInt("item_slot" + i + ".amount");
                    short durability = (short) playerInventory.get().getInt("item_slot" + i + ".durability");
                    String displayName = playerInventory.get().getString("item_slot" + i + ".name");
                    List<String> enchantments = playerInventory.get().getStringList("item_slot" + i + ".enchantments");
                    List<String> lore = playerInventory.get().getStringList("item_slot" + i + ".lore");
                    boolean unbreakable = playerInventory.get().getBoolean("item_slot" + i + ".unbreakable");
                    boolean allowPlayerEnchant = playerInventory.get().getBoolean("item_slot" + i + ".allowPlayerEnchant");
                    Material material = Material.getMaterial(type);
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> metaLore = new ArrayList<>();
                        for (String ench : enchantments) {
                            String[] enchSplit = ench.split(" ");
                            int level = Integer.parseInt(enchSplit[1]);
                            enchSplit[1] = romanNumeral(enchSplit[1]);
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                            meta.addEnchant(enchantment, level, true);
                        }
                        for (String loreString : lore) {
                            loreString = loreString.replace("&", "§");
                            metaLore.add(loreString);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            meta.setLore(metaLore);
                        }
                        try
                        {
                            if (!displayName.equals(""))
                            {
                                meta.setDisplayName(displayName);
                            }
                        }catch(NullPointerException e){
                            //do nothing
                        }
                        if (unbreakable)
                        {
                            meta.setUnbreakable(unbreakable);
                        }
                        item.setItemMeta(meta);
                        item.setDurability(durability);
                    }
                    inventoryItems[i] = item;
                } else {
                    String type = playerInventory.get().getString("armor_slot" + count + ".type");
                    String displayName = playerInventory.get().getString("armor_slot" + count + ".name");
                    short durability = (short) playerInventory.get().getInt("armor_slot" + count + ".durability");
                    List<String> enchantments = playerInventory.get().getStringList("armor_slot" + count + ".enchantments");
                    List<String> lore = playerInventory.get().getStringList("armor_slot" + count + ".lore");
                    boolean unbreakable = playerInventory.get().getBoolean("armor_slot" + count + ".unbreakable");
                    boolean allowPlayerEnchant = playerInventory.get().getBoolean("armor_slot" + count + ".allowPlayerEnchant");
                    Material material = Material.getMaterial(type);
                    ItemStack item = new ItemStack(material, 1);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> metaLore = new ArrayList<>();
                        for (String ench : enchantments) {
                            String[] enchSplit = ench.split(" ");
                            int level = Integer.parseInt(enchSplit[1]);
                            enchSplit[1] = romanNumeral(enchSplit[1]);
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                            meta.addEnchant(enchantment, level, true);
                        }
                        for (String loreString : lore) {
                            loreString = loreString.replace("&", "§");
                            metaLore.add(loreString);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                        if (!allowPlayerEnchant) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            meta.setLore(metaLore);
                        }
                        try
                        {
                            if (!displayName.equals(""))
                            {
                                meta.setDisplayName(displayName);
                            }
                        }catch(NullPointerException e){
                            //do nothing
                        }
                        if (unbreakable)
                        {
                            meta.setUnbreakable(unbreakable);
                        }
                        item.setDurability(durability);
                        item.setItemMeta(meta);
                    }
                    if (count < 4) {
                        armorItems[count] = item;
                        count++;
                    }
                }
            }catch(IllegalArgumentException e){
                if (i < 36) {
                    ItemStack item = new ItemStack(Material.AIR);
                    inventoryItems[i] = item;
                }else if (i < 40){
                    ItemStack item = new ItemStack(Material.AIR);
                    armorItems[count] = item;
                    count++;
                }
            }
        }
        player.getInventory().setContents(inventoryItems);
        player.getInventory().setArmorContents(armorItems);
        player.updateInventory();
        String type = playerInventory.get().getString("offhand_slot.type");
        int amount = playerInventory.get().getInt("offhand_slot.amount");
        String displayName = playerInventory.get().getString("offhand_slot.name");
        short durability = (short) playerInventory.get().getInt("offhand_slot.durability");
        List<String> enchantments = playerInventory.get().getStringList("offhand_slot.enchantments");
        List<String> lore = playerInventory.get().getStringList("offhand_slot.lore");
        boolean unbreakable = playerInventory.get().getBoolean("offhand_slot.unbreakable");
        boolean allowPlayerEnchant = playerInventory.get().getBoolean("offhand_slot.allowPlayerEnchant");
        if (!type.equals("AIR")){
            ItemStack item = new ItemStack(Material.getMaterial(type), amount);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> metaLore = new ArrayList<>();
                for (String ench : enchantments) {
                    String[] enchSplit = ench.split(" ");
                    int level = Integer.parseInt(enchSplit[1]);
                    enchSplit[1] = romanNumeral(enchSplit[1]);
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchSplit[0]));
                    meta.addEnchant(enchantment, level, true);
                }
                for (String loreString : lore) {
                    loreString = loreString.replace("&", "§");
                    metaLore.add(loreString);
                }
                if (!allowPlayerEnchant) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if (!allowPlayerEnchant) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.setLore(metaLore);
                }
                try
                {
                    if (!displayName.equals(""))
                    {
                        meta.setDisplayName(displayName);
                    }
                }catch(NullPointerException e){
                    //do nothing
                }
                if (unbreakable)
                {
                    meta.setUnbreakable(unbreakable);
                }
                item.setItemMeta(meta);
                item.setDurability(durability);
            }
            player.getInventory().setItemInOffHand(item);
        }
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
        int count = 0;
        List<Material> armor = new ArrayList<>();
        while (x < enderChest.getContents().length){
            ItemStack itemStack = enderChest.getItem(x);
            String material;
            int amount;
            try {
                material = itemStack.getType().name();
                amount = itemStack.getAmount();
            } catch (NullPointerException e) {
                material = "AIR";
                amount = 1;
            }
            List<String> enchantmentList = new ArrayList<>();
            try {
                if (itemStack.getItemMeta().hasEnchants()) {
                    Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
                    for (Map.Entry entry : enchantments.entrySet()) {
                        Enchantment enchantment = (Enchantment) entry.getKey();
                        NamespacedKey enchNSK = enchantment.getKey();
                        String enchString = enchNSK.getKey();
                        int level = (int) entry.getValue();
                        enchantmentList.add(enchString + " " + level);
                    }
                }
            } catch (NullPointerException e) {
                // do nothing
            }
            String displayName;
            try {
                displayName = itemStack.getItemMeta().getDisplayName();
            } catch (NullPointerException e) {
                displayName = "";
            }
            List<String> lore;
            try {
                lore = itemStack.getItemMeta().getLore();
            } catch (NullPointerException e) {
                lore = new ArrayList<>();
            }
            List<String> loreAlt = new ArrayList<>();
            try {
                if (lore.size() > 0) {
                    for (int j = 0; j < lore.size(); j++) {
                        String loreString = lore.get(j);
                        loreString = loreString.replace("§", "&");
                        loreAlt.add(loreString);
                    }
                }
            }catch(NullPointerException e){
                //do nothing
            }
            boolean unbreakable;
            try {
                unbreakable = itemStack.getItemMeta().isUnbreakable();
            } catch (NullPointerException e) {
                unbreakable = false;
            }
            enderChestInventory.get().set("slot" + x + ".type", material);
            enderChestInventory.get().set("slot" + x + ".amount", amount);
            if (!displayName.equals("")) {
                enderChestInventory.get().set("slot" + x + ".name", displayName);
            }
            try {
                enderChestInventory.get().set("slot" + x + ".durability", itemStack.getDurability());
            }catch(NullPointerException e){
                enderChestInventory.get().set("slot" + x + ".durability", null);
            }
            enderChestInventory.get().set("slot" + x + ".unbreakable", unbreakable);
            if (enchantmentList.size() != 0) {
                enderChestInventory.get().set("slot" + x + ".enchantments", enchantmentList);
            } else {
                enderChestInventory.get().set("slot" + x + ".enchantments", null);
            }
            if (loreAlt.size() != 0) {
                enderChestInventory.get().set("slot" + x + ".lore", loreAlt);
            }
            enderChestInventory.save();
            x++;
        }
        enderChestInventory.save();
        while (i < player.getInventory().getContents().length){
            ItemStack itemStack = player.getInventory().getItem(i);
            String material;
            int amount;
            try {
                if (i > 35){
                    try {
                        material = itemStack.getType().name();
                        amount = itemStack.getAmount();
                        Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
                        List<String> enchantmentList = new ArrayList<>();
                        for (Map.Entry entry : enchantments.entrySet()) {
                            Enchantment enchantment = (Enchantment) entry.getKey();
                            NamespacedKey enchNSK = enchantment.getKey();
                            String enchString = enchNSK.getKey();
                            int level = (int) entry.getValue();
                            enchantmentList.add(enchString + " " + level);
                        }
                        String displayName = itemStack.getItemMeta().getDisplayName();
                        List<String> lore = itemStack.getItemMeta().getLore();
                        List<String> loreAlt = new ArrayList<>();
                        if (lore != null) {
                            for (int j = 0; j < lore.size(); j++) {
                                String loreString = lore.get(j);
                                loreString = loreString.replace("§", "&");
                                loreAlt.add(loreString);
                            }
                        }
                        boolean unbreakable = itemStack.getItemMeta().isUnbreakable();
                        playerInventory.get().set("armor_slot" + count + ".type", material);
                        playerInventory.get().set("armor_slot" + count + ".name", displayName);
                        playerInventory.get().set("armor_slot" + count + ".durability", itemStack.getDurability());
                        playerInventory.get().set("armor_slot" + count + ".unbreakable", unbreakable);
                        playerInventory.get().set("armor_slot" + count + ".enchantments", enchantmentList);
                        playerInventory.get().set("armor_slot" + count + ".lore", loreAlt);
                    }catch(NullPointerException j){
                        playerInventory.get().set("armor_slot" + count + ".type", "AIR");
                        playerInventory.get().set("armor_slot" + count + ".name", null);
                        playerInventory.get().set("armor_slot" + count + ".durability", null);
                        playerInventory.get().set("armor_slot" + count + ".unbreakable", null);
                        playerInventory.get().set("armor_slot" + count + ".enchantments", null);
                        playerInventory.get().set("armor_slot" + count + ".lore", null);
                    }finally {
                        count++;
                        playerInventory.save();
                    }
                }else{
                    material = itemStack.getType().name();
                    amount = itemStack.getAmount();
                    Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
                    List<String> enchantmentList = new ArrayList<>();
                    for (Map.Entry entry : enchantments.entrySet()){
                        Enchantment enchantment = (Enchantment) entry.getKey();
                        NamespacedKey enchNSK = enchantment.getKey();
                        String enchString = enchNSK.getKey();
                        int level = (int) entry.getValue();
                        enchantmentList.add(enchString + " " + level);
                    }
                    String displayName = itemStack.getItemMeta().getDisplayName();
                    List<String> lore = itemStack.getItemMeta().getLore();
                    List<String> loreAlt = new ArrayList<>();
                    if (lore != null) {
                        for (int j = 0; j < lore.size(); j++) {
                            String loreString = lore.get(j);
                            loreString = loreString.replace("§", "&");
                            loreAlt.add(loreString);
                        }
                    }
                    boolean unbreakable = itemStack.getItemMeta().isUnbreakable();
                    playerInventory.get().set("item_slot"+i+".type", material);
                    playerInventory.get().set("item_slot"+i+".amount", amount);
                    if (!displayName.equals("")) {
                        playerInventory.get().set("item_slot" + i + ".name", displayName);
                    }
                    playerInventory.get().set("item_slot" + i + ".durability", itemStack.getDurability());
                    playerInventory.get().set("item_slot"+ i +".unbreakable", unbreakable);
                    if (enchantmentList.size() != 0) {
                        playerInventory.get().set("item_slot" + i + ".enchantments", enchantmentList);
                    }else{
                        playerInventory.get().set("item_slot" + i + ".enchantments", null);
                    }
                    if (loreAlt.size() != 0) {
                        playerInventory.get().set("item_slot" + i + ".lore", loreAlt);
                    }
                    playerInventory.save();
                }
                i++;
            }catch(NullPointerException e){
                if (i < 40) {
                    material = "AIR";
                    amount = 0;
                    playerInventory.get().set("item_slot" + i + ".type", "AIR");
                    playerInventory.get().set("item_slot" + i + ".name", null);
                    playerInventory.get().set("item_slot" + i + ".durability", null);
                    playerInventory.get().set("item_slot" + i + ".unbreakable", null);
                    playerInventory.get().set("item_slot" + i + ".enchantments", null);
                    playerInventory.get().set("item_slot" + i + ".lore", null);
                    playerInventory.save();
                }
                i++;
            }
        }
        String material = player.getInventory().getItemInOffHand().getType().name();
        int amount = player.getInventory().getItemInOffHand().getAmount();
        if (player.getInventory().getItemInOffHand().getItemMeta() != null) {
            Map<Enchantment, Integer> enchantments = player.getInventory().getItemInOffHand().getItemMeta().getEnchants();
            List<String> enchantmentList = new ArrayList<>();
            for (Map.Entry entry : enchantments.entrySet()) {
                Enchantment enchantment = (Enchantment) entry.getKey();
                NamespacedKey enchNSK = enchantment.getKey();
                String enchString = enchNSK.getKey();
                int level = (int) entry.getValue();
                enchantmentList.add(enchString + " " + level);
            }
            String displayName = player.getInventory().getItemInOffHand().getItemMeta().getDisplayName();
            List<String> lore = player.getInventory().getItemInOffHand().getItemMeta().getLore();
            List<String> loreAlt = new ArrayList<>();
            if (lore != null) {
                for (int j = 0; j < lore.size(); j++) {
                    String loreString = lore.get(j);
                    loreString = loreString.replace("§", "&");
                    loreAlt.add(loreString);
                }
            }
            boolean unbreakable = player.getInventory().getItemInOffHand().getItemMeta().isUnbreakable();
            if (!displayName.equals("")) {
                playerInventory.get().set("offhand_slot.name", displayName);
            }
            playerInventory.get().set("offhand_slot.durability", player.getInventory().getItemInOffHand().getDurability());
            playerInventory.get().set("offhand_slot.unbreakable", unbreakable);
            if (enchantmentList.size() != 0) {
                playerInventory.get().set("offhand_slot.enchantments", enchantmentList);
            }else{
                playerInventory.get().set("offhand_slot.enchantments", null);
            }
            if (loreAlt.size() != 0) {
                playerInventory.get().set("offhand_slot.lore", loreAlt);
            }
        }
        playerInventory.get().set("offhand_slot.type", material);
        playerInventory.get().set("offhand_slot.amount", amount);
        playerInventory.save();
        playerInventory.reload();
    }

    private void saveInventory(Player player, String world) {
        String uuid = player.getUniqueId().toString();
        Inventory inventory = player.getInventory();
        Inventory enderChest = player.getEnderChest();
        EnderChestInventoryFile enderChestInventory = new EnderChestInventoryFile(plugin, uuid, world);
        PlayerInventoryFile playerInventory = new PlayerInventoryFile(plugin, uuid, world);
        int i=0;
        int x = 0;
        int count = 0;
        List<Material> armor = new ArrayList<>();
        while (x < enderChest.getContents().length){
            while (x < enderChest.getContents().length){
                ItemStack itemStack = enderChest.getItem(x);
                String material;
                int amount;
                try {
                    material = itemStack.getType().name();
                    amount = itemStack.getAmount();
                }catch(NullPointerException e){
                    material = "AIR";
                    amount = 1;
                }
                List<String> enchantmentList = new ArrayList<>();
                try {
                    if (itemStack.getItemMeta().hasEnchants()) {
                        Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
                        for (Map.Entry entry : enchantments.entrySet()) {
                            Enchantment enchantment = (Enchantment) entry.getKey();
                            NamespacedKey enchNSK = enchantment.getKey();
                            String enchString = enchNSK.getKey();
                            int level = (int) entry.getValue();
                            enchantmentList.add(enchString + " " + level);
                        }
                    }
                }catch(NullPointerException e){
                    // do nothing
                }
                String displayName;
                try{
                    displayName = itemStack.getItemMeta().getDisplayName();
                }catch(NullPointerException e){
                    displayName = "";
                }
                List<String> lore;
                try {
                    lore = itemStack.getItemMeta().getLore();
                }catch(NullPointerException e){
                    lore = new ArrayList<>();
                }
                List<String> loreAlt = new ArrayList<>();
                try {
                    if (lore.size() > 0) {
                        for (int j = 0; j < lore.size(); j++) {
                            String loreString = lore.get(j);
                            loreString = loreString.replace("§", "&");
                            loreAlt.add(loreString);
                        }
                    }
                }catch(NullPointerException e){
                    // do nothing
                }
                boolean unbreakable;
                try {
                    unbreakable = itemStack.getItemMeta().isUnbreakable();
                }catch(NullPointerException e){
                    unbreakable = false;
                }
                enderChestInventory.get().set("slot"+x+".type", material);
                enderChestInventory.get().set("slot"+x+".amount", amount);
                if (!displayName.equals("")) {
                    enderChestInventory.get().set("slot" + x + ".name", displayName);
                }
                try {
                    enderChestInventory.get().set("slot" + x + ".durability", itemStack.getDurability());
                }catch(NullPointerException e){
                    enderChestInventory.get().set("slot" + x + ".durability", null);
                }
                enderChestInventory.get().set("slot"+ x +".unbreakable", unbreakable);
                if (enchantmentList.size() != 0) {
                    enderChestInventory.get().set("slot" + x + ".enchantments", enchantmentList);
                }else{
                    enderChestInventory.get().set("slot" + x + ".enchantments", null);
                }
                if (loreAlt.size() != 0) {
                    enderChestInventory.get().set("slot" + x + ".lore", loreAlt);
                }
                enderChestInventory.save();
                x++;
            }
            enderChestInventory.save();
        }
        enderChestInventory.save();
        while (i < player.getInventory().getContents().length){
            ItemStack itemStack = player.getInventory().getItem(i);
            String material;
            int amount;
            try {
                if (i > 35){
                    try {
                        material = itemStack.getType().name();
                        amount = itemStack.getAmount();
                        Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
                        List<String> enchantmentList = new ArrayList<>();
                        for (Map.Entry entry : enchantments.entrySet()) {
                            Enchantment enchantment = (Enchantment) entry.getKey();
                            NamespacedKey enchNSK = enchantment.getKey();
                            String enchString = enchNSK.getKey();
                            int level = (int) entry.getValue();
                            enchantmentList.add(enchString + " " + level);
                        }
                        String displayName = itemStack.getItemMeta().getDisplayName();
                        List<String> lore = itemStack.getItemMeta().getLore();
                        List<String> loreAlt = new ArrayList<>();
                        if (lore != null) {
                            for (int j = 0; j < lore.size(); j++) {
                                String loreString = lore.get(j);
                                loreString = loreString.replace("§", "&");
                                loreAlt.add(loreString);
                            }
                        }
                        boolean unbreakable = itemStack.getItemMeta().isUnbreakable();
                        playerInventory.get().set("armor_slot" + count + ".type", material);
                        playerInventory.get().set("armor_slot" + count + ".name", displayName);
                        playerInventory.get().set("armor_slot" + count + ".durability", itemStack.getDurability());
                        playerInventory.get().set("armor_slot" + count + ".unbreakable", unbreakable);
                        playerInventory.get().set("armor_slot" + count + ".enchantments", enchantmentList);
                        playerInventory.get().set("armor_slot" + count + ".lore", loreAlt);
                    }catch(NullPointerException j){
                        playerInventory.get().set("armor_slot" + count + ".type", "AIR");
                        playerInventory.get().set("armor_slot" + count + ".name", null);
                        playerInventory.get().set("armor_slot" + count + ".durability", null);
                        playerInventory.get().set("armor_slot" + count + ".unbreakable", null);
                        playerInventory.get().set("armor_slot" + count + ".enchantments", null);
                        playerInventory.get().set("armor_slot" + count + ".lore", null);
                    }finally {
                        count++;
                        playerInventory.save();
                    }
                }else{
                    material = itemStack.getType().name();
                    amount = itemStack.getAmount();
                    Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
                    List<String> enchantmentList = new ArrayList<>();
                    for (Map.Entry entry : enchantments.entrySet()){
                        Enchantment enchantment = (Enchantment) entry.getKey();
                        NamespacedKey enchNSK = enchantment.getKey();
                        String enchString = enchNSK.getKey();
                        int level = (int) entry.getValue();
                        enchantmentList.add(enchString + " " + level);
                    }
                    String displayName = itemStack.getItemMeta().getDisplayName();
                    List<String> lore = itemStack.getItemMeta().getLore();
                    List<String> loreAlt = new ArrayList<>();
                    if (lore != null) {
                        for (int j = 0; j < lore.size(); j++) {
                            String loreString = lore.get(j);
                            loreString = loreString.replace("§", "&");
                            loreAlt.add(loreString);
                        }
                    }
                    loreAlt.add(itemStack.getItemMeta().getLocalizedName());
                    boolean unbreakable = itemStack.getItemMeta().isUnbreakable();
                    playerInventory.get().set("item_slot"+i+".type", material);
                    playerInventory.get().set("item_slot"+i+".amount", amount);
                    if (!displayName.equals("")) {
                        playerInventory.get().set("item_slot" + i + ".name", displayName);
                    }
                    playerInventory.get().set("item_slot" + i + ".durability", itemStack.getDurability());
                    playerInventory.get().set("item_slot"+ i +".unbreakable", unbreakable);
                    if (enchantmentList.size() != 0) {
                        playerInventory.get().set("item_slot" + i + ".enchantments", enchantmentList);
                    }else{
                        playerInventory.get().set("item_slot" + i + ".enchantments", null);
                    }
                    if (loreAlt.size() != 0) {
                        playerInventory.get().set("item_slot" + i + ".lore", loreAlt);
                    }
                    playerInventory.save();
                }
                i++;
            }catch(NullPointerException e){
                if (i < 40) {
                    material = "AIR";
                    amount = 0;
                    playerInventory.get().set("item_slot" + i + ".type", "AIR");
                    playerInventory.get().set("item_slot" + i + ".name", null);
                    playerInventory.get().set("item_slot" + i + ".durability", null);
                    playerInventory.get().set("item_slot" + i + ".unbreakable", null);
                    playerInventory.get().set("item_slot" + i + ".enchantments", null);
                    playerInventory.get().set("item_slot" + i + ".lore", null);
                    playerInventory.save();
                }
                i++;
            }
        }
        String material = player.getInventory().getItemInOffHand().getType().name();
        int amount = player.getInventory().getItemInOffHand().getAmount();
        if (player.getInventory().getItemInOffHand().getItemMeta() != null) {
            Map<Enchantment, Integer> enchantments = player.getInventory().getItemInOffHand().getItemMeta().getEnchants();
            List<String> enchantmentList = new ArrayList<>();
            for (Map.Entry entry : enchantments.entrySet()) {
                Enchantment enchantment = (Enchantment) entry.getKey();
                NamespacedKey enchNSK = enchantment.getKey();
                String enchString = enchNSK.getKey();
                int level = (int) entry.getValue();
                enchantmentList.add(enchString + " " + level);
            }
            String displayName = player.getInventory().getItemInOffHand().getItemMeta().getDisplayName();
            List<String> lore = player.getInventory().getItemInOffHand().getItemMeta().getLore();
            List<String> loreAlt = new ArrayList<>();
            if (lore != null) {
                for (int j = 0; j < lore.size(); j++) {
                    String loreString = lore.get(j);
                    loreString = loreString.replace("§", "&");
                    loreAlt.add(loreString);
                }
            }
            boolean unbreakable = player.getInventory().getItemInOffHand().getItemMeta().isUnbreakable();
            if (!displayName.equals("")) {
                playerInventory.get().set("offhand_slot.name", displayName);
            }
            playerInventory.get().set("offhand_slot.durability", player.getInventory().getItemInOffHand().getDurability());
            playerInventory.get().set("offhand_slot.unbreakable", unbreakable);
            if (enchantmentList.size() != 0) {
                playerInventory.get().set("offhand_slot.enchantments", enchantmentList);
            }else{
                playerInventory.get().set("offhand_slot.enchantments", null);
            }
            if (loreAlt.size() != 0) {
                playerInventory.get().set("offhand_slot.lore", loreAlt);
            }
        }
        playerInventory.get().set("offhand_slot.type", material);
        playerInventory.get().set("offhand_slot.amount", amount);
        playerInventory.save();
        playerInventory.reload();
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

    public static String romanNumeral(String level){
        String rn = level;
        switch (level){
            case "1":
                rn = "I";
                break;
            case "2":
                rn = "II";
                break;
            case "3":
                rn = "III";
                break;
            case "4":
                rn = "IV";
                break;
            case "5":
                rn = "V";
                break;
            case "6":
                rn = "VI";
                break;
            case "7":
                rn = "VII";
                break;
            case "8":
                rn = "VIII";
                break;
            case "9":
                rn = "IX";
                break;
            case "10":
                rn = "X";
                break;
        }
        return rn;
    }

}
