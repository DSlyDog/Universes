package net.whispwriting.universes.en.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.ConfigFile;
import net.whispwriting.universes.en.files.PerWorldInventoryGroupsFile;
import net.whispwriting.universes.en.utils.Generator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CreateCommand implements CommandExecutor {

    private Universes plugin;

    public CreateCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("Universes.createworld")) {
            if (args.length < 4){
                sender.sendMessage(ChatColor.GOLD + "/universecreate " + ChatColor.YELLOW + "<name> <environment> <world type> <difficulty> <seed>");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Starting creation of world " + ChatColor.DARK_GREEN + args[0]);
            Generator generator = new Generator(plugin, args[0]);
            generator.setEnvironment(getEnvironment(args[1], sender));
            generator.generateStructures(true);
            generator.setType(getWorldType(args[2], sender));
            if (args.length == 5)
                generator.setSeed(Long.parseLong(args[4]));
            generator.createWorld();
            World world = generator.getWorld();
            if (world != null) {
                world.setDifficulty(getDifficulty(args[3], sender));
                List<String> worlds = plugin.worldListFile.get().getStringList("worlds");
                worlds.add(world.getName());
                String name = world.getName();
                double x = world.getSpawnLocation().getX();
                double y = world.getSpawnLocation().getY();
                double z = world.getSpawnLocation().getZ();
                PerWorldInventoryGroupsFile groupsFile = new PerWorldInventoryGroupsFile(plugin);
                groupsFile.get().set(world.getName()+".group", world.getName());
                groupsFile.save();
                plugin.worldListFile.get().set("worlds", worlds);
                plugin.worlds.get().set("worlds." + args[0] + ".name", args[0]);
                plugin.worlds.get().set("worlds." + args[0] + ".type", args[1]);
                plugin.worlds.get().set("worlds." + args[0] + ".pvp", true);
                plugin.worlds.get().set("worlds." + args[0] + ".spawn.world", name);
                plugin.worlds.get().set("worlds." + args[0] + ".spawn.x", x);
                plugin.worlds.get().set("worlds." + args[0] + ".spawn.y", y);
                plugin.worlds.get().set("worlds." + args[0] + ".spawn.z", z);
                plugin.worlds.get().set("worlds." + args[0] + ".allowMonsters", true);
                plugin.worlds.get().set("worlds." + args[0] + ".allowAnimals", true);
                plugin.worlds.get().set("worlds." + args[0] + ".gameMode", "survival");
                plugin.worlds.get().set("worlds." + args[0] + ".respawnWorld", "world");
                plugin.worlds.get().set("worlds." + args[0] + ".playerLimit", -1);
                plugin.worlds.get().set("worlds." + args[0] + ".allowFlight", true);
                plugin.worlds.save();
                plugin.worldListFile.save();
                plugin.worlds.reload();
                plugin.worldListFile.reload();
                sender.sendMessage(ChatColor.GREEN + "World successfully created.");
                Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
                boolean hasUNehters = false;
                boolean hasEnds = false;
                for (Plugin plugin : plugins){
                    if (plugin.getName().equals("Universe-Nethers"))
                        hasUNehters = true;
                    else if (plugin.getName().equals("Universe-Ends"))
                        hasEnds = true;
                }
                if (hasUNehters){
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        ConfigFile config = new ConfigFile(plugin);
                        boolean netherPerWorld = config.get().getBoolean("Universe-Nethers.nether-per-overworld");
                        if (netherPerWorld) {
                            sender.sendMessage(ChatColor.GREEN + "Starting creation of corresponding nether");
                            Generator generatorNether = new Generator(plugin, args[0] + "_nether");
                            generatorNether.setEnvironment(World.Environment.NETHER);
                            generatorNether.generateStructures(true);
                            generatorNether.setType(getWorldType(args[2], sender));
                            generatorNether.createWorld();
                            world = generatorNether.getWorld();
                            if (world != null) {
                                world.setDifficulty(getDifficulty(args[3], sender));
                                worlds.add(world.getName());
                                x = world.getSpawnLocation().getX();
                                y = world.getSpawnLocation().getY();
                                z = world.getSpawnLocation().getZ();
                                groupsFile.get().set(world.getName()+".group", args[0]);
                                groupsFile.save();
                                plugin.worldListFile.get().set("worlds", worlds);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".name", args[0] + "_nether");
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".type", "nether");
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".pvp", true);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".spawn.world", name);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".spawn.x", x);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".spawn.y", y);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".spawn.z", z);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".allowMonsters", true);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".allowAnimals", true);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".gameMode", "survival");
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".respawnWorld", "world");
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".playerLimit", -1);
                                plugin.worlds.get().set("worlds." + args[0] + "_nether" + ".allowFlight", true);
                                plugin.worlds.save();
                                plugin.worldListFile.save();
                                plugin.worlds.reload();
                                plugin.worldListFile.reload();
                                sender.sendMessage(ChatColor.GREEN + "World successfully created.");
                            }
                        }
                    }
                }
                if (hasEnds){
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        ConfigFile config = new ConfigFile(plugin);
                        boolean endPerWorld = config.get().getBoolean("Universe-Ends.end-per-overworld");
                        if (endPerWorld) {
                            sender.sendMessage(ChatColor.GREEN + "Starting creation of corresponding end");
                            Generator generatorEnd = new Generator(plugin, args[0] + "_the_end");
                            generatorEnd.setEnvironment(World.Environment.THE_END);
                            generatorEnd.generateStructures(true);
                            generatorEnd.setType(getWorldType(args[2], sender));
                            generatorEnd.createWorld();
                            world = generatorEnd.getWorld();
                            if (world != null) {
                                world.setDifficulty(getDifficulty(args[3], sender));
                                worlds.add(world.getName());
                                x = world.getSpawnLocation().getX();
                                y = world.getSpawnLocation().getY();
                                z = world.getSpawnLocation().getZ();
                                groupsFile.get().set(world.getName()+".group", args[0]);
                                groupsFile.save();
                                plugin.worldListFile.get().set("worlds", worlds);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".name", args[0] + "_nether");
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".type", "nether");
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".pvp", true);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".spawn.world", name);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".spawn.x", x);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".spawn.y", y);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".spawn.z", z);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".allowMonsters", true);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".allowAnimals", true);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".gameMode", "survival");
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".respawnWorld", "world");
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".playerLimit", -1);
                                plugin.worlds.get().set("worlds." + args[0] + "_the_end" + ".allowFlight", true);
                                plugin.worlds.save();
                                plugin.worldListFile.save();
                                plugin.worlds.reload();
                                plugin.worldListFile.reload();
                                sender.sendMessage(ChatColor.GREEN + "World successfully created.");
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage("World creation failed.");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            return true;
        }

        return true;
    }

    private Difficulty getDifficulty(String arg, CommandSender sender) {
        arg = arg.toLowerCase();
        switch (arg){
            case "peaceful":
                return Difficulty.PEACEFUL;
            case "easy":
                return Difficulty.EASY;
            case "normal":
                return Difficulty.NORMAL;
            case "hard":
                return Difficulty.HARD;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid difficulty, defaulting to normal difficulty.");
                return Difficulty.NORMAL;
        }
    }

    private WorldType getWorldType(String arg, CommandSender sender) {
        arg = arg.toLowerCase();
        switch (arg){
            case "amplified":
                return WorldType.AMPLIFIED;
            case "buffet":
                return WorldType.BUFFET;
            case "customized":
                return WorldType.CUSTOMIZED;
            case "flat":
                return WorldType.FLAT;
            case "large_biomes":
                return WorldType.LARGE_BIOMES;
            case "normal":
                return WorldType.NORMAL;
            case "version_1_1":
                return WorldType.VERSION_1_1;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid world type, defaulting to normal world.");
                return WorldType.NORMAL;
        }
    }

    private World.Environment getEnvironment(String arg, CommandSender sender) {
        arg = arg.toLowerCase();
        switch (arg){
            case "normal":
                return World.Environment.NORMAL;
            case "nether":
                return World.Environment.NETHER;
            case "end":
                return World.Environment.THE_END;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid environment, defaulting to normal world.");
                return World.Environment.NORMAL;
        }
    }
}
