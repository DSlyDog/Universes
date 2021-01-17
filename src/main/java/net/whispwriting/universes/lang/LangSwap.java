package net.whispwriting.universes.lang;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.WorldListFile;
import net.whispwriting.universes.en.files.WorldSettingsFile;
import net.whispwriting.universes.lang.utils.WorldConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LangSwap {

    public static void swapToSpanish(Universes pl) {
        File dataFolder = new File(pl.getDataFolder() + "/playerata/");
        for (File file : new File(pl.getDataFolder().getAbsolutePath() + File.separator + "playerdata").listFiles()) {
            FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
            boolean gameModeOverride = conf.getBoolean("gameModeOverride");
            boolean playerLimitOverride = conf.getBoolean("canJoinFullWorlds");
            boolean flightOverride = conf.getBoolean("flightOverride");

            conf.set("gameModeOverride", null);
            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }
            conf.set("canJoinFullWorlds", null);
            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }
            conf.set("flightOverride", null);
            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }

            conf.set("ignorarModoDeJuego", gameModeOverride);
            conf.set("puedeUnirseConMundoLleno", playerLimitOverride);
            conf.set("ignorarAjustesDeVuelo", flightOverride);

            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }
        }
        WorldSettingsFile worldSettingsEn = new WorldSettingsFile(pl);
        WorldListFile worldList = new WorldListFile(pl);
        List<String> wl = worldList.get().getStringList("worlds");
        List<WorldConfiguration> worldConfs = new ArrayList<>();
        for (String world : wl) {
            String name = worldSettingsEn.get().getString("worlds." + world + ".name");
            worldSettingsEn.get().set("worlds."+world+".name", null);
            String type = worldSettingsEn.get().getString("worlds." + world + ".type");
            worldSettingsEn.get().set("worlds."+world+".type", null);
            boolean pvp = worldSettingsEn.get().getBoolean("worlds." + world + ".pvp");
            worldSettingsEn.get().set("worlds."+world+".pvp", null);
            String worldName = worldSettingsEn.get().getString("worlds." + world + ".spawn.world");
            double x = worldSettingsEn.get().getDouble("worlds." + world + ".spawn.x");
            double y = worldSettingsEn.get().getDouble("worlds." + world + ".spawn.y");
            double z = worldSettingsEn.get().getDouble("worlds." + world + ".spawn.z");
            boolean allowMonsters = worldSettingsEn.get().getBoolean("worlds." + world + ".allowMonsters");
            worldSettingsEn.get().set("worlds."+world+".allowMonsters", null);
            boolean allowAnimals = worldSettingsEn.get().getBoolean("worlds." + world + ".allowAnimals");
            worldSettingsEn.get().set("worlds."+world+".allowAnimals", null);
            String gameMode = worldSettingsEn.get().getString("worlds." + world + ".gameMode");
            worldSettingsEn.get().set("worlds."+world+".gameMode", null);
            String respawnWorld = worldSettingsEn.get().getString("worlds." + world + ".respawnWorld");
            worldSettingsEn.get().set("worlds."+world+".respawnWorld", null);
            int playerLimit = worldSettingsEn.get().getInt("worlds." + world + ".playerLimit");
            worldSettingsEn.get().set("worlds."+world+".playerLimit", null);
            boolean allowFlight = worldSettingsEn.get().getBoolean("worlds." + world + ".allowFlight");
            worldSettingsEn.get().set("worlds."+world+".allowFlight", null);

            WorldConfiguration worldConf = new WorldConfiguration(name, type, pvp, worldName, x, y, z, allowMonsters,
                    allowAnimals, gameMode, respawnWorld, playerLimit, allowFlight);

            worldConfs.add(worldConf);
        }
        worldSettingsEn.delete();
        worldSettingsEn = new WorldSettingsFile(pl);
        worldSettingsEn.get().set("worlds.", null);

        for (WorldConfiguration worldConf : worldConfs) {
            String world = worldConf.getName();
            worldSettingsEn.get().set("worlds." + world + ".nombre", worldConf.getName());
            worldSettingsEn.get().set("worlds." + world + ".tipo", worldConf.getType());
            worldSettingsEn.get().set("worlds." + world + ".pvp", worldConf.getPVP());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.world", worldConf.getWorldName());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.x", worldConf.getX());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.y", worldConf.getY());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.z", worldConf.getZ());
            worldSettingsEn.get().set("worlds." + world + ".permitirMonstruos", worldConf.getAllowMonsters());
            worldSettingsEn.get().set("worlds." + world + ".permitirAnimales", worldConf.getAllowAnimals());
            worldSettingsEn.get().set("worlds." + world + ".modoDeJuego", worldConf.getGameMode());
            worldSettingsEn.get().set("worlds." + world + ".mundoDeReaparición", worldConf.getRespawnWorld());
            worldSettingsEn.get().set("worlds." + world + ".límiteDeJugadores", worldConf.getPlayerLimit());
            worldSettingsEn.get().set("worlds." + world + ".permitirVuelo", worldConf.getAllowFlight());
            worldSettingsEn.save();
            worldSettingsEn.reload();
        }

    }

    public static void swapToEnglish(Universes pl) {
        File dataFolder = new File(pl.getDataFolder() + "/playerata/");
        for (File file : new File(pl.getDataFolder().getAbsolutePath() + File.separator + "playerdata").listFiles()) {
            FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
            boolean gameModeOverride = conf.getBoolean("ignorarModoDeJuego");
            boolean playerLimitOverride = conf.getBoolean("puedeUnirseConMundoLleno");
            boolean flightOverride = conf.getBoolean("ignorarAjustesDeVuelo");

            conf.set("ignorarModoDeJuego", null);
            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }
            conf.set("puedeUnirseConMundoLleno", null);
            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }
            conf.set("ignorarAjustesDeVuelo", null);
            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }

            conf.set("gameModeOverride", gameModeOverride);
            conf.set("canJoinFullWorlds", playerLimitOverride);
            conf.set("flightOverride", flightOverride);

            try{
                conf.save(file);
            }catch(IOException e){
                pl.log.warning("Could not save file");
            }
        }
        WorldSettingsFile worldSettingsEn = new WorldSettingsFile(pl);
        WorldListFile worldList = new WorldListFile(pl);
        List<String> wl = worldList.get().getStringList("worlds");
        List<WorldConfiguration> worldConfs = new ArrayList<>();
        for (String world : wl) {
            String name = worldSettingsEn.get().getString("worlds." + world + ".nombre");
            worldSettingsEn.get().set("worlds."+world+".nombre", null);
            String type = worldSettingsEn.get().getString("worlds." + world + ".tipo");
            worldSettingsEn.get().set("worlds."+world+".tipo", null);
            boolean pvp = worldSettingsEn.get().getBoolean("worlds." + world + ".pvp");
            worldSettingsEn.get().set("worlds."+world+".pvp", null);
            String worldName = worldSettingsEn.get().getString("worlds." + world + ".spawn.world");
            double x = worldSettingsEn.get().getDouble("worlds." + world + ".spawn.x");
            double y = worldSettingsEn.get().getDouble("worlds." + world + ".spawn.y");
            double z = worldSettingsEn.get().getDouble("worlds." + world + ".spawn.z");
            boolean allowMonsters = worldSettingsEn.get().getBoolean("worlds." + world + ".permitirMonstruos");
            worldSettingsEn.get().set("worlds."+world+".permitirMonstruos", null);
            boolean allowAnimals = worldSettingsEn.get().getBoolean("worlds." + world + ".permitirAnimales");
            worldSettingsEn.get().set("worlds."+world+".permitirAnimales", null);
            String gameMode = worldSettingsEn.get().getString("worlds." + world + ".modoDeJuego");
            worldSettingsEn.get().set("worlds."+world+".modoDeJuego", null);
            String respawnWorld = worldSettingsEn.get().getString("worlds." + world + ".mundoDeReaparición");
            worldSettingsEn.get().set("worlds."+world+".mundoDeReaparición", null);
            int playerLimit = worldSettingsEn.get().getInt("worlds." + world + ".límiteDeJugadores");
            worldSettingsEn.get().set("worlds."+world+".límiteDeJugadores", null);
            boolean allowFlight = worldSettingsEn.get().getBoolean("worlds." + world + ".permitirVuelo");
            worldSettingsEn.get().set("worlds."+world+".permitirVuelo", null);

            WorldConfiguration worldConf = new WorldConfiguration(name, type, pvp, worldName, x, y, z, allowMonsters,
                    allowAnimals, gameMode, respawnWorld, playerLimit, allowFlight);

            worldConfs.add(worldConf);
        }
        worldSettingsEn.delete();
        worldSettingsEn = new WorldSettingsFile(pl);
        worldSettingsEn.get().set("worlds.", null);

        for (WorldConfiguration worldConf : worldConfs) {
            String world = worldConf.getName();
            worldSettingsEn.get().set("worlds." + world + ".name", worldConf.getName());
            worldSettingsEn.get().set("worlds." + world + ".type", worldConf.getType());
            worldSettingsEn.get().set("worlds." + world + ".pvp", worldConf.getPVP());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.world", worldConf.getWorldName());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.x", worldConf.getX());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.y", worldConf.getY());
            worldSettingsEn.get().addDefault("worlds." + world + ".spawn.z", worldConf.getZ());
            worldSettingsEn.get().set("worlds." + world + ".allowMonsters", worldConf.getAllowMonsters());
            worldSettingsEn.get().set("worlds." + world + ".allowAnimals", worldConf.getAllowAnimals());
            worldSettingsEn.get().set("worlds." + world + ".gameMode", worldConf.getGameMode());
            worldSettingsEn.get().set("worlds." + world + ".respawnWorld", worldConf.getRespawnWorld());
            worldSettingsEn.get().set("worlds." + world + ".playerLimit", worldConf.getPlayerLimit());
            worldSettingsEn.get().set("worlds." + world + ".allowFlight", worldConf.getAllowFlight());
            worldSettingsEn.save();
            worldSettingsEn.reload();
        }

    }

}
