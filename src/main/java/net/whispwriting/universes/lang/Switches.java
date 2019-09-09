package net.whispwriting.universes.lang;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.en.files.LangFile;

public class Switches {

    private LangFile lang = new LangFile(Universes.getPlugin(Universes.class));
    private String l = lang.get().getString("lang");

    public String permissionDeniedCommands(){
        switch (l){
            default:
                return "You do not have access to that command";
            case "es":
                return "No tienes acceso a ese comando.";
        }
    }

    public String commandUsage(String command){
        switch (l){
            default:
                switch (command){
                    case "create":
                        return "<name> <environment> <world type> <difficulty>";
                    case "delete":
                        return "<world name>";
                    case "import":
                        return "<name> <environment> <world type> <difficulty>";
                    case "modify":
                        return "<world> <item to modify> <new data>";
                    case "teleport":
                        return "<world>";
                    case "unload":
                        return "<world name>";
                }
                return "";
            case "es":
                switch (command){
                    case "create":
                        return "<nombre> <ambiente> <tipo de mundo> <dificultad>";
                    case "delete":
                        return "<nombre del mundo>";
                    case "import":
                        return "<name> <environment> <world type> <difficulty>";
                    case "modify":
                        return "<mundo> <ajuste a modificar> <nuevo valor>";
                    case "teleport":
                        return "<nombre del mundo>";
                    case "unload":
                        return "<nombre del mundo>";
                }
                return "";
        }
    }

}
