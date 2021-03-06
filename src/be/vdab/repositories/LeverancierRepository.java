package be.vdab.repositories;

import be.vdab.domain.Leverancier;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeverancierRepository extends AbstractRepository {
    // JDBC 6.1 ResultSet - Kolomvolgnummers
    // Method die de namen van alle leveranciers terug geeft.
    public List<String> findAllNamen() throws SQLException {
        String sql = "select naam from leveranciers";
        // Connection, PreparedStatement en ResultSet erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement/ResultSet sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql);
             // Voer het SQL commando uit.
             ResultSet result = statement.executeQuery()) {
            List<String> namen = new ArrayList<>();
            while (result.next()) {
                namen.add(result.getString("naam"));
            }
            return namen;
        }
    }


    // JDBC 6.2 ResultSet - Kolomnamen
    // Method die het aantal leveranciers opzoekt.
    public int findAantalLeveranciers() throws SQLException {
        String sql = "select count(*) as aantal from leveranciers";
        // Connection, PreparedStatement en ResultSet erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement/ResultSet sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql);
             // Voer het SQL commando uit.
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt("aantal");
        }
    }

    // JDBC 6.4 ResultSet - Werkelijkheid
    // Method die alle leveranciers terug geeft
    public List<Leverancier> findAll() throws SQLException {
        String sql = "select id, naam, adres, postcode, woonplaats, sinds from leveranciers";
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql);
             // Voer het SQL commando uit.
             ResultSet result = statement.executeQuery()) {
            List<Leverancier> leveranciers = new ArrayList<>();
            while (result.next()) {
                leveranciers.add(resultNaarLeverancier(result));
            }
            return leveranciers;
        }
    }

    // JDBC 6.4 ResultSet - Werkelijkheid
    // Routine om alle database gegevens in een leveranciers object te zetten.
    private Leverancier resultNaarLeverancier(ResultSet result) throws SQLException {
        return new Leverancier(result.getLong("id"), result.getString("naam"), result.getString("adres"),
                result.getInt("postcode"), result.getString("woonplaats"), result.getDate("sinds").toLocalDate());
    }


    // JDBC 7.1 SQL Statements met parameters
    // Method die alle leveranciers terug geeft die in een bepaalde woonplaats gevestigd zijn.
    public List<Leverancier> findByWoonplaats(String woonplaats) throws SQLException {
        String sql = "select id, naam, adres, postcode, woonplaats, sinds from leveranciers WHERE woonplaats = ?";
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Vul de eerste parameter (eerste "?") in.
            statement.setString(1, woonplaats);
            try (// Voer het SQL commando uit.
                 ResultSet result = statement.executeQuery()) {
                List<Leverancier> leveranciers = new ArrayList<>();
                while (result.next()) {
                    leveranciers.add(resultNaarLeverancier(result));
                }
                return leveranciers;
            }
        }
    }

    // JDBC 8.0 Record lezen aan de hand van ID.
    // Method die een leverancier terug geeft aan de hand van zijn id.
    public Optional<Leverancier> findById(Long id) throws SQLException {
        String sql = "select id, naam, adres, postcode, woonplaats, sinds from leveranciers WHERE id = ?";
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Vul de eerste parameter (eerste "?") in.
            statement.setLong(1, id);
            try (// Voer het SQL commando uit.
                 ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(resultNaarLeverancier(result));
                } else {
                    return Optional.empty();
                }
            }
        }
    }


    // JDBC 13.1 Datum tijd expliciet
    // Method die de leveranciers terug geeft die actief zijn sinds 01/01/2000.
    public List<Leverancier> findLeveranciersBySinds2000() throws SQLException {
        String sql = "select id, naam, adres, postcode, woonplaats, sinds from leveranciers WHERE sinds >= {d '2000-01-01'}";
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try (ResultSet result = statement.executeQuery()) {
                List<Leverancier> leveranciers = new ArrayList<>();
                while (result.next()) {
                    leveranciers.add(resultNaarLeverancier(result));
                }
                connection.commit();
                return leveranciers;
            }
        }
    }


    // JDBC 13.2 Datum tijd als parameter
    // Method die de leveranciers terug geeft die actief zijn sinds 01/01/2000.
    public List<Leverancier> findLeveranciersBySindsVanaf(LocalDate datum) throws SQLException {
        String sql = "select id, naam, adres, postcode, woonplaats, sinds from leveranciers WHERE sinds >= ?";
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            statement.setDate(1, java.sql.Date.valueOf(datum));
            connection.setAutoCommit(false);
            try (ResultSet result = statement.executeQuery()) {
                List<Leverancier> leveranciers = new ArrayList<>();
                while (result.next()) {
                    leveranciers.add(resultNaarLeverancier(result));
                }
                connection.commit();
                return leveranciers;
            }
        }
    }

    // JDBC 13.3 Functies op datum en tijd
    // Method die de leveranciers terug geeft die actief werden in het jaar 2000.
    public List<Leverancier> findLeveranciersGewordenInHetJaar() throws SQLException {
        String sql = "select id, naam, adres, postcode, woonplaats, sinds from leveranciers WHERE {fn year(sinds)} = 2000";
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try (ResultSet result = statement.executeQuery()) {
                List<Leverancier> leveranciers = new ArrayList<>();
                while (result.next()) {
                    leveranciers.add(resultNaarLeverancier(result));
                }
                connection.commit();
                return leveranciers;
            }
        }
    }
}
