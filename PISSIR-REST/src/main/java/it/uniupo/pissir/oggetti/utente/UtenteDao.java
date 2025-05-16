package it.uniupo.pissir.oggetti.utente;


import it.uniupo.pissir.oggetti.utilis.DbConnect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UtenteDao {
    //ritorna un utente in base a tutti i suoi dati
    public static Utente getUtente(Utente utente){
        return null;
    }

    //ritorna un utente in base al suo nome
    public static Utente getUtenteByNome(String nome){
        return null;
    }

    //ritorna un utente in base al suo cognome
    public static Utente getUtenteByCognome(String cognome){
        return null;
    }

    //ritorna un utente in base alla sua email
    public static Utilizzatore getUtenteByEmail(String email){
        return null;
    }

    //ritorna tutti gli utilizzatori
    public static ArrayList<Utente> getAllUtilizzatori(){
        return null;
    }

    //ritorna tutti gli utilizzatori sospesi
    public static ArrayList<Utente> getUtilizzatoriSospesi(){
        return null;
    }

    //ritorna tutti gli utilizzatori attivi

    public static ArrayList<Utente> getUtilizzatoriAttivi(){
        return null;
    }

    /**
     * Aggiunge un nuovo utente al database.
     *
     * <p>
     *     Questo metodo inserisce un nuovo utente nella tabella "Utente" del database.
     * </p>
     * @param utente L'oggetto Utilizzatore da aggiungere.
     * @return l'utente Utilizzatore aggiunto.
     */
    public static Utilizzatore addUtente(Utilizzatore utente){

        final String sql = "INSERT INTO Utente (nome, cognome, email, password) VALUES (?, ?, ?, ?)";

        try{
            Connection conn = DbConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, utente.getNome());
            st.setString(2, utente.getCognome());
            st.setString(3, utente.getEmail());
            st.setString(4, utente.getPassword());

            st.executeUpdate();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return utente;
    }

    //rimuove un utente dal database
    public static void deleteUtente(int idUtente){}

    //aggiorna i dati di un utente
    public static void aggiornaUtente(Utente utente){}
}
