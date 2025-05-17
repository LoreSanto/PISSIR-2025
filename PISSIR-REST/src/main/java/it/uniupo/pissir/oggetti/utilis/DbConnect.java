package it.uniupo.pissir.oggetti.utilis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    //Inserimento variabili per la locazione del database
    private static final String dbLoc = "jdbc:sqlite:src/main/resources/database.db";
    private static DbConnect instance = null;

    private DbConnect() {
        instance = this;
    }

    public static DbConnect getInstance() {
        if (instance == null)
            return new DbConnect();
        else {
            return instance;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            // return the connection instance
            return DriverManager.getConnection(dbLoc);
        } catch (SQLException e) {
            throw new SQLException("Cannot get connection to " + dbLoc, e);
        }
    }
}
