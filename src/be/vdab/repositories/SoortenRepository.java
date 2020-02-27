package be.vdab.repositories;

import java.io.Serializable;
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

                } else {

                }
            }
        }
    }
}
