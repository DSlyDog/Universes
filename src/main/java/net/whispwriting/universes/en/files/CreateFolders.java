package net.whispwriting.universes.en.files;

import net.whispwriting.universes.Universes;

import java.io.File;

public class CreateFolders {
    public CreateFolders(String directory, Universes plugin){
        File file = new File(plugin.getDataFolder() + "/inventories/"+directory+"/");
        if (!file.exists()){
            file.mkdirs();
        }
    }
}
