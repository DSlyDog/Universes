package net.whispwriting.universes.es.files;

import net.whispwriting.universes.Universes;

public class ConfigFile extends AbstractFile {

    public ConfigFile(Universes pl){
        super(pl, "config.yml", "");
    }

    public void createConfig(){
        config.addDefault("lang", "en");
        save();
    }

}
