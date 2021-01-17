package net.whispwriting.universes.en.guis;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.KitsFile;
import net.whispwriting.universes.en.files.PlayerInventoryFile;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import net.whispwriting.universes.en.tasks.GiveItemTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitUI {

    public static Map<Material, UIItemData> items = new HashMap<>();
    public static Inventory inv;
    public static String inventoryName;
    public static int boxes = 9;
    public static int rows;
    public static KitsFile kitsFile;
    public static Universes plugin = (Universes) Bukkit.getPluginManager().getPlugin("Universes");
    public static String worldName;
    public static List<String> kits;

    public static boolean init(String wn){
        inventoryName = Utils.chat("&d&lAvailable Kits");
        kitsFile = new KitsFile(plugin);
        worldName = wn;
        kits = kitsFile.get().getStringList(worldName+".kits");
        rows = boxes * 1;
        if (kits.size() > 9 && kits.size() < 19){
            rows = boxes * 2;
        }else if (kits.size() > 18 && kits.size() < 28){
            rows = boxes * 3;
        }else if (kits.size() > 27 && kits.size() < 37){
            rows = boxes * 4;
        }else if (kits.size() > 36 && kits.size() < 46){
            rows = boxes * 5;
        }else if (kits.size() > 45 && kits.size() < 55){
            rows = boxes * 6;
        }else if (kits.size() > 54){
            return false;
        }
        inv = Bukkit.createInventory(null, rows);
        return true;
    }

    public static Inventory GUI(Player player){
        PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
        try {
            Inventory toReturn = Bukkit.createInventory(null, rows, inventoryName);
            for (int i = 0; i < kits.size(); i++) {
                String itemName = kitsFile.get().getString(kits.get(i) + ".UIItem");
                itemName = itemName.toUpperCase();
                Material m = Material.getMaterial(itemName);
                boolean enchantItem = kitsFile.get().getBoolean(kits.get(i) + ".enchant-UIItem");
                String displayName = kitsFile.get().getString(kits.get(i) + ".UIName");
                List<String> loreStrings = kitsFile.get().getStringList(kits.get(i) + ".description");
                if (enchantItem) {
                    Utils.createItem(inv, m, Enchantment.MENDING, 1, i, displayName, kits.get(i), m, items, loreStrings);
                } else {
                    Utils.createItem(inv, m, 1, i, displayName, kits.get(i), m, items, loreStrings);
                }
            }
            toReturn.setContents(inv.getContents());
            return toReturn;
        }catch(NullPointerException e){
            return null;
        }
    }

    public static void clickedItem(Player player, String kitName){
        player.closeInventory();
        String usageType = kitsFile.get().getString(kitName+".usage.type");
        if (usageType.equals("timer")) {
            String frequency = kitsFile.get().getString(kitName + ".usage.count");
            if (!frequency.contains("m") && !frequency.contains("h") && !frequency.contains("d") && !frequency.contains("M")) {
                player.closeInventory();
                player.sendMessage(Utils.chat("&cThere was an error giving you a kit. Please report this to a staff member and tell them to look over the logs and console."));
                plugin.log.warning(Utils.chat("[Universes] &cAttempt to give a player kit " + kitName + " failed. The frequency number must be followed by an m, h, d, or M; minutes. hours, days, and months respectively.."));
            } else {
                PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                long timeUsed = playerSettings.get().getLong(kitName);
                compareFrequency(timeUsed, frequency, kitName, player);
            }
        }else if (usageType.equals("uses")) {
            int uses = kitsFile.get().getInt(kitName + ".usage.count");
            PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
            int timesUsed = playerSettings.get().getInt(kitName);
            if (timesUsed >= uses) {
                player.closeInventory();
                player.sendMessage(Utils.chat("&cSorry, you've already used that kit as many times as you're allowed."));
            } else {
                Bukkit.getScheduler().runTask(plugin, new GiveItemTask(kitsFile, kitName, player));
                timesUsed += 1;
                playerSettings.get().set(kitName, timesUsed);
                playerSettings.save();
            }
        }else if (usageType.equals("unlimited")) {
            Bukkit.getScheduler().runTask(plugin, new GiveItemTask(kitsFile, kitName, player));
        }else {
            player.sendMessage(Utils.chat("&cThere was an error giving you a kit. Please report this to a staff member and tell them to look over the logs and console."));
            plugin.log.warning(Utils.chat("[Universes] &cAttempt to give a player kit " + kitName + " failed. The usage type must be either \"timer\", \"uses\", or \"unlimited\"."));
        }
    }

    private static void compareFrequency(long timeUsed, String frequency, String kitName, Player player){
        PlayerSettingsFile playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
        if (frequency.contains("m")) {
            String durationStr = frequency.substring(0, frequency.length() - 1);
            try {
                long duration = Long.parseLong(durationStr);
                duration = duration * 60;
                boolean durationCheck = checkDuration(duration);
                if (durationCheck){
                    long currentTime = System.currentTimeMillis();
                    duration = duration * 1000;
                    long timeUsable = duration + timeUsed;
                    long timeRemaining = timeUsable - currentTime;
                    if (timeRemaining >= 0){
                        timeRemaining = timeRemaining / 1000;
                        if (timeRemaining / 60 == 0) {
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + timeRemaining + ChatColor.RESET + ChatColor.RED + " more second(s) before using that kit.");
                        } else if (timeRemaining / 60 >= 1 && timeRemaining / 60 < 60) {
                            long minutes = timeRemaining / 60;
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            //secondsDouble = secondsDouble *60;
                            //long seconds = (long) secondsDouble;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minute(s), and " +
                                    ChatColor.RED + ChatColor.BOLD + seconds + ChatColor.RESET + ChatColor.RED + " seconds before using that kit.");
                        } else if (timeRemaining / 60 >= 60 && timeRemaining / 24 < 3600) {
                            long hours = timeRemaining / 60 / 60;
                            long minutes = timeRemaining / 60 / (hours + 1);
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60 * (hours + 1)) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hour(s), and " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        } else if (timeRemaining / 24 >= 1 && timeRemaining / 24 < 86400) {
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours, and " + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        }else{
                            long months = timeRemaining / 30 / 24 /60 /60;
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + months + ChatColor.RESET + ChatColor.RED + " month(s)," + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, and " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours before using that kit.");
                        }
                    }else{
                        Bukkit.getScheduler().runTask(plugin, new GiveItemTask(kitsFile, kitName, player));
                        playerSettings.get().set(kitName, System.currentTimeMillis());
                        playerSettings.save();
                    }
                }else{
                    player.sendMessage(Utils.chat("&cThere was an error giving you a kit. Please report this to a staff member and tell them to look over the logs and console."));
                    plugin.log.warning(Utils.chat("[Universes] &cAttempt to give a player kit " + kitName + " failed. The duration between kit uses exceeds one year."));
                }
            } catch (InputMismatchException e) {
                player.sendMessage(ChatColor.DARK_AQUA + "/mute " + ChatColor.AQUA + "<playername> <duration:s:m:h:d:y>");
            }
        } else if (frequency.contains("h")) {
            String durationStr = frequency.substring(0, frequency.length() - 1);
            try {
                long duration = Long.parseLong(durationStr);
                duration = (duration * 60) * 60;
                boolean durationCheck = checkDuration(duration);
                if (durationCheck){
                    long currentTime = System.currentTimeMillis();
                    duration = duration * 1000;
                    long timeUsable = duration + timeUsed;
                    long timeRemaining = timeUsable - currentTime;
                    if (timeRemaining >= 0){
                        timeRemaining = timeRemaining / 1000;
                        if (timeRemaining / 60 == 0) {
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + timeRemaining + ChatColor.RESET + ChatColor.RED + " more second(s) before using that kit.");
                        } else if (timeRemaining / 60 >= 1 && timeRemaining / 60 < 60) {
                            long minutes = timeRemaining / 60;
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            //secondsDouble = secondsDouble *60;
                            //long seconds = (long) secondsDouble;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minute(s), and " +
                                    ChatColor.RED + ChatColor.BOLD + seconds + ChatColor.RESET + ChatColor.RED + " seconds before using that kit.");
                        } else if (timeRemaining / 60 >= 60 && timeRemaining / 24 < 3600) {
                            long hours = timeRemaining / 60 / 60;
                            long minutes = timeRemaining / 60 / (hours + 1);
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60 * (hours + 1)) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hour(s), and " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        } else if (timeRemaining / 24 >= 1 && timeRemaining / 24 < 86400) {
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours, and " + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        }else{
                            long months = timeRemaining / 30 / 24 /60 /60;
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;

                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + months + ChatColor.RESET + ChatColor.RED + " month(s)," + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, and " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours before using that kit.");
                        }
                    }else{
                        Bukkit.getScheduler().runTask(plugin, new GiveItemTask(kitsFile, kitName, player));
                        playerSettings.get().set(kitName, System.currentTimeMillis());
                        playerSettings.save();
                    }
                }else{
                    player.sendMessage(Utils.chat("&cThere was an error giving you a kit. Please report this to a staff member and tell them to look over the logs and console."));
                    plugin.log.warning(Utils.chat("[Universes] &cAttempt to give a player kit " + kitName + " failed. The duration between kit uses exceeds one year."));
                }
            } catch (InputMismatchException e) {
                player.sendMessage(ChatColor.DARK_AQUA + "/mute " + ChatColor.AQUA + "<playername> <duration:s:m:h:d:y>");
            }
        } else if (frequency.contains("d")) {
            String durationStr = frequency.substring(0, frequency.length() - 1);
            try {
                long duration = Long.parseLong(durationStr);
                duration = ((duration * 60) * 60) * 24;
                boolean durationCheck = checkDuration(duration);
                if (durationCheck){
                    long currentTime = System.currentTimeMillis();
                    duration = duration * 1000;
                    long timeUsable = duration + timeUsed;
                    long timeRemaining = timeUsable - currentTime;
                    if (timeRemaining >= 0){
                        timeRemaining = timeRemaining / 1000;
                        if (timeRemaining / 60 == 0) {
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + timeRemaining + ChatColor.RESET + ChatColor.RED + " more second(s) before using that kit.");
                        } else if (timeRemaining / 60 >= 1 && timeRemaining / 60 < 60) {
                            long minutes = timeRemaining / 60;
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            //secondsDouble = secondsDouble *60;
                            //long seconds = (long) secondsDouble;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minute(s), and " +
                                    ChatColor.RED + ChatColor.BOLD + seconds + ChatColor.RESET + ChatColor.RED + " seconds before using that kit.");
                        } else if (timeRemaining / 60 >= 60 && timeRemaining / 24 < 3600) {
                            long hours = timeRemaining / 60 / 60;
                            long minutes = timeRemaining / 60 / (hours + 1);
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60 * (hours + 1)) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hour(s), and " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        } else if (timeRemaining / 24 >= 1 && timeRemaining / 24 < 86400) {
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours, and " + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        }else{
                            long months = timeRemaining / 30 / 24 /60 /60;
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;

                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + months + ChatColor.RESET + ChatColor.RED + " month(s)," + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, and " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours before using that kit.");
                        }
                    }else{
                        Bukkit.getScheduler().runTask(plugin, new GiveItemTask(kitsFile, kitName, player));
                        playerSettings.get().set(kitName, System.currentTimeMillis());
                        playerSettings.save();
                    }
                }else{
                    player.sendMessage(Utils.chat("&cThere was an error giving you a kit. Please report this to a staff member and tell them to look over the logs and console."));
                    plugin.log.warning(Utils.chat("[Universes] &cAttempt to give a player kit " + kitName + " failed. The duration between kit uses exceeds one year."));
                }
            } catch (InputMismatchException e) {
                player.sendMessage(ChatColor.DARK_AQUA + "/mute " + ChatColor.AQUA + "<playername> <duration:s:m:h:d:y>");
            }
        } else if (frequency.contains("M")) {
            String durationStr = frequency.substring(0, frequency.length() - 1);
            try {
                long duration = Long.parseLong(durationStr);
                duration = (((duration * 60) * 60) * 24) * 30;
                boolean durationCheck = checkDuration(duration);
                if (durationCheck) {
                    long currentTime = System.currentTimeMillis();
                    duration = duration * 1000;
                    long timeUsable = duration + timeUsed;
                    long timeRemaining = timeUsable - currentTime;
                    if (timeRemaining >= 0) {
                        timeRemaining = timeRemaining / 1000;
                        if (timeRemaining / 60 == 0) {
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + timeRemaining + ChatColor.RESET + ChatColor.RED + " more second(s) before using that kit.");
                        } else if (timeRemaining / 60 >= 1 && timeRemaining / 60 < 60) {
                            long minutes = timeRemaining / 60;
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            //secondsDouble = secondsDouble *60;
                            //long seconds = (long) secondsDouble;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minute(s), and " +
                                    ChatColor.RED + ChatColor.BOLD + seconds + ChatColor.RESET + ChatColor.RED + " seconds before using that kit.");
                        } else if (timeRemaining / 60 >= 60 && timeRemaining / 24 < 3600) {
                            long hours = timeRemaining / 60 / 60;
                            long minutes = timeRemaining / 60 / (hours + 1);
                            long totalMinutes = minutes + 1;
                            long secondsPassed = (totalMinutes * 60 * (hours + 1)) - timeRemaining;
                            long seconds = 60 - secondsPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hour(s), and " + ChatColor.BOLD + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        } else if (timeRemaining / 24 >= 1 && timeRemaining / 24 < 86400) {
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;
                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours, and " + minutes + ChatColor.RESET + ChatColor.RED + " minutes before using that kit.");
                        } else {
                            long months = timeRemaining / 30 / 24 / 60 / 60;
                            long days = timeRemaining / 24 / 60 / 60;
                            long hours = timeRemaining / 60 / 60 / (days + 1);
                            long totalHours = hours + 1;
                            long minutesPassed = (totalHours * 60 * (days + 1)) - timeRemaining;
                            long minutes = 60 - minutesPassed;

                            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + months + ChatColor.RESET + ChatColor.RED + " month(s)," + ChatColor.BOLD + days + ChatColor.RESET + ChatColor.RED + " days, and " + ChatColor.BOLD + hours + ChatColor.RESET + ChatColor.RED + " hours before using that kit.");
                        }
                    } else {
                        Bukkit.getScheduler().runTask(plugin, new GiveItemTask(kitsFile, kitName, player));
                        playerSettings.get().set(kitName, System.currentTimeMillis());
                        playerSettings.save();
                    }
                } else {
                    player.sendMessage(Utils.chat("&cThere was an error giving you a kit. Please report this to a staff member and tell them to look over the logs and console."));
                    plugin.log.warning(Utils.chat("[Universes] &cAttempt to give a player kit " + kitName + " failed. The duration between kit uses exceeds one year."));
                }
            } catch (InputMismatchException e) {
                // do nothing
            }
        }
    }

    private static boolean checkDuration(long duration){
        if (duration > 31536000){
            return false;
        }
        return true;
    }

}
