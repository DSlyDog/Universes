package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.events.ModifyInventoryClick;
import net.whispwriting.universes.en.files.PlayerSettingsFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import net.whispwriting.universes.en.guis.WorldSettingsUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.List;

public class ModifyCommand implements CommandExecutor {

    private Universes plugin;
    private WorldSettingsFile worldSettings;
    private PlayerSettingsFile playerSettings;

    public ModifyCommand(Universes pl, WorldSettingsFile ws, PlayerSettingsFile ps){
        plugin = pl;
        worldSettings = ws;
        playerSettings = ps;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("Universes.modify")){
            if (args.length == 3 || args.length == 2 && args[1].equalsIgnoreCase("spawn")){
                World worldToModify = Bukkit.getWorld(args[0]);
                if (worldToModify != null){
                    if (args[1].equalsIgnoreCase("pvp")){
                        if (args[2].equalsIgnoreCase("true")){
                            worldSettings.get().set("worlds."+args[0]+".pvp", true);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "PVP settings updated.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false")){
                            worldSettings.get().set("worlds."+args[0]+".pvp", false);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "PVP settings updated.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "PVP must be either true or false.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("spawn")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            Location loc = player.getLocation();
                            if (worldToModify == loc.getWorld()){
                                worldSettings.get().set("worlds."+args[0]+".spawn.world", loc.getWorld().getName());
                                worldSettings.get().set("worlds."+args[0]+".spawn.x", loc.getX());
                                worldSettings.get().set("worlds."+args[0]+".spawn.y", loc.getY());
                                worldSettings.get().set("worlds."+args[0]+".spawn.z", loc.getZ());
                                worldSettings.save();
                                worldSettings.reload();
                                player.sendMessage(ChatColor.GREEN + "The world spawn point has been moved to where you stand.");
                                return true;
                            }else{
                                player.sendMessage(ChatColor.RED + "You must be in the world you want to set a spawn point for.");
                                return true;
                            }
                        }else{
                            sender.sendMessage(ChatColor.RED + "Only players can update the spawn point of the world.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("allowMonsters")){
                        if (args[2].equalsIgnoreCase("true")){
                            worldSettings.get().set("worlds."+args[0]+".allowMonsters", true);
                            worldToModify.setSpawnFlags(true, worldSettings.get().getBoolean("worlds."+args[0]+".allowAnimals"));
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "allowMonsters settings updated.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false")){
                            worldSettings.get().set("worlds."+args[0]+".allowMonsters", false);
                            worldToModify.setSpawnFlags(false, worldSettings.get().getBoolean("worlds."+args[0]+".allowAnimals"));
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
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "allowMonsters settings updated.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "allowMonsters must be either true or false.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("allowAnimals")){
                        if (args[2].equalsIgnoreCase("true")){
                            worldSettings.get().set("worlds."+args[0]+".allowAnimals", true);
                            worldToModify.setSpawnFlags(worldSettings.get().getBoolean("worlds."+args[0]+".allowMonsters"), true);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "allowAnimals settings updated.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false")){
                            worldSettings.get().set("worlds."+args[0]+".allowAnimals", false);
                            worldToModify.setSpawnFlags(worldSettings.get().getBoolean("worlds."+args[0]+".allowMonsters"), false);
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
                                }
                                else if (e.getType() == EntityType.HORSE){
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
                                }else{
                                    e.remove();
                                }
                            }
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "allowAnimals settings updated.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "allowAnimals must be either true or false.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("gameMode")){
                        if (args[2].equalsIgnoreCase("survival") || args[2].equalsIgnoreCase("creative") || args[2].equalsIgnoreCase("adventure") || args[2].equalsIgnoreCase("specttor")){
                            String mode = args[2].toLowerCase();
                            worldSettings.get().set("worlds."+args[0]+".gameMode", mode);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "World gamemode updated.");
                            return true;
                        }else{
                            sender.sendMessage(ChatColor.RED + "That is an invalid gamemode.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("respawnWorld")){
                        World respawnWorld = Bukkit.getWorld(args[2]);
                        if (respawnWorld != null){
                            worldSettings.get().set("worlds."+args[0]+".respawnWorld", args[2]);
                            sender.sendMessage(ChatColor.GREEN + "respawnWorld updated.");
                            worldSettings.save();
                            worldSettings.reload();
                            return true;
                        }else{
                            sender.sendMessage(ChatColor.RED + "The world you gave for the respawn world does not exist.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("playerLimit")){
                        try {
                            int newPlayerLimit = Integer.parseInt(args[2]);
                            worldSettings.get().set("worlds."+args[0]+".playerLimit", newPlayerLimit);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Player limit updated.");
                            return true;
                        }catch (InputMismatchException e){
                            sender.sendMessage(ChatColor.RED + "Please choose a number for the player limit, or -1 to disable.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("allowFlight")){
                        if (args[2].equalsIgnoreCase("true")){
                            worldSettings.get().set("worlds."+args[0]+".allowFlight", true);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "allowFlight settings updated.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false")){
                            worldSettings.get().set("worlds."+args[0]+".allowFlight", false);
                            worldSettings.save();
                            worldSettings.reload();
                            Collection<?> players = Bukkit.getOnlinePlayers();
                            for (Object o : players){
                                Player player = (Player) o;
                                if (player.isFlying()){
                                    playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                                    boolean allowFlight = playerSettings.get().getBoolean("flightOverride");
                                    if (!allowFlight) {
                                        player.setFlying(false);
                                        player.sendMessage(ChatColor.RED + "Flying is no longer permitted in this world.");
                                    }
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "allowFlight settings updated.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "allowFlight must be either true or false.");
                            return true;
                        }
                    }else{
                        sender.sendMessage(ChatColor.DARK_RED + args[1] + " " + ChatColor.RED + "is not a valid setting.");
                        return true;
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "Could not find a world by that name.");
                    return true;
                }
            }else{
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    player.openInventory(WorldSettingsUI.GUI(player, player.getLocation().getWorld(), plugin));
                    Bukkit.getPluginManager().registerEvents(new ModifyInventoryClick(plugin, player.getUniqueId().toString()), plugin);
                }else {
                    sender.sendMessage(ChatColor.GOLD + "/universemodify " + ChatColor.YELLOW + "<world> <item to modify> <new data>");
                }
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
        }
        return true;
    }
}
