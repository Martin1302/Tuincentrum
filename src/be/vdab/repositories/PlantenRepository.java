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
            // Haal de parameters op
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


    // JDBC 10 Transactions
    // Method die de prijs van alle planten boven 100 € met 10% verhoogt en beneden 100 € met 5%
    public void verhoogPrijzenBovenEnOnder100€() throws SQLException {
        String sqlVanaf100 = "update planten set prijs = prijs * 1.1 where prijs >= 100";
        String sqlTot100 = "update planten set prijs = prijs * 1.05 where prijs < 100";
        // Connection en PreparedStatement erfen van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare SQL statements.
             PreparedStatement statementVanaf100 = connection.prepareStatement(sqlVanaf100);
             PreparedStatement statementTot100 = connection.prepareStatement(sqlTot100)) {
            // Zet AutoCommit af. AutoCommit (default = true) voert één commando uit in één transactie
            connection.setAutoCommit(false);
            // Voer SQL commandoos uit onder één transactie.
            statementTot100.executeUpdate();
            statementVanaf100.executeUpdate();
            // Leg alle bewerkingen in de dB uitgevoerd vast dmv commit.
            connection.commit();
        }
    }
}
