package net.whispwriting.universes.lang.utils;

public class WorldConfiguration {

    private String name;
    private String type;
    private boolean pvp;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private boolean allowMonsters;
    private boolean allowAnimals;
    private String gameMode;
    private String respawnWorld;
    private int playerLimit;
    private boolean allowFlight;

    public WorldConfiguration(String n, String t, boolean p, String wn, double x1, double y1, double z1, boolean am, boolean aa,
                              String gm, String rw, int pl, boolean af){

        name = n;
        type = t;
        pvp = p;
        x = x1;
        y = y1;
        z = z1;
        allowMonsters = am;
        allowAnimals = aa;
        gameMode = gm;
        respawnWorld = rw;
        playerLimit = pl;
        allowFlight = af;

    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public boolean getPVP(){
        return pvp;
    }

    public String getWorldName(){
        return worldName;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }

    public boolean getAllowMonsters(){
        return allowMonsters;
    }

    public boolean getAllowAnimals(){
        return allowAnimals;
    }

    public String getGameMode(){
        gameMode = translateGameMode(gameMode);
        return gameMode;
    }

    public String getRespawnWorld(){
        return respawnWorld;
    }

    public int getPlayerLimit(){
        return playerLimit;
    }

    public boolean getAllowFlight(){
        return allowFlight;
    }

    private String translateGameMode(String gm){
        switch (gm){
            case "survival":
                return "supervivencia";
            case "creativo":
                return "creative";
            case "creative":
                return "creativo";
            case "aventura":
                return "adventure";
            case "adventure":
                return "aventura";
            case "espectador":
                return "spectator";
            case "spectator":
                return "espectador";
            default:
                return "survival";
        }
    }

}
