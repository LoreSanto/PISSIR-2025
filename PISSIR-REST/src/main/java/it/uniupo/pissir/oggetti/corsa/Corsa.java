package it.uniupo.pissir.oggetti.corsa;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;
import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.utente.Utilizzatore;

import java.util.Date;

public class Corsa {
    private int id;//id della corsa singola
    private Utilizzatore utente; //utente che ha effettuato la corsa
    private Mezzo mezzo; //mezzo utilizzato per la corsa
    private Parcheggio partenza; //parcheggio di partenza
    private Parcheggio arrivo; //parcheggio di arrivo
    private Date dataPartenza; //data di partenza
    private Date dataArrivo; //data di arrivo
    private float importo; //importo della corsa

    //Costruttore
    public Corsa(int id, Utilizzatore utente, Mezzo mezzo, Parcheggio partenza, Parcheggio arrivo, Date dataPartenza, Date dataArrivo, float importo) {
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
    public Utilizzatore getUtente() {
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
    public Date getDataPartenza() {
        return dataPartenza;
    }

    //Ritorna la data di arrivo
    public Date getDataArrivo() {
        return dataArrivo;
    }

    //Ritorna l'importo della corsa
    public float getImporto() {
        return importo;
    }

    //Metodo che ritorni il calcola dell'importo
    public double calcolaImporto() {
        return 0;
    }


    //Metodo che ritorni la durata della corsa
    public int calcolaDurata() {
        return 0;
    }

    //Metodo che calcola i punti da assegnare all'utente (un punto per ogni minuto)
    public int calcolaPunti() {
        return calcolaDurata() * 1;
    }
}
