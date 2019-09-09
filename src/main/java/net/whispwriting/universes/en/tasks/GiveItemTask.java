package net.whispwriting.universes.en.tasks;

import net.whispwriting.universes.en.files.KitsFile;
import net.whispwriting.universes.en.guis.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiveItemTask implements Runnable {

    private KitsFile kitsFile;
    private String kitName;
    private Player player;

    public GiveItemTask(KitsFile kf, String kn, Player p){
        player = p;
        kitName = kn;
        kitsFile = kf;
    }

    @Override
    public void run() {
        String message = kitsFile.get().getString(kitName+".UIItem-selected-message");
        player.sendMessage(Utils.chat(message));
        List<String> items = kitsFile.get().getStringList(kitName+".items");
        for (String item : items){
            List<String> enchantmentNames = kitsFile.get().getStringList(kitName+".enchantments."+item);
            int amount = kitsFile.get().getInt(kitName+".item-amount."+item);
            item = item.toUpperCase();
            Material m = Material.getMaterial(item);
            ItemStack itemStack = new ItemStack(m, amount);
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (enchantmentNames.size() != 0){
                item = item.toLowerCase();
                for (String enchantmentName : enchantmentNames){
                    String[] enchNameParts = enchantmentName.split(" ");
                    String enchNameFinal = enchNameParts[0];
                    int level = Integer.parseInt(enchNameParts[1]);
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchNameFinal));
                    meta.addEnchant(enchantment, level, true);
                }
            }
            List<String> itemDescription = kitsFile.get().getStringList(kitName+".item-description."+item);
            if (itemDescription.size() != 0) {
                for (String part : itemDescription) {
                    lore.add(Utils.chat(part));
                }
            }
            meta.setLore(lore);
            String displayName = kitsFile.get().getString(kitName+".item-name."+item);
            if (displayName != null) {
                meta.setDisplayName(Utils.chat(displayName));
            }
            boolean unbreakable = kitsFile.get().getBoolean(kitName+".item-unbreakable."+item);
            meta.setUnbreakable(unbreakable);
            itemStack.setItemMeta(meta);
            player.getInventory().addItem(itemStack);
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
