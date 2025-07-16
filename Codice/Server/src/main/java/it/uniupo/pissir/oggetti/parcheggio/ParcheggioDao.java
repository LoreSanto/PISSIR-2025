package it.uniupo.pissir.oggetti.parcheggio;

import it.uniupo.pissir.oggetti.utilis.DbConnect;
import it.uniupo.pissir.oggetti.zona.Zona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ParcheggioDao {

    /**
     * <h2>Recupera tutti i parcheggi dal database</h2>
     *
     * @return una lista di oggetti Parcheggio con i dettagli dei parcheggi e il numero di posti occupati.
     */
    public static ArrayList<Parcheggio> getAllParcheggi() {
        ArrayList<Parcheggio> parcheggi = new ArrayList<>();
        String sql = "SELECT p.nome, p.numero_posti, p.zona, COUNT(m.id_mezzo) AS posti_occupati " +
                "FROM parcheggio p LEFT JOIN Mezzo m ON p.nome = m.parcheggio " +
                "GROUP BY p.nome, p.numero_posti, p.zona ";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Parcheggio parcheggio = new Parcheggio(
                        rs.getString("nome"),
                        rs.getInt("numero_posti"),
                        new Zona(rs.getString("zona")),
                        rs.getInt("posti_occupati")
                );
                parcheggi.add(parcheggio);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Numero parcheggi trovati: " + parcheggi.size());
        return parcheggi;
    }

    /**
     * <h2>Aggiunge un nuovo parcheggio al database</h2>
     *
     * @param nome il nome del parcheggio da aggiungere.
     * @param capienzaMassima la capienza massima del parcheggio.
     * @param zona la zona in cui si trova il parcheggio.
     * @return true se il parcheggio è stato aggiunto con successo, false altrimenti.
     */
    public static boolean addParcheggio(String nome, int capienzaMassima, String zona) {
        String sql = "INSERT INTO Parcheggio (nome, numero_posti, zona)" +
                "VALUES (?, ?, (SELECT nome FROM Zona WHERE nome = ?))";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, nome);
            st.setInt(2, capienzaMassima);
            st.setString(3, zona);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
