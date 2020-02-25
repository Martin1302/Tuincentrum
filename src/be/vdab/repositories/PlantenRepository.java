package be.vdab.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlantenRepository extends AbstractRepository {
    // Method die de prijs van alle planten met 10% verhoogt.
    public int verhoogAllePrijzenMet10Percent() throws SQLException {
        String sql = "update planten set prijs = prijs * 1.1";
        // Connection en PreparedStatement erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare het SQL statement.
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Voer het SQL commando uit.
            return statement.executeUpdate();
        }
    }
}
