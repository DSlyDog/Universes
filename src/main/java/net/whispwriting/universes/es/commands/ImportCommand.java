package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.utils.Generator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;

public class ImportCommand implements CommandExecutor {

    private Universes plugin;

    public ImportCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("Universes.importworld")) {
            if (args.length != 4){
                sender.sendMessage(ChatColor.GOLD + "/universeimport " + ChatColor.YELLOW + "<name> <environment> <world type> <difficulty>");
                return true;
            }
            File worldDirectory = new File(Bukkit.getWorldContainer()+"/"+args[0]);
            if (worldDirectory.isDirectory()){
                File datFile = new File(worldDirectory + "/level.dat");
                File uidFile = new File(worldDirectory + "/uid.dat");
                if (datFile.exists() || uidFile.exists()){
                    sender.sendMessage(ChatColor.GREEN + "Comenzando importación del mundo " + ChatColor.DARK_GREEN + args[0]);
                    Generator generator = new Generator(plugin, args[0]);
                    generator.setEnvironment(getEnvironment(args[1], sender));
                    generator.generateStructures(true);
                    generator.setType(getWorldType(args[2], sender));
                    generator.createWorld();
                    World world = generator.getWorld();
                    if (world != null) {
                        world.setDifficulty(getDifficulty(args[3], sender));
                        List<String> worlds = plugin.worldListFileEs.get().getStringList("worlds");
                        worlds.add(world.getName());
                        plugin.worldListFileEs.get().set("worlds", worlds);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".nombre", args[0]);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".tipo", args[1]);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".pvp", true);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".spawn", world.getSpawnLocation());
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".permitirMonstruos", true);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".permitirAnimales", true);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".modoDeJuego", "supervivencia");
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".mundoDeReaparición", "world");
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".límiteDeJugadores", -1);
                        plugin.worldSettingsEs.get().set("worlds." + args[0] + ".permitirVuelo", true);
                        plugin.worldSettingsEs.save();
                        plugin.worldListFileEs.save();
                        sender.sendMessage(ChatColor.GREEN + "El mundo ha sido importado correctamente.");
                    } else {
                        sender.sendMessage("Importación del mundo fallida.");
                        return true;
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "Ese no es un mundo válido de Minecraft Edición Java.");
                    return true;
                }
            }else{
                sender.sendMessage(ChatColor.RED + "No se ha podido encontrar ningún mundo con ese nombre.");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }

        return true;
    }

    private Difficulty getDifficulty(String arg, CommandSender sender) {
        arg = arg.toLowerCase();
        switch (arg){
            case "pacífico":
                return Difficulty.PEACEFUL;
            case "fácil":
                return Difficulty.EASY;
            case "normal":
                return Difficulty.NORMAL;
            case "difícil":
                return Difficulty.HARD;
            default:
                sender.sendMessage(ChatColor.RED + "Dificultad no válida, estableciendo dificultad a normal.");
                return Difficulty.NORMAL;
        }
    }

    private WorldType getWorldType(String arg, CommandSender sender) {
        arg = arg.toLowerCase();
        switch (arg){
            case "amplificado":
                return WorldType.AMPLIFIED;
            case "bufet":
                return WorldType.BUFFET;
            case "personalizado":
                return WorldType.CUSTOMIZED;
            case "extraplano":
                return WorldType.FLAT;
            case "súperbiomas":
                return WorldType.LARGE_BIOMES;
            case "normal":
                return WorldType.NORMAL;
            case "versión_1_1":
                return WorldType.VERSION_1_1;
            default:
                sender.sendMessage(ChatColor.RED + "Tipo de mundo inválido, estableciendo el mundo a normal.");
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
                sender.sendMessage(ChatColor.RED + "Ambiente inválido, estableciendo el mundo a normal.");
                return World.Environment.NORMAL;
        }
    }
}
