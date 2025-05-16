package it.uniupo.pissir.oggetti.utente;

import java.util.ArrayList;

public class UtenteDao {
    //ritorna un utente in base a tutti i suoi dati
    public Utente getUtente(Utente utente){
        return null;
    }

    //ritorna un utente in base al suo nome
    public Utente getUtenteByNome(String nome){
        return null;
    }

    //ritorna un utente in base al suo cognome
    public Utente getUtenteByCognome(String cognome){
        return null;
    }

    //ritorna un utente in base alla sua email
    public Utente getUtenteByEmail(String email){
        return null;
    }

    //ritorna tutti gli utilizzatori
    public ArrayList<Utente> getAllUtilizzatori(){
        return null;
    }

    //ritorna tutti gli utilizzatori sospesi
    public ArrayList<Utente> getUtilizzatoriSospesi(){
        return null;
    }

    //ritorna tutti gli utilizzatori attivi
    public ArrayList<Utente> getUtilizzatoriAttivi(){
        return null;
    }

    //aggiunge un utente al database
    public static Utilizzatore addUtente(Utilizzatore utente){
        return null;
    }

    //rimuove un utente dal database
    public void deleteUtente(int idUtente){}

    //aggiorna i dati di un utente
    public void aggiornaUtente(Utente utente){}
}
