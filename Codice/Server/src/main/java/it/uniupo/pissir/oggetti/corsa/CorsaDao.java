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

    /**
     * <h2>Restituisce tutte le corse registrate nel database.</h2>
     *
     */
    public static ArrayList<Corsa> getAllCorse(){
        ArrayList<Corsa> corse = new ArrayList<>();
        //String sql = "SELECT id_corsa, email_utilizzatore AS email_utente, id_mezzo, (strftime('%s', fine_corsa) - strftime('%s', inizio_corsa)) / 60 AS durata_corsa_minuti, costo, parcheggio_partenza, parcheggio_fine, date(inizio_corsa) AS data " +
        //        "FROM Corsa";

        String sql = "SELECT c.id_corsa, c.email_utilizzatore, c.id_mezzo, m. tipo, c.parcheggio_partenza, c.parcheggio_fine, c.inizio_corsa, c.fine_corsa, c.costo " +
                     "FROM Corsa c JOIN Mezzo m ON c.id_mezzo = m.id_mezzo  WHERE c.parcheggio_fine IS NOT NULL ";

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

    /**
     * <h2>Restituisce tutte le corse effettuate da un utente specifico.</h2>
     */
    public static ArrayList<Corsa> getAllCorseUser(String email){
        ArrayList<Corsa> corse = new ArrayList<>();
        //String sql = "SELECT id_corsa, email_utilizzatore AS email_utente, id_mezzo, (strftime('%s', fine_corsa) - strftime('%s', inizio_corsa)) / 60 AS durata_corsa_minuti, costo, parcheggio_partenza, parcheggio_fine, date(inizio_corsa) AS data " +
        //        "FROM Corsa";

        String sql = "SELECT c.id_corsa, c.email_utilizzatore, c.id_mezzo, m. tipo, c.parcheggio_partenza, c.parcheggio_fine, c.inizio_corsa, c.fine_corsa, c.costo " +
                "FROM Corsa c JOIN Mezzo m ON c.id_mezzo = m.id_mezzo " +
                "WHERE c.email_utilizzatore = ? and c.fine_corsa IS NOT NULL";

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

    /**
     * <h2>Restituisce una corsa che è in corso nel database.</h2>
     * @param email L'email dell'utilizzatore che sta effettuato la corsa.
     * @return la corsa dell'utente se c'è, altrimenti restituisce null;
     */
    public static Corsa getCorsaAttualeByEmail(String email) {

        String sql = "SELECT c.id_corsa, c.email_utilizzatore, c.id_mezzo, m. tipo, m.batteria, c.parcheggio_partenza, c.parcheggio_fine, c.inizio_corsa, c.fine_corsa, c.costo " +
                "FROM Corsa c JOIN Mezzo m ON c.id_mezzo = m.id_mezzo Where c.email_utilizzatore = ? AND c.fine_corsa IS NULL";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
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

                int batteria = rs.getInt("batteria");

                return new Corsa(
                        rs.getInt("id_corsa"),
                        new Utente( null, null, rs.getString("email_utilizzatore"), null), // Utente che ha effettuato la corsa
                        new Mezzo(rs.getInt("id_mezzo"), tipo, null, null, batteria, null, null), // Mezzo utilizzato per la corsa
                        new Parcheggio(rs.getString("parcheggio_partenza"), 0, null, 0), // Parcheggio partenza
                        new Parcheggio(rs.getString("parcheggio_fine"), 0, null, 0), // Parcheggio arrivo
                        rs.getString("inizio_corsa"), // Data di partenza
                        rs.getString("fine_corsa"), // Data di arrivo
                        rs.getFloat("costo") // Importo della corsa
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Nessuna corsa in corso trovata per l'email: " + email);
        return null;
    }

    /**
     * <h2>Termina una corsa nel database.</h2>
     * @param idCorsa La corsa da chiudere.
     * @param parcheggioFine Dov'è stato parcheggiato il mezzo.
     * @return id del mezzo parcheggiato
     */
    public static int terminaCorsaById(int idCorsa,String parcheggioFine) {

        try (Connection conn = DbConnect.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            //Imposto fine_corsa e parcheggio_fine
            String updateCorsaSQL = "UPDATE Corsa SET parcheggio_fine = ?, fine_corsa = datetime('now') WHERE id_corsa = ? AND parcheggio_fine IS NULL";
            try (PreparedStatement st1 = conn.prepareStatement(updateCorsaSQL)) {
                st1.setString(1, parcheggioFine);
                st1.setInt(2, idCorsa);
                int rows = st1.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return 0; // Nessuna corsa modificata
                }
            }

            //Calcolo il costo
            // Calcola il costo: 5 euro per i primi 30 minuti, poi 0.10 euro per ogni minuto extra
            String updateCostoSQL = "UPDATE Corsa SET costo = " +
                    "ROUND(5 + CASE WHEN (strftime('%s', datetime('now')) - strftime('%s', inizio_corsa)) / 60 > 30 " +
                    "THEN ((strftime('%s', datetime('now')) - strftime('%s', inizio_corsa)) / 60 - 30) * 0.1 ELSE 0 END, 2) " +
                    "WHERE id_corsa = ?";
            try (PreparedStatement st2 = conn.prepareStatement(updateCostoSQL)) {
                st2.setInt(1, idCorsa);
                st2.executeUpdate();
            }

            //Recupero email_utilizzatore, id_mezzo e tipo ed il costo
            String selectCorsaSQL = "SELECT Corsa.email_utilizzatore, Corsa.id_mezzo, Corsa.costo, Mezzo.tipo " +
                    "FROM Corsa JOIN Mezzo ON Corsa.id_mezzo = Mezzo.id_mezzo " +
                    "WHERE Corsa.id_corsa = ?";
            String email = null;
            int idMezzo = 0;
            double costo = 0;
            String tipoMezzo = null;
            try (PreparedStatement st3 = conn.prepareStatement(selectCorsaSQL)) {
                st3.setInt(1, idCorsa);
                ResultSet rs = st3.executeQuery();
                if( rs.next()) {
                    email = rs.getString("email_utilizzatore");
                    idMezzo = rs.getInt("id_mezzo");
                    costo = rs.getDouble("costo");
                    tipoMezzo = rs.getString("tipo");
                } else {
                    conn.rollback();
                    return 0; // Nessuna corsa trovata
                }

            }

            //Aggiorno il credito e lo stato dell''utilizzatore
            String updateUtenteSQL = "UPDATE Utilizzatore SET credito = ROUND(credito - ?,2), stato = CASE WHEN credito - ? < 0 THEN 'sospeso' ELSE stato END, " +
                    "numeroSospensione = numeroSospensione + CASE WHEN credito - ? < 0 THEN 1 ELSE 0 END WHERE email = ?";
            try (PreparedStatement st4 = conn.prepareStatement(updateUtenteSQL)) {
                st4.setDouble(1, costo);
                st4.setDouble(2, costo);
                st4.setDouble(3, costo);
                st4.setString(4, email);
                if ("BICICLETTA".equalsIgnoreCase(tipoMezzo)) {
                    String aggiornaPuntiSQL = "UPDATE Utilizzatore SET punti = punti + 5 WHERE email = ?";
                    try (PreparedStatement st5 = conn.prepareStatement(aggiornaPuntiSQL)) {
                        st5.setString(1, email);
                        st5.executeUpdate();
                    }
                }
                int rows = st4.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    System.out.println("Errore nell'aggiornamento credito dell'utente: nessun utente trovato con l'email " + email);
                    return 0; // Nessun utente modificato
                }
            }

            //Aggiorno il parcheggio del mezzo
            String updateParcheggioMezzoSQL = "UPDATE Mezzo SET parcheggio = ? WHERE id_mezzo = ?";
            try (PreparedStatement st6 = conn.prepareStatement(updateParcheggioMezzoSQL)) {
                st6.setString(1, parcheggioFine);
                st6.setInt(2, idMezzo);
                int rows = st6.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    System.out.println("Errore nell'aggiornamento del parcheggio del mezzo con id " + idMezzo);
                    return 0; // Mezzo non aggiornato
                }
            }

            conn.commit();
            return idMezzo;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
