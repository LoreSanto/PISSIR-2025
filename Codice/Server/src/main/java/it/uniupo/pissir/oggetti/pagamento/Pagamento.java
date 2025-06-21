package it.uniupo.pissir.oggetti.pagamento;

import it.uniupo.pissir.oggetti.user.Utente;

public class Pagamento {
    private int id; //id del pagamento
    private String data; //data del pagamento
    private float importo; //importo del pagamento
    private Utente utente; //utente che ha effettuato il pagamento

    //costruttore
    public Pagamento(int id, String data, float importo, Utente utente) {
        this.id = id;
        this.data = data;
        this.importo = importo;
        this.utente = utente;
    }

    //Ritorna l'id del pagamento
    public int getId() {
        return id;
    }

    //Ritorna l'utente che ha effettuato il pagamento
    public Utente getUtente() {
        return utente;
    }

    //Ritorna la data del pagamento
    public String getData() {
        return data;
    }

    //Ritorna l'importo del pagamento
    public float getImporto() {
        return importo;
    }
}
