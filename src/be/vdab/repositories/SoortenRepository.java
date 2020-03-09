package be.vdab.repositories;

import be.vdab.exceptions.SoortBestaatAlException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                    // De soort bestaat al.
                    connection.commit();
                    throw new SoortBestaatAlException();
                } else {
                    // De soort bestaat nog niet
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
                // Insert successfully executed. Retrieve autonum column ID.
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


    // JDBC 15.4  Batch update (meerdere netwerk paketten)
    // Method die een reeks nieuwe soorten toevoegt aan tabel soorten.
    public void create(List<String> soortNamen) throws SQLException {
        String sql = "insert into soorten (naam) values (?)";
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            // Loop over de ingegeven lijst van nieuwe soort namen en voer het sql commando uit.
            for (String soortNaam : soortNamen) {
                statement.setString(1, soortNaam);
                statement.executeUpdate();
            }
            connection.commit();
            System.out.println(soortNamen.size() + " soorten toegevoegd");
        }
    }

    // JDBC 15.4  Batch update (één netwerk pakket)  Zoals hierboven maar dan met echte batch update
    // Method die een reeks nieuwe soorten toevoegt aan tabel soorten en de ID's geef.
    public List<Long> create2(List<String> soortNamen) throws SQLException {
        String sql = "insert into soorten (naam) values (?)";
        try (Connection connection = super.getConnection();
             // Prepare het statement en zorg ervoor dat de automatisch gegenereerde IDs gekend zijn.
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            for (String soortNaam : soortNamen) {
                statement.setString(1, soortNaam);
                // Voeg het stetement toe aan het netwerk pakket maar verstuur het nog niet.
                statement.addBatch();
            }
            // Stuur het netwerk pakket naar de dB.
            statement.executeBatch();
            // Haal de auto gegenereerde IDs op.
            List<Long> gegenereerdeIds = new ArrayList<>();
            try (ResultSet result = statement.getGeneratedKeys()) {
                while (result.next()) {
                    gegenereerdeIds.add(result.getLong(1));
                }
            }
            connection.commit();
            System.out.println(soortNamen.size() + " soorten toegevoegd");
            return gegenereerdeIds;
        }
    }
}
