package it.uniupo.pissir.oggetti.mezzo;

import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.zona.Zona;

public class Mezzo {
    private int id; //identificativo del mezzo
    private TipoMezzo tipo; //tipo del mezzo
    private StatoMezzo stato; //stato del mezzo
    private Parcheggio parcheggio; //posizione del mezzo
    private Zona zona; //zona in cui si trova il mezzo
    private int batteria; //livello di batteria del mezzo
    private String imei; //codice identificativo del mezzo

    //costruttore
    public Mezzo(int id, TipoMezzo tipo, Zona zona, Parcheggio parcheggio, int batteria, StatoMezzo stato, String imei) {
        this.id = id;
        this.tipo = tipo;
        this.parcheggio = parcheggio;
        this.batteria = batteria;
        this.stato = stato;
        this.zona = zona;
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
    public Parcheggio getParcheggio() {
        return parcheggio;
    }

    //setta lo stato del mezzo del mezzo
    public void setStato(StatoMezzo stato) {
        this.stato = stato;
    }

    //setta la posizione del mezzo
    public void setPosizione(Parcheggio parcheggio) {
        this.parcheggio = parcheggio;
    }

    //ritorna il tipo del mezzo
    public TipoMezzo getTipo() {
        return tipo;
    }

    //ritorna la zona in cui si trova il mezzo
    public Zona getZona() {
        return zona;
    }

    //ritorna il livello di batteria del mezzo
    public int getBatteria() {
        return batteria;
    }
}
