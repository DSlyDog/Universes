package net.whispwriting.universes.en.files;

import net.whispwriting.universes.Universes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class AbstractFile {

    protected Universes plugin;
    private File file;
    protected FileConfiguration config;

    public AbstractFile(Universes pl, String filename, String d){
        plugin = pl;
        File dir = new File(pl.getDataFolder() + d);
        if (!dir.exists()){
            dir.mkdirs();
        }
        file = new File(dir, filename);
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    public void save(){
        try{
            config.save(file);
        }catch(IOException e){
            plugin.log.warning("Could not save file");
        }
    }
    public FileConfiguration get(){
        return config;
    }

    public void reload(){
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void delete() {
        file.delete();
    }
}
