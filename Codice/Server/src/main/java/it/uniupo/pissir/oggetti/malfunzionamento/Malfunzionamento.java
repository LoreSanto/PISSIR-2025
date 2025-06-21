package it.uniupo.pissir.oggetti.malfunzionamento;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;

public class Malfunzionamento {
    private Mezzo mezzo;//Mezzo che ha un malfunzionamento
    private String descrizione;//Descrizione malfunzionamento

    //Costruttore malfunzionamento
    public Malfunzionamento(Mezzo mezzo, String descrizione) {
        this.mezzo = mezzo;
        this.descrizione = descrizione;
    }

    //Metodo get per mezzo che ha un malfunzionamento
    public Mezzo getMezzo() {
        return mezzo;
    }

    //Metodo get per descrizione del mezzo che ha un malfunzionamento
    public String getDescrizione() {
        return descrizione;
    }

    //Metodo per cambiare stato del mezzo con malfunzionamento
    public void cambiaStatoMezzo(){

    }
}
