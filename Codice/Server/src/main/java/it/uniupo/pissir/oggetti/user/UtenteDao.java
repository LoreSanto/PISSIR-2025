package it.uniupo.pissir.oggetti.user;

import it.uniupo.pissir.oggetti.utilis.DbConnect;
import it.uniupo.pissir.oggetti.utilis.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;

public class UtenteDao {

    /**
     * Recupera un utente amministratore dal database in base all'email e alla password.
     *
     * @param email    l'email dell'utente
     * @param password la password dell'utente
     * @return un oggetto Gestore se l'utente esiste, altrimenti null
     */

    public static Gestore getUtenteAdmin(String email, String password, String tipo) {

        final String sql = "SELECT nome, cognome, email, passwd FROM Utente WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("passwd");
                if (PasswordUtil.checkPassword(password, hashedPassword)) {
                    return new Gestore(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email"),
                            tipo
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Recupera un utente base dal database in base all'email e alla password.
     *
     * @param email    l'email dell'utente
     * @param password la password dell'utente
     * @return un oggetto Utente se l'utente esiste, altrimenti null
     */
    public static Utente getUtenteBase(String email, String password,String tipo) {
        final String sql = "SELECT u.nome, u.cognome, u.email, u.passwd, ut.credito, ut.punti, ut.numeroSospensione, ut.stato " +
                "FROM Utente u JOIN Utilizzatore ut ON u.email = ut.email " +
                "WHERE u.email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("passwd");
                if (PasswordUtil.checkPassword(password, hashedPassword)) {
                    return new Utente(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email"),
                            hashedPassword,
                            rs.getDouble("credito"),
                            rs.getInt("punti"),
                            rs.getInt("numeroSospensione"),
                            rs.getString("stato"),
                            tipo
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Recupera il tipo di utente (user o admin) in base all'email e alla password.
     *
     * @param email    l'email dell'utente
     * @param password la password dell'utente
     * @return il tipo di utente ("user" o "admin") se esiste, altrimenti null
     */
    public static String getTipoUtente(String email, String password) {


        final String sql = "SELECT tipo, passwd FROM Utente WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("passwd");
                if (PasswordUtil.checkPassword(password, hashedPassword)) {
                    return rs.getString("tipo");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * <h2>Aggiunge un nuovo utente al database.</h2>
     *
     * <p>
     *     Questa funzione inserisce un nuovo utente nel database.
     * </p>
     *
     * @param newUtente l'oggetto Utente da aggiungere
     */
    public static Utente addUtente(Utente newUtente) {
        final String sql = "INSERT INTO Utente (nome, cognome, email, passwd) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            String hashedPassword = PasswordUtil.hashPassword(newUtente.getPassword());

            st.setString(1, newUtente.getNome());
            st.setString(2, newUtente.getCognome());
            st.setString(3, newUtente.getEmail());
            st.setString(4, hashedPassword);
            //st.setString(5, "user"); // Imposto il tipo come "user"

            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newUtente;
    }

    /**
     * Verifica se un utente con l'email specificata esiste nel database.
     *
     * @param email l'email da verificare
     * @return true se l'utente esiste, false altrimenti
     */
    public static boolean existsByEmail(String email) {
        final String sql = "SELECT email FROM Utente WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);

            ResultSet rs = st.executeQuery();
            return rs.next(); // ritorna true se esiste almeno una riga

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // in caso di errore o se non esiste
    }

    /**
     * Recupera tutti gli utenti di tipo "user" dal database.
     *
     * @return una lista di oggetti Utente
     */
    public static ArrayList<Utente> getAllUtenti() {
        ArrayList<Utente> utenti = new ArrayList<>();
        // PROBLEMA RISOLTO: aggiunto spazio prima di FROM
        final String sql = "SELECT u.nome, u.cognome, u.email, u.passwd, ut.credito, ut.punti, ut.numeroSospensione, ut.stato " +
                "FROM Utente u JOIN Utilizzatore ut ON u.email = ut.email " +
                "WHERE u.tipo = 'user'";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Utente utente = new Utente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        rs.getString("passwd"),
                        rs.getDouble("credito"),
                        rs.getInt("punti"),
                        rs.getInt("numeroSospensione"),
                        rs.getString("stato"),
                        "user" // Valore fisso dato che filtri per tipo = 'user'
                );
                utenti.add(utente);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Consiglio: stampa anche il messaggio dell'errore per debug
            System.err.println("Errore SQL: " + e.getMessage());
        }

        System.out.println("Numero utenti trovati: " + utenti.size());
        return utenti;
    }

    /**
     * Aggiorna lo stato di un utente nel database.
     *
     * @param email            l'email dell'utente da aggiornare
     * @param nuovoStato       il nuovo stato dell'utente
     * @param numeroSospensione il numero di sospensioni dell'utente
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public static boolean aggiornaStatoUtente(String email, String nuovoStato, int numeroSospensione) {
        final String sql = "UPDATE Utilizzatore SET stato = ?, numeroSospensione = ? WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, nuovoStato);
            st.setInt(2, numeroSospensione);
            st.setString(3, email);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0; // Ritorna true se almeno una riga è stata aggiornata

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // In caso di errore, ritorna false
        }
    }

    /**
     * <h2>Ricarico il credito dell'utente</h2>
     * @param email mail dell'utente che richiede la ricarica
     * @param importo importo che l'utente vuole ricaricare
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public static boolean ricaricaCredito(String email, double importo) {

        final String sql = "UPDATE Utilizzatore SET credito = credito + ? WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setBigDecimal(1, java.math.BigDecimal.valueOf(importo));//converto il double in decimal per il database
            st.setString(2, email);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0; // Ritorna true se almeno una riga è stata aggiornata

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // In caso di errore, ritorna false
        }

    }

    /**
     * <h2>Converte i punti in credito</h2>
     * @param email mail dell'utente che richiede la conversione
     * @param punti punti da convertire
     * @param importo importo che l'utente vuole ricaricare
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public static boolean convertiPunti(String email, int punti, double importo){
        final String sql = "UPDATE Utilizzatore SET credito = ?, punti = ? WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setBigDecimal(1, java.math.BigDecimal.valueOf(importo));//converto il double in decimal per il database
            st.setInt(2, punti);
            st.setString(3, email);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0; // Ritorna true se almeno una riga è stata aggiornata

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // In caso di errore, ritorna false
        }
    }

    //-------------------------------------------------------------------

    /**
     * <h2>Aggiorna dato della ricarica dell'utente</h2>
     * @param email mail dell'utente che richiede la ricarica
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public static Utente getDatiUtenteByMail(String email) {
        final String sql = "SELECT * FROM Utilizzatore WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return new Utente(
                        null,
                        null,
                        null,
                        null,
                        rs.getDouble("credito"),
                        rs.getInt("punti"),
                        rs.getInt("numeroSospensione"),
                        rs.getString("stato"),
                        null
                );

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    //-------------------------------------------------------------------



    /**
     * <h2>Verifica che l'utente non sia sospeso</h2>
     * @param email mail dell'utente da verificare
     * @return true se l'utente non è sospeso, false altrimenti
     */
    public static boolean isUtenteSospeso(String email) {
        final String sql = "SELECT stato FROM Utilizzatore WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String stato = rs.getString("stato");
                return "sospeso".equalsIgnoreCase(stato);//ritorna true se lo stato è "sospeso"
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // In caso di errore o se l'utente non è sospeso
    }

    /**
     * <h2>Aumenta il numero di sospensioni dell'utente</h2>
     * @param email mail dell'utente da aggiornare
     */
    public static void upNumeroSospensioni(String email) {
        final String sql = "UPDATE Utilizzatore SET numeroSospensione = numeroSospensione + 1 WHERE email = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
