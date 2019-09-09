package net.whispwriting.universes.en.files;

import net.whispwriting.universes.Universes;

public class PlayerInventoryFile extends AbstractFile {

    public PlayerInventoryFile(Universes pl, String uuid, String world){
        super(pl, uuid+".yml", "/inventories/"+world+"/");
    }

}
