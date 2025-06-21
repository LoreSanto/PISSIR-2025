package it.uniupo.pissir.oggetti.corsa;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;
import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.user.Utente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Corsa {
    private int id;//id della corsa singola
    private Utente utente; //utente che ha effettuato la corsa
    private Mezzo mezzo; //mezzo utilizzato per la corsa
    private Parcheggio partenza; //parcheggio di partenza
    private Parcheggio arrivo; //parcheggio di arrivo
    private String dataPartenza; //data di partenza
    private String dataArrivo; //data di arrivo
    private float importo; //importo della corsa

    //Costruttore
    public Corsa(int id, Utente utente, Mezzo mezzo, Parcheggio partenza, Parcheggio arrivo, String dataPartenza, String dataArrivo, float importo) {
        this.id = id;
        this.utente = utente;
        this.mezzo = mezzo;
        this.partenza = partenza;
        this.arrivo = arrivo;
        this.dataPartenza = dataPartenza;
        this.dataArrivo = dataArrivo;
        this.importo = importo;
    }

    //Ritorna l'id della corsa
    public int getId() {
        return id;
    }

    //Ritorna l'utente che ha effettuato la corsa
    public Utente getUtente() {
        return utente;
    }

    //Ritorna il mezzo utilizzato per la corsa
    public Mezzo getMezzo() {
        return mezzo;
    }

    //Ritorna il parcheggio di partenza
    public Parcheggio getPartenza() {
        return partenza;
    }

    //Ritorna il parcheggio di arrivo
    public Parcheggio getArrivo() {
        return arrivo;
    }

    //Ritorna la data di partenza
    public String getDataPartenza() {
        return dataPartenza;
    }

    //Ritorna la data di arrivo
    public String getDataArrivo() {
        return dataArrivo;
    }

    //Ritorna l'importo della corsa
    public float getImporto() {
        return importo;
    }
}
