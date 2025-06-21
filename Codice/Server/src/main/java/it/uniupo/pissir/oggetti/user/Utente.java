package it.uniupo.pissir.oggetti.user;

public class Utente {

    private String nome;
    private String cognome;
    private String email;
    private String password;
    private double credito;
    private int punti;
    private int num_sospensioni;
    private String stato;
    private String tipo;

    public Utente(String nome, String cognome, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public Utente(String nome, String cognome, String email, String password, double credito, int punti, int num_sospensioni, String stato, String tipo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.credito = credito;
        this.punti = punti;
        this.num_sospensioni = num_sospensioni;
        this.stato = stato;
        this.tipo = tipo;
    }

    // Getter e Setter
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getCredito() { return credito; }
    public int getPunti() { return punti; }
    public int getNumSospensioni() { return num_sospensioni; }
    public String getStato() { return stato; }
    public String getTipo() { return tipo; }
}
