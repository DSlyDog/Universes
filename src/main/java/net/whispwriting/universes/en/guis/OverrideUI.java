package net.whispwriting.universes.en.guis;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverrideUI {

    public static Map<Material, UIItemData> items = new HashMap<>();
    public static Inventory inv;
    public static String inventoryName;
    public static int boxes = 9;
    public static int rows = boxes * 1;
    public static int i = -1;

    public static void init(){
        inventoryName = Utils.chat("&2&lOverrides");

        inv = Bukkit.createInventory(null, rows);
    }

    public static Inventory GUI(Player p, Universes plugin){
        Inventory toReturn = Bukkit.createInventory(null, rows, inventoryName);
        PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, p.getUniqueId().toString());
        if (p.hasPermission("Universes.override.gamemode")) {
            i++;
            boolean gameModeOverride = playerSettings.get().getBoolean("gameModeOverride");
            if (!gameModeOverride) {
                Utils.createItem(inv, Material.GRASS_BLOCK, 1, i, "&bGameModeOverride", "WhispGMO", Material.GRASS_BLOCK, items, "&dClick to enable or disable.", "&cGameModeOverride is disabled.");
            } else {
                Utils.createItem(inv, Material.GRASS_BLOCK, Enchantment.MENDING, 1, i, "&bGameModeOverride", "WhispGMO", Material.GRASS_BLOCK, items, "&dClick to enable or disable.", "&2GameModeOverride is enabled.");
            }
        }

        if (p.hasPermission("Universes.override.fullworld")) {
            i++;
            boolean canJoinFullWorlds = playerSettings.get().getBoolean("canJoinFullWorlds");
            if (!canJoinFullWorlds) {
                Utils.createItem(inv, Material.BEDROCK, 1, i, "&bFullWorldOverride", "WhispFWO", Material.BEDROCK, items, "&dClick to enable or disable.", "&cFullWorldOverride is disabled.");
            } else {
                Utils.createItem(inv, Material.BEDROCK, Enchantment.MENDING, 1, i, "&bFullWorldOverride", "WhispFWO", Material.BEDROCK, items, "&dClick to enable or disable.", "&2FullWorldOverride is enabled.");
            }
        }

        if (p.hasPermission("Universes.override.flight")) {
            i++;
            boolean flightOverride = playerSettings.get().getBoolean("flightOverride");
            if (!flightOverride) {
                Utils.createItem(inv, Material.ELYTRA, 1, i, "&bFlightOverride", "WhispFW", Material.ELYTRA, items, "&dClick to enable or disable.", "&cFlightOverride is disabled.");
            } else {
                Utils.createItem(inv, Material.ELYTRA, Enchantment.MENDING, 1, i, "&bFlightOverride", "WhispFW", Material.ELYTRA, items, "&dClick to enable or disable.", "&2FlightOverride is enabled.");
            }
        }

        if (p.hasPermission("Universes.override.perworldinv")) {
            i++;
            boolean perWorldInvOverride = playerSettings.get().getBoolean("perWorldInvOverride");
            if (!perWorldInvOverride) {
                Utils.createItem(inv, Material.CRAFTING_TABLE, 1, i, "&bPerWorldInventoryOverride", "WhispPWIO", Material.CRAFTING_TABLE, items, "&dClick to enable or disable.", "&cPerWorldInventoryOverride is disabled.");
            } else {
                Utils.createItem(inv, Material.CRAFTING_TABLE, Enchantment.MENDING, 1, i, "&bPerWorldInventoryOverride", "WhispPWIO", Material.CRAFTING_TABLE, items, "&dClick to enable or disable.", "&2PerWorldInventoryOverride is enabled.");
            }
        }
        toReturn.setContents(inv.getContents());
        return toReturn;
    }

    public static void clickItem(Player p, int slot, ItemStack clicked, Inventory inventory, Universes plugin){

        if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bGameModeOverride"))){
            PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, p.getUniqueId().toString());
            boolean gameModeOverride = playerSettings.get().getBoolean("gameModeOverride");
            if (p.hasPermission("Universes.override.gamemode")) {
                if (gameModeOverride) {
                    playerSettings.get().set("gameModeOverride", false);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&cGameModeOverride is disabled"));
                    meta.setLore(lore);
                    clicked.setItemMeta(meta);
                    clicked.removeEnchantment(Enchantment.MENDING);
                    p.sendMessage(ChatColor.RED + "GameModeOverride has been disabled.");
                } else {
                    playerSettings.get().set("gameModeOverride", true);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&2GameModeOverride is enabled"));
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    clicked.setItemMeta(meta);
                    clicked.addUnsafeEnchantment(Enchantment.MENDING, 1);
                    p.sendMessage(ChatColor.DARK_GREEN + "GameModeOverride has been enabled.");
                }
            }else{
                p.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bFullWorldOverride"))){
            PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, p.getUniqueId().toString());
            boolean canJoinFullWorlds = playerSettings.get().getBoolean("canJoinFullWorlds");
            if (p.hasPermission("Universes.override.fullworld")){
                if (canJoinFullWorlds) {
                    playerSettings.get().set("canJoinFullWorlds", false);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&cFullWorldOverride is disabled"));
                    meta.setLore(lore);
                    clicked.setItemMeta(meta);
                    clicked.removeEnchantment(Enchantment.MENDING);
                    p.sendMessage(ChatColor.RED + "FullWorldOverride has been disabled.");
                } else {
                    playerSettings.get().set("canJoinFullWorlds", true);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&2FullWorldOverride is enabled"));
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    clicked.setItemMeta(meta);
                    clicked.addUnsafeEnchantment(Enchantment.MENDING, 1);
                    p.sendMessage(ChatColor.DARK_GREEN + "FullWorldOverride has been enabled.");
                }
            }else{
                p.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bFlightOverride"))){
            PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, p.getUniqueId().toString());
            boolean flightOverride = playerSettings.get().getBoolean("flightOverride");
            if (p.hasPermission("Universes.override.flight")){
                if (flightOverride) {
                    playerSettings.get().set("flightOverride", false);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&cFlightOverride is disabled"));
                    meta.setLore(lore);
                    meta.removeEnchant(Enchantment.MENDING);
                    clicked.setItemMeta(meta);
                    //p.openInventory(GUI(p, plugin));
                    p.sendMessage(ChatColor.RED + "FlightOverride has been disabled.");
                } else {
                    playerSettings.get().set("flightOverride", true);
                    playerSettings.save();
                    //p.closeInventory();
                    clicked.removeEnchantment(Enchantment.MENDING);
                    ItemMeta meta = clicked.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&2FlightOverride is enabled"));
                    meta.setLore(lore);
                    meta.addEnchant(Enchantment.MENDING, 1, true);
                    clicked.setItemMeta(meta);
                    //p.openInventory(GUI(p, plugin));
                    p.sendMessage(ChatColor.DARK_GREEN + "FlightOverride has been enabled.");
                }
            }else{
                p.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bPerWorldInventoryOverride"))){
            PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, p.getUniqueId().toString());
            boolean perWorldInvOverride = playerSettings.get().getBoolean("perWorldInvOverride");
            if (p.hasPermission("Universes.override.perworldinv")){
                if (perWorldInvOverride) {
                    playerSettings.get().set("perWorldInvOverride", false);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&cPerWorldInventoryOverride is disabled"));
                    meta.setLore(lore);
                    clicked.setItemMeta(meta);
                    clicked.removeEnchantment(Enchantment.MENDING);
                    //p.openInventory(GUI(p, plugin));
                    p.sendMessage(ChatColor.RED + "PerWorldInventoryOverride has been disabled.");
                } else {
                    playerSettings.get().set("perWorldInvOverride", true);
                    playerSettings.save();
                    //p.closeInventory();
                    ItemMeta meta = clicked.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(1);
                    lore.add(Utils.chat("&2PerWorldInventoryOverride is enabled"));
                    meta.setLore(lore);
                    clicked.setItemMeta(meta);
                    clicked.addUnsafeEnchantment(Enchantment.MENDING, 1);
                    //p.openInventory(GUI(p, plugin));
                    p.sendMessage(ChatColor.DARK_GREEN + "PerWorldInventoryOverride has been enabled.");
                }
            }else{
                p.sendMessage(ChatColor.DARK_RED + "You do not have permission to change that setting.");
            }
        }

    }

}
