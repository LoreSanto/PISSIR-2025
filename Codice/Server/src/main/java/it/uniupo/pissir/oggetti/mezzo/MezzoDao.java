package it.uniupo.pissir.oggetti.mezzo;

import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.utilis.DbConnect;
import it.uniupo.pissir.oggetti.zona.Zona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MezzoDao {

    //ritorna tutti i mezzi
    public static ArrayList<Mezzo> getAllMezzi() {
        ArrayList<Mezzo> mezzi = new ArrayList<>();
        final String sql = "SELECT m.id_mezzo, m.tipo, p.zona, m.parcheggio, m.batteria, m.status, p.numero_posti, m.codice_IMEI" +
                " FROM Mezzo m JOIN Parcheggio p ON m.parcheggio = p.nome";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                TipoMezzo tipo;
                StatoMezzo stato;

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

                switch (rs.getString("status")) {
                    case "PRELEVABILE":
                        stato = StatoMezzo.PRELEVABILE;
                        break;
                    case "NON_DISPONIBILE":
                        stato = StatoMezzo.NON_DISPONIBILE;
                        break;
                    case "IN_USO":
                        stato = StatoMezzo.IN_USO;
                        break;
                    default:
                        throw new IllegalArgumentException("Stato del mezzo sconosciuto: " + rs.getString("status"));
                }

                Mezzo mezzo = new Mezzo(
                        rs.getInt("id_mezzo"),
                        tipo,
                        new Zona(rs.getString("zona")),
                        new Parcheggio(rs.getString("parcheggio"), rs.getInt("numero_posti"), new Zona(rs.getString("zona")), 0),
                        rs.getInt("batteria"),
                        stato,
                        rs.getString("codice_IMEI")
                );
                mezzi.add(mezzo);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Numero mezzi trovati: " + mezzi.size());
        return mezzi;
    }

    //ritorna un mezzi disponibili
    public static ArrayList<Mezzo> getMezziDisponibili() {
        ArrayList<Mezzo> mezzi = new ArrayList<>();
        final String sql = "SELECT m.id_mezzo, m.tipo, p.zona, m.parcheggio, m.batteria, m.status, p.numero_posti, m.codice_IMEI" +
                " FROM Mezzo m JOIN Parcheggio p ON m.parcheggio = p.nome" +
                " WHERE m.status = 'PRELEVABILE'";

        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                TipoMezzo tipo;
                StatoMezzo stato;

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

                switch (rs.getString("status")) {
                    case "PRELEVABILE":
                        stato = StatoMezzo.PRELEVABILE;
                        break;
                    case "NON_DISPONIBILE":
                        stato = StatoMezzo.NON_DISPONIBILE;
                        break;
                    case "IN_USO":
                        stato = StatoMezzo.IN_USO;
                        break;
                    default:
                        throw new IllegalArgumentException("Stato del mezzo sconosciuto: " + rs.getString("status"));
                }

                Mezzo mezzo = new Mezzo(
                        rs.getInt("id_mezzo"),
                        tipo,
                        new Zona(rs.getString("zona")),
                        new Parcheggio(rs.getString("parcheggio"), rs.getInt("numero_posti"), new Zona(rs.getString("zona")), 0),
                        (tipo == TipoMezzo.BICICLETTA) ? -1 : rs.getInt("batteria"),
                        stato,
                        rs.getString("codice_IMEI")
                );
                mezzi.add(mezzo);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Numero mezzi trovati: " + mezzi.size());
        return mezzi;
    }

    public static boolean aggiornaMezzo(Mezzo mezzo) {
        final String sql = "UPDATE Mezzo SET parcheggio = ?, batteria = ?, status = ? WHERE id_mezzo = ?";
        try (Connection conn = DbConnect.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, mezzo.getParcheggio().getNome());
            st.setInt(2, mezzo.getBatteria());
            st.setString(3, mezzo.getStato().toString());
            st.setInt(4, mezzo.getId());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setStatusMezzo(int idMezzo, String newStatus) {
        final String sql = "UPDATE Mezzo SET status = ? WHERE id_mezzo = ?";

        try (Connection conn = DbConnect.getInstance().getConnection();

             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, newStatus);
            st.setInt(2, idMezzo);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
