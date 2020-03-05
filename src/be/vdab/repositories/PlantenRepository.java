package be.vdab.repositories;

import be.vdab.exceptions.PlantNietGevondenException;
import be.vdab.exceptions.PrijsTeLaagException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlantenRepository extends AbstractRepository {
    // JDBC 5 PreparedStatement
    // Method die de prijs van alle planten met 10% verhoogt.
    public int verhoogAllePrijzenMet10Percent() throws SQLException {
        String sql = "update planten set prijs = prijs * 1.1";
        // Connection en PreparedStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
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
        // Connection en CallableStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
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
        // Connection en PreparedStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
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

    // JDBC 14.1  Lock
    // Method die de prijs van een plant maximaal tot de helft verminderd.
    public void verlaagPrijs(long id, BigDecimal nieuwePrijs) throws SQLException {
        // Define sql command. The "for update" locks the read record until the end of the transaction.
        String sqlSelect = "select prijs from planten where id = ? for update";
        // Connection en PreparedStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare SQL statements.
             PreparedStatement statementSelect = connection.prepareStatement(sqlSelect)) {
            // Fill in user parameter
            statementSelect.setLong(1, id);
            // Define isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Zet AutoCommit af. AutoCommit (default = true) voert één commando uit in één transactie
            connection.setAutoCommit(false);
            // Voer SQL commandoos uit onder één transactie.
            try (ResultSet result = statementSelect.executeQuery()) {
                // Plant Id gevonden ?
                if (result.next()) {
                    BigDecimal oudePrijs = result.getBigDecimal("prijs");
                    BigDecimal minimumNieuwePrijs = oudePrijs.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    // Voeldoet nieuwe prijs aan de vereiste minimum prijs?
                    if (nieuwePrijs.compareTo(minimumNieuwePrijs) >= 0) {
                        String sqlUpdate = "update planten set prijs = ? where id = ?";
                        try (PreparedStatement statementUpdate = connection.prepareStatement(sqlUpdate)) {
                            // Fill in user parameters
                            statementUpdate.setBigDecimal(1, nieuwePrijs);
                            statementUpdate.setLong(2, id);
                            // Voer sql command uit
                            statementUpdate.executeUpdate();
                            // Leg alle bewerkingen in de dB uitgevoerd vast dmv commit.
                            connection.commit();
                            return;
                        }
                    }
                    // Prijs te laag
                    connection.rollback();
                    throw new PrijsTeLaagException();
                }
                // Plant niet gevonden. Unlock read record.
                connection.rollback();
                throw new PlantNietGevondenException();
            }
        }
    }


    // JDBC 14.2  Lock (Variant op 14.1)
    // Method die de prijs van een plant maximaal tot de helft verminderd.
    public void verlaagPrijs2(long id, BigDecimal nieuwePrijs) throws SQLException {
        // Define sql command. The "for update" locks the read record until the end of the transaction.
        String sqlUpdate = "update planten set prijs = ? where id = ? and ? > prijs/2";
        // Connection en PreparedStatement erven van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie/statement sluit.
        // Vraag een connectie aan.
        try (Connection connection = super.getConnection();
             // Prepare SQL statements.
             PreparedStatement statementUpdate = connection.prepareStatement(sqlUpdate)) {
            // Fill in user parameters
            statementUpdate.setBigDecimal(1, nieuwePrijs);
            statementUpdate.setLong(2, id);
            statementUpdate.setBigDecimal(3, nieuwePrijs);
            // Define isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Zet AutoCommit af. AutoCommit (default = true) voert één commando uit in één transactie
            connection.setAutoCommit(false);
            // Voer SQL commandoos uit onder één transactie.
            int aantalAangepast = statementUpdate.executeUpdate();
            // Update correct verlopen ?
            if (aantalAangepast == 1) {
                connection.commit();
                return;
            }

            // Update niet correct verlopen. Wat is de oorzaak ? Plant bestaat niet of prijs te laag ?
            String sqlSelect = "select count(*) as aantal from planten where id = ?";
            try (PreparedStatement statementSelect = connection.prepareStatement(sqlSelect)) {
                // Fill in user parameter
                statementSelect.setLong(1, id);
                try (ResultSet result = statementSelect.executeQuery()) {
                    result.next();
                    if (result.getInt("aantal") == 0) {
                        // Plant niet gevonden
                        connection.rollback();
                        throw new PlantNietGevondenException();
                    }
                    // Prijs te laag
                    connection.rollback();
                    throw new PrijsTeLaagException();
                }
            }
        }
    }


    // JDBC 15.2  Het SQL Keyword
    // Method die de namen van de planten laat zien aan de hand van id's ingegeven door de gebruiker
    public List<String> findNamenByIds(Set<Long> ids) throws SQLException {
        StringBuilder sql = new StringBuilder("select naam from planten where id in (");
        for (int i = 0; i != ids.size(); i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length() - 1, ')');
        try (Connection connection = super.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            for (long id : ids) {
                statement.setLong(index++, id);
            }
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try (ResultSet result = statement.executeQuery()) {
                List<String> plantenNamen = new ArrayList<>();
                while (result.next()) {
                    plantenNamen.add(result.getString("naam"));
                }
                connection.commit();
                return plantenNamen;
            }
        }
    }
}
