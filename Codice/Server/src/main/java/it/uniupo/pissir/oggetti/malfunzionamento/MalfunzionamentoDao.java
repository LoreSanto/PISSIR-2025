package it.uniupo.pissir.oggetti.malfunzionamento;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;

import java.util.ArrayList;

public class MalfunzionamentoDao {

    //ritorna un malfunzionamento un base a tutti i suoi dati
    public Malfunzionamento getMalfunzionamento(Malfunzionamento malfunzionamento){
        return null;
    }

    //ritorna un malfunzionamento in base al mezzo
    public Malfunzionamento getMalfunzionamentoByMezzo(Mezzo mezzo){
        return null;
    }

    //ritorna tutti i malfunzionamenti in base alla loro descrizione comune
    public ArrayList<Malfunzionamento> getMalfunzionamentiByDescrizione(String descrizione){
        return null;
    }

    //ritorna tutti i malfunzionamenti
    public ArrayList<Malfunzionamento> getAllMalfunzionamenti(){
        return null;
    }

    //aggiunge un nuovo malfunzionamento al database
    public void addMalfunzionamento(Malfunzionamento malfunzionamento){}

    //rimuove un malfunzionamento dal databased
    public void deleteMalfunzionamento(int idMalfunzionamento){}
}
