package be.vdab.repositories;

import be.vdab.exceptions.SoortBestaatAlException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SoortenRepository extends AbstractRepository {
    // JDBC 11 Isolation level
    // Method die een soort zal toevoegen aan tabel soorten.
    public void create(String naam) throws SQLException {
        String select = "select id from soorten where naam = ?";
        String insert = "insert into soorten (naam) values (?)";
        // Connection en PreparedStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statementSelect = connection.prepareStatement(select)) {
            // Haal de parameters op
            statementSelect.setString(1, naam);
            // Set Isolation level op highest level : Serializable
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            // Zet AutoCommit af. AutoCommit (default = true) voert één commando uit in één transactie
            connection.setAutoCommit(false);
            try (// Voer SQL commandoos uit onder één transactie.
                 ResultSet result = statementSelect.executeQuery()) {
                if (result.next()) {
                    connection.commit();
                    throw new SoortBestaatAlException();
                } else {
                    try (PreparedStatement statementInsert = connection.prepareStatement(insert)) {
                        statementInsert.setString(1, naam);
                        statementInsert.executeUpdate();
                        connection.commit();
                    }
                }
            }
        }
    }


    // JDBC 12 Autonum kolommen
    // Method die een soort zal toevoegen aan tabel soorten. Quasi identiek aan JDBC 11.
    public long create2(String naam) throws SQLException {
        String select = "select id from soorten where naam = ?";
        String insert = "insert into soorten (naam) values (?)";
        // Connection en PreparedStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Specify to return the autonumber value for ID.
             PreparedStatement statementInsert = connection.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statementInsert.setString(1, naam);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try {
                statementInsert.executeUpdate();
                try (ResultSet result = statementInsert.getGeneratedKeys()) {
                    result.next();
                    // Get the new column ID
                    long nieuweID = result.getLong(1);
                    // Close transaction
                    connection.commit();
                    return nieuweID;
                }
            } catch (SQLException ex) {
                try (PreparedStatement statementSelect = connection.prepareStatement(select)) {
                    statementSelect.setString(1, naam);
                    try (ResultSet result = statementSelect.executeQuery()) {
                        if (result.next()) {
                            // De opgegeven soort was reeds aanwezig.
                            connection.commit();
                            // Throw de exceptie dat de soort al bestond.
                            throw new SoortBestaatAlException();
                        } else {
                            // De opgegeven soort was niet aanwezig. Er is iets anders fout gegaan.
                            // Throw de originele SQLException verder door.
                            connection.commit();
                            throw ex;
                        }
                    }
                }
            }
        }
    }
}
