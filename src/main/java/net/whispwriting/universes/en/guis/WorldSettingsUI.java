package net.whispwriting.universes.en.guis;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.events.ChangePlayerLimit;
import net.whispwriting.universes.en.events.ChangeRespawnWorld;
import net.whispwriting.universes.en.files.WorldListFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class WorldSettingsUI {

    public static Map<Material, UIItemData> items = new HashMap<>();
    public static Inventory inv;
    public static String inventoryString;
    public static int columns = 9;
    public static int rows = columns * 1;
    public static String respawnWorld = "";

    public static void init(){
        inventoryString = Utils.chat("&6&lWorld Settings");
        inv = Bukkit.createInventory(null, rows);
    }

    public static Inventory GUI(Player player, World world, Universes plugin){
        Inventory returnInv = Bukkit.createInventory(null, rows);
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        String worldName = world.getName();

        boolean pvp = worldSettings.get().getBoolean("worlds."+worldName+".pvp");
        if (!pvp){
            Utils.createItem(inv, Material.DIAMOND_SWORD, 1, 0, "&bPVP", "WhispPVP", Material.DIAMOND_SWORD, items, "&dClick to enable or disable.", "&cPVP is currently disabled.");
        }else{
            Utils.createItem(inv, Material.DIAMOND_SWORD, Enchantment.FIRE_ASPECT, 1, 0, "&bPVP", "WhispPVP", Material.DIAMOND_SWORD, items, "&dClick to enable or disable.", "&2PVP is currently enabled.");
        }

        Utils.createItem(inv, Material.SPAWNER, 1, 1, "&bWorld Spawn", "WhispWS", Material.SPAWNER, items, "&dClick to change the spawn point of the world.");

        boolean allowAnimals = worldSettings.get().getBoolean("worlds."+worldName+".allowAnimals");
        if (!allowAnimals){
            Utils.createItem(inv, Material.WOLF_SPAWN_EGG, 1, 2, "&bAllow Animals", "WhispAA", Material.WOLF_SPAWN_EGG, items, "&dClick to enable or disable.", "&callowAnimals is disabled.");
        }else{
            Utils.createItem(inv, Material.WOLF_SPAWN_EGG, Enchantment.MENDING, 1, 2, "&bAllow Animals", "WhispAA", Material.WOLF_SPAWN_EGG, items, "&dClick to enable or disable.", "&2allowAnimals is enabled.");
        }

        boolean allowMonsters = worldSettings.get().getBoolean("worlds."+worldName+".allowMonsters");
        if (!allowMonsters){
            Utils.createItem(inv, Material.ZOMBIE_SPAWN_EGG, 1, 3, "&bAllow Monsters", "WhispAA", Material.ZOMBIE_SPAWN_EGG, items, "&dClick to enable or disable.", "&callowMonsters is disabled.");
        }else{
            Utils.createItem(inv, Material.ZOMBIE_SPAWN_EGG, Enchantment.MENDING, 1, 3, "&bAllow Monsters", "WhispAA", Material.ZOMBIE_SPAWN_EGG, items, "&dClick to enable or disable.", "&2allowMonsters is enabled.");
        }

        String gameModeString = worldSettings.get().getString("worlds."+worldName+".gameMode");
        GameMode mode = getGameModeFromString(gameModeString);
        if (mode == GameMode.SURVIVAL){
            List<Material> swapItems = Arrays.asList(Material.BEDROCK, Material.END_CRYSTAL, Material.TORCH);
            Utils.createItem(inv, Material.GRASS_BLOCK, 1, 4, "&bChange GameMode", "WhispGM", swapItems, items, "&dClick to change GameMode.", "&2GameMode is currently Survival.");
        }else if (mode == GameMode.CREATIVE){
            List<Material> swapItems = Arrays.asList(Material.GRASS_BLOCK, Material.END_CRYSTAL, Material.TORCH);
            Utils.createItem(inv, Material.BEDROCK, 1, 4, "&bChange GameMode", "WhispGM", swapItems, items, "&dClick to change GameMode.", "&2GameMode is currently Creative.");
        }else if (mode == GameMode.SPECTATOR){
            List<Material> swapItems = Arrays.asList(Material.BEDROCK, Material.GRASS_BLOCK, Material.TORCH);
            Utils.createItem(inv, Material.END_CRYSTAL, 1, 4, "&bChange GameMode", "WhispGM", swapItems, items, "&dClick to change GameMode.", "&2GameMode is currently Spectator.");
        }else{
            List<Material> swapItems = Arrays.asList(Material.BEDROCK, Material.END_CRYSTAL, Material.GRASS_BLOCK);
            Utils.createItem(inv, Material.TORCH, 1, 4, "&bChange GameMode", "WhispGM", swapItems, items, "&dClick to change GameMode.", "&2GameMode is currently Adventure.");
        }

        String currentWorld = worldSettings.get().getString("worlds."+worldName+".respawnWorld");
        Utils.createItem(inv, Material.OAK_SAPLING, 1, 5, "&bRespawn World", "WhispRS", Material.OAK_SAPLING, items, "&dClick to change the respawn world.", "&2Respawn world is currently: "+currentWorld+".");

        int playerLimit = worldSettings.get().getInt("worlds."+worldName+".playerLimit");
        Utils.createItem(inv, Material.PLAYER_HEAD, 1, 6, "&bPlayer Limit", "WhispPL", Material.PLAYER_HEAD, items, "&dClick to change the player limit.", "&2Player limit is currently: "+playerLimit+".");

        boolean allowFlight = worldSettings.get().getBoolean("worlds."+worldName+".allowFlight");
        if (!allowFlight){
            Utils.createItem(inv, Material.ELYTRA, 1, 7, "&bAllow Flight", "WhispFly", Material.ELYTRA, items, "&dClick to enable or disable.", "&callowFlight is currently disabled.");
        }else{
            Utils.createItem(inv, Material.ELYTRA, Enchantment.MENDING,1, 7, "&bAllow Flight", "WhispFly", Material.ELYTRA, items, "&dClick to enable or disable.", "&2allowFlight is currently enabled.");
        }

        returnInv.setContents(inv.getContents());
        return returnInv;
    }

    public static void clicked(Player player, Universes plugin, ItemStack clicked, String worldName){
        WorldSettingsFile worldSettings = new WorldSettingsFile(plugin);
        if(clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bPVP"))){
            boolean pvp = worldSettings.get().getBoolean("worlds."+worldName+".pvp");
            if (pvp){
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&cPVP is currently disabled."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.removeEnchantment(Enchantment.FIRE_ASPECT);
                worldSettings.get().set("worlds."+worldName+".pvp", false);
                worldSettings.save();
                player.sendMessage(Utils.chat("&cPVP is no longer allowed in "+Utils.chat("&4"+worldName)));
            }else{
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2PVP is currently enabled."));
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                clicked.setItemMeta(meta);
                clicked.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                worldSettings.get().set("worlds."+worldName+".pvp", true);
                worldSettings.save();
                player.sendMessage(Utils.chat("&2PVP is now allowed in "+Utils.chat("&a"+worldName)));
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bWorld Spawn"))){
            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();

            worldSettings.get().set("worlds."+worldName+".spawn.name", worldName);
            worldSettings.get().set("worlds."+worldName+".spawn.x", x);
            worldSettings.get().set("worlds."+worldName+".spawn.y", y);
            worldSettings.get().set("worlds."+worldName+".spawn.z", z);
            worldSettings.save();
            player.sendMessage(Utils.chat("&2Spawn point for world &a"+worldName+" &2has been set to where you stand."));
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bAllow Animals"))){
            boolean allowAnimals = worldSettings.get().getBoolean("worlds."+worldName+".allowAnimals");
            if (allowAnimals){
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&callowAnimals is disabled."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.removeEnchantment(Enchantment.MENDING);
                worldSettings.get().set("worlds."+worldName+".allowAnimals", false);
                worldSettings.save();
                World worldToModify = Bukkit.getWorld(worldName);
                worldToModify.setSpawnFlags(worldSettings.get().getBoolean("worlds."+worldName+".allowMonsters"), false);
                List<Entity> entities = worldToModify.getEntities();
                for (Entity e : entities){
                    if (e.getType() == EntityType.WOLF){
                        Wolf wolf = (Wolf) e;
                        if (!wolf.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.CAT){
                        Cat cat = (Cat) e;
                        if (!cat.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.LLAMA){
                        Llama llama = (Llama) e;
                        if (!llama.isTamed()) {
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.MULE){
                        Mule mule = (Mule) e;
                        if (!mule.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.DONKEY){
                        Donkey donkey = (Donkey) e;
                        if (!donkey.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.HORSE){
                        Horse horse = (Horse) e;
                        if (!horse.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.ZOMBIE_HORSE){
                        ZombieHorse horse = (ZombieHorse) e;
                        if (!horse.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.SKELETON_HORSE){
                        SkeletonHorse horse = (SkeletonHorse) e;
                        if (!horse.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.PARROT){
                        Parrot parrot = (Parrot) e;
                        if (!parrot.isTamed()){
                            e.remove();
                        }
                    } else if (e.getType() == EntityType.PLAYER || e.getType() == EntityType.ARMOR_STAND || e.getType() == EntityType.ARROW || e.getType() == EntityType.BOAT ||
                            e.getType() == EntityType.DROPPED_ITEM || e.getType() == EntityType.ENDER_CRYSTAL || e.getType() == EntityType.ENDER_PEARL || e.getType() == EntityType.EXPERIENCE_ORB
                            || e.getType() == EntityType.FALLING_BLOCK || e.getType() == EntityType.FIREWORK || e.getType() == EntityType.FISHING_HOOK || e.getType() == EntityType.FIREBALL ||
                            e.getType() == EntityType.DRAGON_FIREBALL || e.getType() == EntityType.SMALL_FIREBALL || e.getType() == EntityType.ITEM_FRAME || e.getType() == EntityType.LEASH_HITCH
                            || e.getType() == EntityType.MINECART || e.getType() == EntityType.MINECART_CHEST || e.getType() == EntityType.MINECART_COMMAND || e.getType() == EntityType.MINECART_FURNACE
                            || e.getType() == EntityType.MINECART_HOPPER || e.getType() == EntityType.MINECART_MOB_SPAWNER || e.getType() == EntityType.MINECART_TNT || e.getType() == EntityType.PAINTING
                            || e.getType() == EntityType.PRIMED_TNT || e.getType() == EntityType.SPECTRAL_ARROW || e.getType() == EntityType.SPLASH_POTION || e.getType() == EntityType.THROWN_EXP_BOTTLE
                            || e.getType() == EntityType.TRIDENT || e.getType() == EntityType.SNOWBALL || e.getType() == EntityType.VILLAGER || e.getType() == EntityType.WITHER_SKULL){
                        //do nothing
                    } else{
                        e.remove();
                    }
                }
                player.sendMessage(Utils.chat("&cAnimals are no longer allowed in this world."));
            }else{
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2allowAnimals is enabled."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.addUnsafeEnchantment(Enchantment.MENDING, 2);
                worldSettings.get().set("worlds."+worldName+".allowAnimals", true);
                worldSettings.save();
                World worldToModify = Bukkit.getWorld(worldName);
                worldToModify.setSpawnFlags(worldSettings.get().getBoolean("worlds."+worldName+".allowMonsters"), true);
                player.sendMessage(Utils.chat("&2Animals are now allowed in this world."));
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bAllow Monsters"))){
            boolean allowMonsters = worldSettings.get().getBoolean("worlds."+worldName+".allowMonsters");
            if (allowMonsters){
                World worldToModify = Bukkit.getWorld(worldName);
                worldSettings.get().set("worlds."+worldName+".allowMonsters", false);
                worldToModify.setSpawnFlags(false, worldSettings.get().getBoolean("worlds."+worldName+".allowAnimals"));
                worldSettings.save();
                List<Entity> entities = worldToModify.getEntities();
                for (Entity e : entities){
                    if (e.getType() == EntityType.SKELETON){
                        e.remove();
                    }
                    if (e.getType() == EntityType.WITHER_SKELETON){
                        e.remove();
                    }
                    if (e.getType() == EntityType.ZOMBIE){
                        e.remove();
                    }
                    if (e.getType() == EntityType.PIG_ZOMBIE){
                        e.remove();
                    }
                    if (e.getType() == EntityType.SPIDER || e.getType() == EntityType.CAVE_SPIDER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.ENDERMAN){
                        e.remove();
                    }
                    if (e.getType() == EntityType.ENDERMITE){
                        e.remove();
                    }
                    if (e.getType() == EntityType.ENDER_DRAGON){
                        e.remove();
                    }
                    if (e.getType() == EntityType.BLAZE){
                        e.remove();
                    }
                    if (e.getType() == EntityType.CREEPER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.DROWNED){
                        e.remove();
                    }
                    if (e.getType() == EntityType.ELDER_GUARDIAN){
                        e.remove();
                    }
                    if (e.getType() == EntityType.EVOKER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.GHAST){
                        e.remove();
                    }
                    if (e.getType() == EntityType.GUARDIAN){
                        e.remove();
                    }
                    if (e.getType() == EntityType.HUSK){
                        e.remove();
                    }
                    if (e.getType() == EntityType.MAGMA_CUBE){
                        e.remove();
                    }
                    if (e.getType() == EntityType.PHANTOM){
                        e.remove();
                    }
                    if (e.getType() == EntityType.PILLAGER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.RAVAGER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.SHULKER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.SILVERFISH){
                        e.remove();
                    }
                    if (e.getType() == EntityType.WITHER){
                        e.remove();
                    }
                    if (e.getType() == EntityType.SLIME){
                        e.remove();
                    }
                    if (e.getType() == EntityType.STRAY){
                        e.remove();
                    }
                    if (e.getType() == EntityType.VEX){
                        e.remove();
                    }
                    if (e.getType() == EntityType.VINDICATOR){
                        e.remove();
                    }
                    if (e.getType() == EntityType.WITCH){
                        e.remove();
                    }
                    if (e.getType() == EntityType.ZOMBIE_VILLAGER){
                        e.remove();
                    }

                }
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add("&callowMonsters is disabled.");
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.removeEnchantment(Enchantment.MENDING);
                player.sendMessage(Utils.chat("&callowMonsters has been disabled."));
            }else{
                World worldToModify = Bukkit.getWorld(worldName);
                worldSettings.get().set("worlds."+worldName+".allowMonsters", true);
                worldToModify.setSpawnFlags(true, worldSettings.get().getBoolean("worlds."+worldName+".allowAnimals"));
                worldSettings.save();
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add("&2allowMonsters is enabled.");
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                clicked.setItemMeta(meta);
                clicked.addUnsafeEnchantment(Enchantment.MENDING, 2);
                player.sendMessage(Utils.chat("&2allowMonsters has been enabled."));
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bChange GameMode"))){
            GameMode mode = getGameModeFromString(worldSettings.get().getString("worlds."+worldName+".gameMode"));
            if (mode == GameMode.SURVIVAL){
                worldSettings.get().set("worlds."+worldName+".gameMode", "creative");
                worldSettings.save();
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2GameMode is currently Creative."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.setType(Material.BEDROCK);
                player.sendMessage(Utils.chat("&2GameMode has been changed to Creative."));
            }else if (mode == GameMode.CREATIVE){
                worldSettings.get().set("worlds."+worldName+".gameMode", "spectator");
                worldSettings.save();
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2GameMode is currently Spectator."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.setType(Material.END_CRYSTAL);
                player.sendMessage(Utils.chat("&2GameMode has been changed to Spectator."));
            }else if (mode == GameMode.SPECTATOR){
                worldSettings.get().set("worlds."+worldName+".gameMode", "adventure");
                worldSettings.save();
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2GameMode is currently Adventure."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.setType(Material.TORCH);
                player.sendMessage(Utils.chat("&2GameMode has been changed to Adventure."));
            }else{
                worldSettings.get().set("worlds."+worldName+".gameMode", "survival");
                worldSettings.save();
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2GameMode is currently Survival."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                clicked.setType(Material.GRASS_BLOCK);
                player.sendMessage(Utils.chat("&2GameMode has been changed to Survival."));
            }
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bRespawn World"))){
            player.closeInventory();
            Bukkit.getPluginManager().registerEvents(new ChangeRespawnWorld(player.getUniqueId().toString(), plugin, worldName), plugin);
            player.sendMessage(Utils.chat("&2Please enter the name of the new respawn world."));
        }else if(clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bPlayer Limit"))){
            player.closeInventory();
            Bukkit.getPluginManager().registerEvents(new ChangePlayerLimit(player.getUniqueId().toString(), worldSettings, worldName, plugin), plugin);
            player.sendMessage(Utils.chat("&2Please enter a number for the new player limit."));
        }else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&bAllow Flight"))){
            boolean allowFlight = worldSettings.get().getBoolean("worlds."+worldName+".allowFlight");
            if (allowFlight){
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&callowFlight is currently disabled."));
                meta.setLore(lore);
                clicked.setItemMeta(meta);
                worldSettings.get().set("worlds."+worldName+".allowFlight", false);
                worldSettings.save();
                clicked.removeEnchantment(Enchantment.MENDING);
                player.sendMessage(Utils.chat("&cFlight has been disabled in this world."));
                World world = Bukkit.getWorld(worldName);
                Collection<Player> players = world.getPlayers();
                for (Player p : players){
                    if (p.isFlying()){
                        p.setFlying(false);
                        p.sendMessage(Utils.chat("&cFlying has been disabled in this world."));
                    }
                }
            }else{
                ItemMeta meta = clicked.getItemMeta();
                List<String> lore = meta.getLore();
                lore.remove(1);
                lore.add(Utils.chat("&2allowFlight is currently enabled."));
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                clicked.setItemMeta(meta);
                worldSettings.get().set("worlds."+worldName+".allowFlight", true);
                worldSettings.save();
                clicked.addUnsafeEnchantment(Enchantment.MENDING, 2);
                player.sendMessage(Utils.chat("&2Flight has been enabled in this world."));
            }
        }
    }

    private static GameMode getGameModeFromString(String gameModeString) {
        switch (gameModeString){
            case "adventure":
                return GameMode.ADVENTURE;
            case "creative":
                return GameMode.CREATIVE;
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return GameMode.SURVIVAL;
        }

    }

}
