package it.uniupo.pissir.oggetti.mezzo;

import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;

public abstract class Mezzo {
    private int id; //identificativo del mezzo
    StatoMezzo stato; //stato del mezzo
    Parcheggio posizione; //posizione del mezzo

    //costruttore
    public Mezzo(int id, StatoMezzo stato, Parcheggio posizione) {
        this.id = id;
        this.stato = stato;
        this.posizione = posizione;
    }

    //ritorna l'id del mezzo
    public int getId() {
        return id;
    }

    //ritorna lo stato del mezzo
    public StatoMezzo getStato() {
        return stato;
    }

    //ritorna la posizione del mezzo
    public Parcheggio getPosizione() {
        return posizione;
    }

    //setta lo stato del mezzo del mezzo
    public void setStato(StatoMezzo stato) {
        this.stato = stato;
    }

    //setta la posizione del mezzo
    public void setPosizione(Parcheggio posizione) {
        this.posizione = posizione;
    }
}
