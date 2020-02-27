package be.vdab.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlantenRepository extends AbstractRepository {
    // JDBC 5 PreparedStatement
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


    // JDBC 9 Stored procedures
    // Method die alle planten opzoekt waarin een bepaald woord voorkomt.
    public List<String> findNamenByWoord(String woord) throws SQLException {
        String call = "{call PlantNamenMetEenWoord(?)}";
        // Connection en CallableStatement erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare de stored procedure call.
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, "%" + woord + "%");
            // Voer de stored procedure call uit.
            try (ResultSet result = statement.executeQuery()) {
                List<String> plantenNamen = new ArrayList<>();
                while (result.next()) {
                    plantenNamen.add(result.getString("naam"));
                }
                return plantenNamen;
            }
        }
    }
}
