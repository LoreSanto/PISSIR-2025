package it.uniupo.pissir.oggetti.corsa;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;
import it.uniupo.pissir.oggetti.utente.Utente;

import java.util.ArrayList;

public class CorsaDao {

    //ritorna una corsa in base a tutti i suoi dati
    public Corsa getCorsa(Corsa corsa){
        return null;
    }

    //ritorna una corsa in base al suo id
    public Corsa getCorsaById(int id){
        return null;
    }

    //ritorna le corse effettuate da un mezzo
    public ArrayList<Corsa> getCorseByMezzo(Mezzo mezzo){
        return null;
    }

    //ritorna le corse effettuate da un utente
    public ArrayList<Corsa> getCorseByUtilizzatore(Utente utente){
        return null;
    }

    //ritorna tutte le corse
    public ArrayList<Corsa> getAllCorse(){
        return null;
    }

    //aggiunge una nuova corsa al database
    public void addCorsa(Corsa corsa){}
}
