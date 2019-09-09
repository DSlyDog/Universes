package net.whispwriting.universes.en.files;

import net.whispwriting.universes.Universes;

public class LangFile extends AbstractFile{

    public LangFile(Universes pl){
        super(pl, "lang.yml", "");
    }

    public void createConfig(){
        config.addDefault("lang", "en");
    }

}
