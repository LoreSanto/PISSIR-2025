package it.uniupo.pissir.oggetti.zona;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;
import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;

import java.util.ArrayList;

public class ZonaDao {
    //ritorna una zona in base a tutti i suoi dati
    public Zona getZona(Zona zona){
        return null;
    }

    //ritorna una zona in base al suo nome
    public Zona getZonaByNome(String nome){
        return null;
    }

    //ritorna la zona in base al parcheggio
    public Zona getZonaByParcheggio(Parcheggio parcheggio){
        return null;
    }

    //ritorna la zona contenente un dato mezzo
    public Zona getZonaByMezzo(Mezzo mezzo){
        return null;
    }

    //ritorna le zone che contengono un dato tipo di mezzo elettrico
    public ArrayList<Zona> getZoneByTipoMezzoElettrico(){
        return null;
    }

    //ritorna le zone che contengono un dato mezzo muscolare
    public ArrayList<Zona> getZoneByMezzoMuscolare(){
        return null;
    }

    //ritorna tutte le zone
    public ArrayList<Zona> getAllZone(){
        return null;
    }

    //aggiorna i dati di una zona nel dataBase
    public void aggiornaZona(int idZona){}
}
