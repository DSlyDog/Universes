package net.whispwriting.universes.es.files;

import net.whispwriting.universes.Universes;

public class PlayerSettingsFile extends AbstractFile {

    public PlayerSettingsFile(Universes pl, String uuid){
        super(pl, uuid+".yml", "/playerdata");
    }

    public void setup(){
        config.addDefault("ignorarModoDeJuego", false);
        config.addDefault("puedeUnirseConMundoLleno", false);
        config.addDefault("ignorarAjustesDeVuelo", false);
    }

}
