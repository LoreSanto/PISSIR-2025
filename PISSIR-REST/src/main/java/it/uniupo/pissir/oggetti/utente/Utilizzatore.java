package it.uniupo.pissir.oggetti.utente;

import it.uniupo.pissir.oggetti.pagamento.Pagamento;

public class Utilizzatore extends Utente{
    private float credito; // Credito dell'utilizzatore
    private int punti; // Punti dell'utilizzatore
    private int numero_carta_credito; // Numero della carta di credito dell'utilizzatore
    private StatoUtilizzatore stato; // Stato dell'utilizzatore
    private int num_sospensioni; // Numero di sospensioni dell'utilizzatore

    // Costruttore
    public Utilizzatore(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password, RuoloUtente.UTILIZZATORE);
    }

    public void ricaricaCredito(Pagamento pagamento) {
        credito += pagamento.getImporto();
    }

    //Metodo che calcola il buono sconto in base ai punti
    public float calcoloBuonoSconto(){
        //da implementare
        return 0;
    }

    //Ritorna il numero di sospensioni
    public int getNumSospensioni() {
        return num_sospensioni;
    }

    //Metodo per aggiornare il numero delle sospensioni
    public void setNumSospensioni() {
        this.num_sospensioni++;
    }

    //Ritorna lo stato dell'utente
    public StatoUtilizzatore getStato() {
        return stato;
    }

    //Setta lo stato dell'utente
    public void setStato(StatoUtilizzatore stato) {
        this.stato = stato;
    }
}
