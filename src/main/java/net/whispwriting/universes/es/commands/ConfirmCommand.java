package net.whispwriting.universes.es.commands;

import net.whispwriting.universes.Universes;
import net.whispwriting.universes.es.files.WorldListFile;
import net.whispwriting.universes.es.files.WorldSettingsFile;
import net.whispwriting.universes.es.utils.PlayersWhoCanConfirm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;

public class ConfirmCommand implements CommandExecutor {

    private Universes plugin;
    private WorldListFile worldList;
    private WorldSettingsFile worldSettings;

    public ConfirmCommand(Universes pl, WorldListFile wl, WorldSettingsFile ws){
        plugin = pl;
        worldList = wl;
        worldSettings = ws;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("Universes.delete")){
            sender.sendMessage(ChatColor.DARK_RED + "No tienes acceso a ese comando.");
            return true;
        }
        for (Object o : plugin.playersEs){
            PlayersWhoCanConfirm p = (PlayersWhoCanConfirm) o;
            if (p.getSender() == sender){
                sender.sendMessage(ChatColor.GREEN + "Eliminando mundo.");
                String worldName = p.getWorld();
                World world = Bukkit.getWorld(worldName);
                world.getEntities().clear();
                Bukkit.getServer().unloadWorld(world, true);
                File file = new File(Bukkit.getWorldContainer() + "/"+worldName);
                deleteFolderContents(file);
                file.delete();
                List<String> worlds = worldList.get().getStringList("worlds");
                for (int i=0; i<worlds.size(); i++){
                    if (worlds.get(i).equals(worldName)){
                        worlds.remove(i);
                    }
                }
                worldList.get().set("worlds", worlds);
                worldList.save();
                worldList.reload();
                worldSettings.get().set("worlds."+worldName, null);
                worldSettings.save();
                worldSettings.reload();
                sender.sendMessage(ChatColor.GREEN + "Mundo eliminado.");
                plugin.playersEs.remove(p);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "No tienes nada que confirmar.");
        return true;
    }

    private void deleteFolderContents(File directory){
        String[] files = directory.list();
        for (String f : files){
            File current = new File(directory.getPath(), f);
            if (current.isDirectory()){
                deleteFolderContents(current);
                current.delete();
            }else{
                current.delete();
            }
        }
    }
}
