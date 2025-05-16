package it.uniupo.pissir.oggetti.utente;

public abstract class Utente {
    private String nome; // Nome dell'utente
    private String cognome; // Cognome dell'utente
    private String email; // Email dell'utente
    private String password; // Password dell'utente
    private RuoloUtente ruolo;

    // Costruttore
    public Utente(String nome, String cognome, String email, String password, RuoloUtente ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    //Ritorna il nome dell'utente
    public String getNome() {
        return nome;
    }

    //Ritorna il cognome dell'utente
    public String getCognome() {
        return cognome;
    }

    //Ritorna l'email dell'utente
    public String getEmail() {
        return email;
    }

    //Ritorna la password dell'utente
    public String getPassword() {
        return password;
    }

    //Ritorna il ruolo dell'utente
    public RuoloUtente getRuolo(){
        return ruolo;
    }
}
