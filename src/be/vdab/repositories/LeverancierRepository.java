package be.vdab.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeverancierRepository extends AbstractRepository {
    public List<String> findAllNamen() throws SQLException {
        String sql = "select naam from leveranciers";
        // Connection, PreparedStatement en ResultSet erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement/ResultSet sluit.
        // Vraag een connectie aan.
        try (Connection connection = getConnection();
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


    public int findAantalLeveranciers() throws SQLException {
        String sql = "select count(*) as aantal from leveranciers";
        // Connection, PreparedStatement en ResultSet erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement/ResultSet sluit.
        // Vraag een connectie aan.
        try (Connection connection = getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql);
             // Voer het SQL commando uit.
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt("aantal");
        }
    }
}
