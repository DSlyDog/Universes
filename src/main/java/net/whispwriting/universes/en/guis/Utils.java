package net.whispwriting.universes.en.guis;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String chat(String s){
        s = s.replace("&", "§");
        return s;
    }

    public static ItemStack createItem(Inventory inv, Material m, int amount, int inventorySlot, String displayName, String id, Material swapItem, Map<Material,UIItemData> items, String... loreStrings){
        ItemStack item;
        List<String> lore = new ArrayList<>();
        item = new ItemStack(m, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chat(displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        for (String s: loreStrings){
            lore.add(chat(s));
        }
        meta.setLocalizedName(id);
        meta.setLore(lore);
        item.setItemMeta(meta);
        UIItemData i = new UIItemData(item, id);
        items.put(m ,i);
        items.put(swapItem, i);
        inv.setItem(inventorySlot, item);
        return item;
    }

    public static ItemStack createItem(Inventory inv, Material m, Enchantment ench, int amount, int inventorySlot, String displayName, String id, Material swapItem, Map<Material,UIItemData> items, String... loreStrings){
        ItemStack item;
        List<String> lore = new ArrayList<>();
        item = new ItemStack(m, amount);
        item.addUnsafeEnchantment(ench, 2);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(chat(displayName));
        for (String s: loreStrings){
            lore.add(chat(s));
        }
        meta.setLocalizedName(id);
        meta.setLore(lore);
        item.setItemMeta(meta);
        UIItemData i = new UIItemData(item, id);
        items.put(m ,i);
        items.put(swapItem, i);
        inv.setItem(inventorySlot, item);
        return item;
    }

    public static ItemStack createItem(Inventory inv, Material m, int amount, int inventorySlot, String displayName, String id, Material swapItem, Map<Material,UIItemData> items, List<String> loreStrings){
        ItemStack item;
        List<String> lore = new ArrayList<>();
        item = new ItemStack(m, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chat(displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        for (String s: loreStrings){
            lore.add(chat(s));
        }
        meta.setLocalizedName(id);
        meta.setLore(lore);
        item.setItemMeta(meta);
        UIItemData i = new UIItemData(item, id);
        items.put(m ,i);
        items.put(swapItem, i);
        inv.setItem(inventorySlot, item);
        return item;
    }

    public static ItemStack createItem(Inventory inv, Material m, Enchantment ench, int amount, int inventorySlot, String displayName, String id, Material swapItem, Map<Material,UIItemData> items, List<String> loreStrings){
        ItemStack item;
        List<String> lore = new ArrayList<>();
        item = new ItemStack(m, amount);
        item.addUnsafeEnchantment(ench, 2);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(chat(displayName));
        for (String s: loreStrings){
            lore.add(chat(s));
        }
        meta.setLocalizedName(id);
        meta.setLore(lore);
        item.setItemMeta(meta);
        UIItemData i = new UIItemData(item, id);
        items.put(m ,i);
        items.put(swapItem, i);
        inv.setItem(inventorySlot, item);
        return item;
    }

    public static ItemStack createItem(Inventory inv, Material m, int amount, int inventorySlot, String displayName, String id, List<Material> swapItems, Map<Material,UIItemData> items, String... loreStrings){
        ItemStack item = new ItemStack(m ,amount);
        List<String> lore = new ArrayList<>();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chat(displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        for (String s : loreStrings){
            lore.add(chat(s));
        }
        meta.setLore(lore);
        meta.setLocalizedName(id);
        item.setItemMeta(meta);
        UIItemData i = new UIItemData(item, id);
        items.put(m, i);
        for (Material swapItem : swapItems) {
            UIItemData iz = new UIItemData(item, id);
            items.put(swapItem, iz);
        }
        inv.setItem(inventorySlot, item);
        return item;
    }

}
