package it.uniupo.pissir.oggetti.parcheggio;

import it.uniupo.pissir.oggetti.zona.Zona;

public class Parcheggio {
    private int id;//identificatore singolo id del parcheggio
    private int capienzaMax;//numero capienza massima del parcheggio
    private Zona zona;//zona della città al cui interno ci possono essere più parcheggi

    //Costuttore parcheggio
    public Parcheggio(int id, int capienzaMax, Zona zona) {
        this.id = id;
        this.capienzaMax = capienzaMax;
        this.zona = zona;
    }

    //Metodo get per Id del parcheggio
    public int getId() {
        return id;
    }

    //Metodo get per capienza massima del parcheggio
    public int getCapienzaMax() {
        return capienzaMax;
    }

    //Metodo get per zona del parcheggio
    public Zona getZona() {
        return zona;
    }
}
