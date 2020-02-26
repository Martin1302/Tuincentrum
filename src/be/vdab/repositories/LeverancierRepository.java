package be.vdab.repositories;

import be.vdab.domain.Leverancier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            try (
                    // Voer het SQL commando uit.
                    ResultSet result = statement.executeQuery()) {
                List<Leverancier> leveranciers = new ArrayList<>();
                while (result.next()) {
                    leveranciers.add(resultNaarLeverancier(result));
                }
                return leveranciers;
            }
        }
    }
}
