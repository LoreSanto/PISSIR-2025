package it.uniupo.pissir.oggetti.mezzo;

import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.zona.Zona;

import java.util.ArrayList;

public class MezzoDao {
    //ritorna un mezzo in base a tutti i suoi dati
    public Mezzo getMezzo(Mezzo mezzo){
        return null;
    }

    //ritorna un mezzo in base al suo id
    public Mezzo getMezzoById(int id){
        return null;
    }

    //ritorna i mezzi elettrici in base al loro tipo
    public ArrayList<Mezzo> getMezziElettriciByTipo(TipoMezzoElettrico tipo){
        return null;
    }

    //ritorna i mezzi muscolari in base al suo tipo
    public ArrayList<Mezzo> getAllMezziMuscolari(){
        return null;
    }

    //ritorna i mezzi in base al loro parcheggio
    public ArrayList<Mezzo> getMezziByParcheggio(Parcheggio parcheggio){
        return null;
    }

    //ritorna i mezzi in base alla loro zona
    public ArrayList<Mezzo> getMezziByZona(Zona zona){
        return null;
    }

    //ritorna tutti i mezzi
    public ArrayList<Mezzo> getAllMezzi(){
        return null;
    }

    //ritorna tutti i mezzi disponibili
    public ArrayList<Mezzo> getMezziDisponibili(){
        return null;
    }

    //ritorna tutti i mezzi non disponibili
    public ArrayList<Mezzo> getMezziNonDisponibili(){
        return null;
    }

    //aggiorna i dati di un mezzo
    public void aggiornaMezzo(int idMezzo){}
}
