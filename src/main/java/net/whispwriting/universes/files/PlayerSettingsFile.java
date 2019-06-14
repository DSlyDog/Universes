package net.whispwriting.universes.files;

import net.whispwriting.universes.Universes;

public class PlayerSettingsFile extends AbstractFile {

    public PlayerSettingsFile(Universes pl, String uuid){
        super(pl, uuid+".yml", "/playerdata");
    }

    public void setup(){
        config.addDefault("gameModeOverride", false);
        config.addDefault("canJoinFullWorlds", false);
        config.addDefault("flightOverride", false);
    }

}
