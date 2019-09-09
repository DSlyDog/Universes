package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.es.files.PlayerSettingsFile;
import net.whispwriting.universes.es.files.WorldSettingsFile;
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
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("verdad")){
                            worldSettings.get().set("worlds."+args[0]+".pvp", true);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Ajuste de PVP activado.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("falso")){
                            worldSettings.get().set("worlds."+args[0]+".pvp", false);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Ajuste de PVP activado.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "El ajuste de PVP debe ser verdadero o falso.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("spawn")){
                        if (sender instanceof Player){
                            Player player = (Player) sender;
                            Location loc = player.getLocation();
                            if (worldToModify == loc.getWorld()){
                                worldSettings.get().set("worlds."+args[0]+".spawn", loc);
                                worldSettings.save();
                                worldSettings.reload();
                                player.sendMessage(ChatColor.GREEN + "El punto de aparición ha sido movido al lugar en el que te encuentras.");
                                return true;
                            }else{
                                player.sendMessage(ChatColor.RED + "Debes estar en el mundo en el cual quieres establecer el punto de aparición.");
                                return true;
                            }
                        }else{
                            sender.sendMessage(ChatColor.RED + "Sólo los jugadores pueden actualizar el punto de aparición del mundo.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("allowMonsters") || args[1].equalsIgnoreCase("permitirMonstruos")){
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("verdad")){
                            worldSettings.get().set("worlds."+args[0]+".permitirMonstruos", true);
                            worldToModify.setSpawnFlags(true, worldSettings.get().getBoolean("worlds."+args[0]+".permitirAnimales"));
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Ajuste permitirMonstruos actualizado.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("falso")){
                            worldSettings.get().set("worlds."+args[0]+".permitirMonstruos", false);
                            worldToModify.setSpawnFlags(false, worldSettings.get().getBoolean("worlds."+args[0]+".permitirAnimales"));
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
                            sender.sendMessage(ChatColor.GREEN + "Ajuste permitirMonstruos actualizado.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "El ajuste permitirMonstruos debe ser verdadero o falso.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("allowAnimals") || args[1].equalsIgnoreCase("permitirAnimales")){
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("verdad")){
                            worldSettings.get().set("worlds."+args[0]+".permitirAnimales", true);
                            worldToModify.setSpawnFlags(worldSettings.get().getBoolean("worlds."+args[0]+".permitirMonstruos"), true);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Ajuste permitirAnimales actualizado.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("falso")){
                            worldSettings.get().set("worlds."+args[0]+".permitirAnimales", false);
                            worldToModify.setSpawnFlags(worldSettings.get().getBoolean("worlds."+args[0]+".permitirMonstruos"), false);
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
                            sender.sendMessage(ChatColor.GREEN + "Ajuste permitirAnimales actualizado.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "El ajuste permitirAnimales debe ser verdadero o falso.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("gameMode") || args[1].equalsIgnoreCase("modoDeJuego")){
                        if (args[2].equalsIgnoreCase("survival") || args[2].equalsIgnoreCase("supervivencia") ||
                                args[2].equalsIgnoreCase("creative") || args[2].equalsIgnoreCase("creativo") || args[2].equalsIgnoreCase("adventure") ||
                                args[2].equalsIgnoreCase("aventura") || args[2].equalsIgnoreCase("specttor") || args[2].equalsIgnoreCase("espectador")){
                            String mode = args[2].toLowerCase();
                            worldSettings.get().set("worlds."+args[0]+".modoDeJuego", mode);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "El modo de juego del mundo ha sido actualizado.");
                            return true;
                        }else{
                            sender.sendMessage(ChatColor.RED + "Ese modo de juego no es válido.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("respawnWorld") || args[1].equalsIgnoreCase("mundoDeReaparición")){
                        World respawnWorld = Bukkit.getWorld(args[2]);
                        if (respawnWorld != null){
                            worldSettings.get().set("worlds."+args[0]+".mundoDeReaparición", args[2]);
                            sender.sendMessage(ChatColor.GREEN + "mundoDeReaparición actualizado.");
                            worldSettings.save();
                            worldSettings.reload();
                            return true;
                        }else{
                            sender.sendMessage(ChatColor.RED + "El mundo de reaparición seleccionado no existe.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("playerLimit") || args[1].equalsIgnoreCase("límiteDeJugadores")){
                        try {
                            int newPlayerLimit = Integer.parseInt(args[2]);
                            worldSettings.get().set("worlds."+args[0]+".límiteDeJugadores", newPlayerLimit);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Límite de jugadores actualizado.");
                            return true;
                        }catch (InputMismatchException e){
                            sender.sendMessage(ChatColor.RED + "Por favor, elige un número para el límite de jugadores, o -1 para desactivarlo.");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("allowFlight") || args[1].equalsIgnoreCase("permitirVuelo")){
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("verdad")){
                            worldSettings.get().set("worlds."+args[0]+".permitirVuelo", true);
                            worldSettings.save();
                            worldSettings.reload();
                            sender.sendMessage(ChatColor.GREEN + "Ajuste permitirVuelo actualizado.");
                            return true;
                        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("falso")){
                            worldSettings.get().set("worlds."+args[0]+".permitirVuelo", false);
                            worldSettings.save();
                            worldSettings.reload();
                            Collection<?> players = Bukkit.getOnlinePlayers();
                            for (Object o : players){
                                Player player = (Player) o;
                                if (player.isFlying()){
                                    playerSettings = new PlayerSettingsFile(plugin, player.getUniqueId().toString());
                                    boolean allowFlight = playerSettings.get().getBoolean("ignorarAjustesDeVuelo");
                                    if (!allowFlight) {
                                        player.setFlying(false);
                                        player.sendMessage(ChatColor.RED + "Ya no está permitido volar en este mundo.");
                                    }
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "Ajuste permitirVuelo actualizado.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "permitirVuelo debe ser verdadero o falso.");
                            return true;
                        }
                    }else{
                        sender.sendMessage(ChatColor.DARK_RED + args[1] + " " + ChatColor.RED + "no es un ajuste válido.");
                        return true;
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "No se ha podido encontrar ningún mundo con ese nombre.");
                    return true;
                }
            }else{
                sender.sendMessage(ChatColor.GOLD + "/universemodify " + ChatColor.YELLOW + "<mundo> <ajuste a modificar> <nuevo valor>");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
        }
        return true;
    }
}
