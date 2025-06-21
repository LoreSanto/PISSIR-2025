package it.uniupo.pissir.oggetti.parcheggio;

import it.uniupo.pissir.oggetti.zona.Zona;

public class Parcheggio {
    private String nome;//identificatore singolo id del parcheggio
    private int capienzaMax;//numero capienza massima del parcheggio
    private Zona zona;//zona della città al cui interno ci possono essere più parcheggi
    private int postiOccupati; // Numero di posti occupati

    //Costuttore parcheggio
    public Parcheggio(String nome, int capienzaMax, Zona zona, int postiOccupati) {
        this.nome = nome;
        this.capienzaMax = capienzaMax;
        this.zona = zona;
        this.postiOccupati = postiOccupati;
    }

    //Metodo get per Id del parcheggio
    public String getNome() {
        return nome;
    }

    //Metodo get per capienza massima del parcheggio
    public int getCapienzaMax() {
        return capienzaMax;
    }

    //Metodo get per zona del parcheggio
    public Zona getZona() {
        return zona;
    }

    //Metodo get per posti occupati del parcheggio
    public int getPostiOccupati() {
        return postiOccupati;
    }
}
