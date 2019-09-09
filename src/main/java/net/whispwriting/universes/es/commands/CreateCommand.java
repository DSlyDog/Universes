package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.utils.Generator;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CreateCommand implements CommandExecutor {

    private Universes plugin;

    public CreateCommand(Universes pl){
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("Universes.createworld")) {
            if (args.length != 4){
                sender.sendMessage(ChatColor.GOLD + "/universecreate " + ChatColor.YELLOW + "<nombre> <ambiente> <tipo de mundo> <dificultad>");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Iniciando creación del mundo " + ChatColor.DARK_GREEN + args[0]);
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
                String name = world.getName();
                double x = world.getSpawnLocation().getX();
                double y = world.getSpawnLocation().getY();
                double z = world.getSpawnLocation().getZ();
                plugin.worldListFileEs.get().set("worlds", worlds);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".nombre", args[0]);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".tipo", args[1]);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".pvp", true);
                plugin.worldSettingsEs.get().addDefault("worlds." + args[0] + ".spawn.world", world);
                plugin.worldSettingsEs.get().addDefault("worlds." + args[0] + ".spawn.x", x);
                plugin.worldSettingsEs.get().addDefault("worlds." + args[0] + ".spawn.y", y);
                plugin.worldSettingsEs.get().addDefault("worlds." + args[0] + ".spawn.z", z);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".permitirMonstruos", true);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".permitirAnimales", true);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".modoDeJuego", "survival");
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".mundoDeReaparición", "world");
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".límiteDeJugadores", -1);
                plugin.worldSettingsEs.get().set("worlds." + args[0] + ".permitirVuelo", true);
                plugin.worldSettingsEs.save();
                plugin.worldListFileEs.save();
                sender.sendMessage(ChatColor.GREEN + "Mundo creado satisfactoriamente.");
            } else {
                sender.sendMessage("Creación del mundo fallida.");
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
