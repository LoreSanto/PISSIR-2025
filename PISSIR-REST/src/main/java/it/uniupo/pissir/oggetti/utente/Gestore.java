package it.uniupo.pissir.oggetti.utente;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;
import it.uniupo.pissir.oggetti.mezzo.MezzoElettrico;
import it.uniupo.pissir.oggetti.mezzo.StatoMezzo;
import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;

public class Gestore extends Utente{
    // Costruttore
    public Gestore(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password, RuoloUtente.GESTORE);
    }

    //Metodo per cambiare lo stato di un mezzo
    public void cambiaStatoMezzo(Mezzo mezzo, StatoMezzo statoMezzo) {
        mezzo.setStato(statoMezzo);
    }

    //Metodo per cambiare il livello della batteria di un mezzo
    public void cambiaLivelloBatteria(MezzoElettrico mezzo, int livelloBatteria) {
        mezzo.setLivelloBatteria(livelloBatteria);
    }

    //Metodo per cambiare la posizione di un mezzo
    public void cambiaPosizioneMezzo(Mezzo mezzo, Parcheggio parcheggio) {
        mezzo.setPosizione(parcheggio);
    }

    //Metodo per cambiare lo stato di un utilizzatore
    public void cambiaStatoUtilizzatore(Utilizzatore utilizzatore){
        if (utilizzatore.getStato() == StatoUtilizzatore.ATTIVO) {
            utilizzatore.setStato(StatoUtilizzatore.SOSPESO);
        } else {
            utilizzatore.setStato(StatoUtilizzatore.ATTIVO);
        }
    }
}
