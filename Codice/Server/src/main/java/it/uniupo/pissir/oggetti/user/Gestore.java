package it.uniupo.pissir.oggetti.user;

public class Gestore {
    private String nome;
    private String cognome;
    private String email;
    private String tipo;

    public Gestore(String nome, String cognome, String email, String tipo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.tipo = tipo;
    }

    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
}
