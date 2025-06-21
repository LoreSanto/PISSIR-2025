package it.uniupo.pissir.oggetti.corsa;

import it.uniupo.pissir.oggetti.mezzo.Mezzo;
import it.uniupo.pissir.oggetti.mezzo.TipoMezzo;
import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.user.Utente;
import it.uniupo.pissir.oggetti.utilis.DbConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CorsaDao {


    //ritorna una corsa in base a tutti i suoi dati
    public Corsa getCorsa(Corsa corsa){
        return null;
    }

    //ritorna una corsa in base al suo id
    public Corsa getCorsaById(int id){
        return null;
    }

    //ritorna le corse effettuate da un mezzo
    public ArrayList<Corsa> getCorseByMezzo(Mezzo mezzo){
        return null;
    }

    //ritorna le corse effettuate da un utente
    public ArrayList<Corsa> getCorseByUtilizzatore(Utente utente){
        return null;
    }

    //ritorna tutte le corse
    public static ArrayList<Corsa> getAllCorse(){
        ArrayList<Corsa> corse = new ArrayList<>();
        //String sql = "SELECT id_corsa, email_utilizzatore AS email_utente, id_mezzo, (strftime('%s', fine_corsa) - strftime('%s', inizio_corsa)) / 60 AS durata_corsa_minuti, costo, parcheggio_partenza, parcheggio_fine, date(inizio_corsa) AS data " +
        //        "FROM Corsa";

        String sql = "SELECT c.id_corsa, c.email_utilizzatore, c.id_mezzo, m. tipo, c.parcheggio_partenza, c.parcheggio_fine, c.inizio_corsa, c.fine_corsa, c.costo " +
                     "FROM Corsa c JOIN Mezzo m ON c.id_mezzo = m.id_mezzo ";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                TipoMezzo tipo;
                switch (rs.getString("tipo")) {
                    case "BICICLETTA":
                        tipo = TipoMezzo.BICICLETTA;
                        break;
                    case "MONOPATTINO":
                        tipo = TipoMezzo.MONOPATTINO;
                        break;
                    case "BICI_ELETTRICA":
                        tipo = TipoMezzo.BICI_ELETTRICA;
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo di mezzo sconosciuto: " + rs.getString("tipo"));
                }

                Corsa corsa = new Corsa(
                    rs.getInt("id_corsa"),
                    new Utente( null, null, rs.getString("email_utilizzatore"), null), // Utente che ha effettuato la corsa
                    new Mezzo(rs.getInt("id_mezzo"), tipo, null, null, 0, null, null), // Mezzo utilizzato per la corsa
                    new Parcheggio(rs.getString("parcheggio_partenza"), 0, null, 0), // Parcheggio partenza
                    new Parcheggio(rs.getString("parcheggio_fine"), 0, null, 0), // Parcheggio arrivo
                    rs.getString("inizio_corsa"), // Data di partenza
                    rs.getString("fine_corsa"), // Data di arrivo
                    rs.getFloat("costo") // Importo della corsa
                );
                corse.add(corsa);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Numero corse trovate: " + corse.size());
        return corse;
    }

    //ritorna tutte le corse di un utilizzatore
    public static ArrayList<Corsa> getAllCorseUser(String email){
        ArrayList<Corsa> corse = new ArrayList<>();
        //String sql = "SELECT id_corsa, email_utilizzatore AS email_utente, id_mezzo, (strftime('%s', fine_corsa) - strftime('%s', inizio_corsa)) / 60 AS durata_corsa_minuti, costo, parcheggio_partenza, parcheggio_fine, date(inizio_corsa) AS data " +
        //        "FROM Corsa";

        String sql = "SELECT c.id_corsa, c.email_utilizzatore, c.id_mezzo, m. tipo, c.parcheggio_partenza, c.parcheggio_fine, c.inizio_corsa, c.fine_corsa, c.costo " +
                "FROM Corsa c JOIN Mezzo m ON c.id_mezzo = m.id_mezzo " +
                "WHERE c.email_utilizzatore = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                TipoMezzo tipo;
                switch (rs.getString("tipo")) {
                    case "BICICLETTA":
                        tipo = TipoMezzo.BICICLETTA;
                        break;
                    case "MONOPATTINO":
                        tipo = TipoMezzo.MONOPATTINO;
                        break;
                    case "BICI_ELETTRICA":
                        tipo = TipoMezzo.BICI_ELETTRICA;
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo di mezzo sconosciuto: " + rs.getString("tipo"));
                }

                Corsa corsa = new Corsa(
                        rs.getInt("id_corsa"),
                        new Utente( null, null, rs.getString("email_utilizzatore"), null), // Utente che ha effettuato la corsa
                        new Mezzo(rs.getInt("id_mezzo"), tipo, null, null, 0, null, null), // Mezzo utilizzato per la corsa
                        new Parcheggio(rs.getString("parcheggio_partenza"), 0, null, 0), // Parcheggio partenza
                        new Parcheggio(rs.getString("parcheggio_fine"), 0, null, 0), // Parcheggio arrivo
                        rs.getString("inizio_corsa"), // Data di partenza
                        rs.getString("fine_corsa"), // Data di arrivo
                        rs.getFloat("costo") // Importo della corsa
                );
                corse.add(corsa);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Numero corse trovate: " + corse.size());
        return corse;
    }

    /**
     * <h2>Aggiunge una corsa al database.</h2>
     * @param email L'email dell'utilizzatore che ha effettuato la corsa.
     * @param parcheggio Il parcheggio di partenza della corsa.
     * @param idMezzo L'ID del mezzo utilizzato per la corsa.
     */
    public static boolean addCorsa(String email, String parcheggio, int idMezzo) {

        String sql = "INSERT INTO Corsa (email_utilizzatore, parcheggio_partenza, id_mezzo) VALUES (?, ?, ?)";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            st.setString(2, parcheggio);
            st.setInt(3, idMezzo);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Corsa aggiunta con successo.");
                return true;
            } else {
                System.out.println("Nessuna corsa aggiunta.");
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

    }



}
