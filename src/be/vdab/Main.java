package be.vdab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final String URL = "jdbc:mysql://localhost/tuincentrum?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Brussels";
    // private static final String USER = "root";
    // private static final String PASSWORD = "mysql";
    // User "cursist" en passwoord "cursist" zijn toegevoegd in MySQL Workbench. Zie cursus JDBC hfdst 3.3
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";

    public static void main(String[] args) {
        // Connection erft van AutoCloseable waardoor compiler zelf finally blok toevoegt die de connectie sluit.
        // DriverManager maakt een connectie naar de database.
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connectie geopened");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
