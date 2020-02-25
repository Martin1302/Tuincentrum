package be.vdab.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class AbstractRepository {
    private static final String URL = "jdbc:mysql://localhost/tuincentrum?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Brussels";
    // private static final String USER = "root";
    // private static final String PASSWORD = "mysql";
    // User "cursist" en passwoord "cursist" zijn toegevoegd in MySQL Workbench. Zie cursus JDBC hfdst 3.3
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";

    // Method with package visibility to set up a database connection.
    Connection getConnection() throws SQLException {
        // DriverManager maakt een connectie naar de database.
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
