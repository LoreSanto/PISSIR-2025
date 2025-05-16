package it.uniupo.pissir.oggetti.mezzo;

import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;

public class MezzoElettrico extends Mezzo {
    private int livello_batteria; //indica il livello di batteria del mezzo elettrico
    private TipoMezzoElettrico tipo; //indica il tipo di mezzo elettrico

    //costruttore
    public MezzoElettrico(int id, StatoMezzo stato, Parcheggio posizione) {
        super(id, stato, posizione);
    }

    //ritorna il livello di batteria del mezzo elettrico
    public int getLivelloBatteria() {
        return livello_batteria;
    }

    //ritorna il tipo di mezzo elettrico
    public TipoMezzoElettrico getTipo() {
        return tipo;
    }

    //setta il livello di batteria del mezzo elettrico
    public void setLivelloBatteria(int livello_batteria) {
        this.livello_batteria = livello_batteria;
    }
}
