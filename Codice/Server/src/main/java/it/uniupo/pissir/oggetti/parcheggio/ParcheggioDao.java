package it.uniupo.pissir.oggetti.parcheggio;

import it.uniupo.pissir.oggetti.utilis.DbConnect;
import it.uniupo.pissir.oggetti.zona.Zona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ParcheggioDao {

    //ritorna tutti i parcheggi
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
}
