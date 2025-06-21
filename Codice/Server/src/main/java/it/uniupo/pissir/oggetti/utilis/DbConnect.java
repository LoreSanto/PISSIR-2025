package it.uniupo.pissir.oggetti.utilis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    // Inserimento variabili per localizzazione DB
    static private final String dbLoc = "jdbc:sqlite:src/main/resources/database.db";
    static private DbConnect instance = null;

    /**
     * <h2>Costruttore privato della classe</h2>
     *
     */
    private DbConnect() {
        instance = this;
    }

    /**
     * <h2>Metodo per ottenere l'istanza della connessione al database</h2>
     * <p>
     *     Questo metodo istanzia la connessione al database se non è già stata
     * </p>
     * @return istanza della connessione al database
     */
    public static DbConnect getInstance() {
        if (instance == null)
            return new DbConnect();
        else {
            return instance;
        }
    }

    /**
     * <h2>Metodo per ottenere la connessione al database</h2>
     * <p>
     *     Questo metodo restituisce la connessione al database
     * </p>
     * @return connessione al database
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        try {
            //ritorna l'instance della connessione
            return DriverManager.getConnection(dbLoc);
        } catch (SQLException e) {
            throw new SQLException("Cannot get connection to " + dbLoc, e);
        }
    }
}
