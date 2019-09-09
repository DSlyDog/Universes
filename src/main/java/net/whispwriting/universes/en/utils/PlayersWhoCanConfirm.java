package net.whispwriting.universes.en.utils;

import org.bukkit.command.CommandSender;

public class PlayersWhoCanConfirm {

    private CommandSender sender;
    private String world;

    public PlayersWhoCanConfirm(CommandSender s, String w){
        sender = s;
        world = w;
    }

    public CommandSender getSender(){
        return sender;
    }

    public String getWorld(){
        return world;
    }

}
