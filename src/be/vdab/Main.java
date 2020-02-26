package be.vdab;

import be.vdab.domain.Leverancier;
import be.vdab.repositories.LeverancierRepository;
import be.vdab.repositories.PlantenRepository;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // JDBC 5 PreparedStatement
        PlantenRepository plantenRepository = new PlantenRepository();
        try {
            // Verhoog van alle planten de prijs met 10%.
            System.out.println(plantenRepository.verhoogAllePrijzenMet10Percent());
            System.out.println("Alle planten aangepast.\n");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 6 ResultSet
        LeverancierRepository leverancierRepository = new LeverancierRepository();
        try {
            // JDBC 6.1 ResultSet - Kolomvolgnummers
            // Zoek alle namen van de leveranciers.
            for (String naam : leverancierRepository.findAllNamen()) {
                System.out.println(naam);
            }

            // JDBC 6.2 ResultSet - Kolomnamen
            // Bepaal hoeveel leveranciers er zijn.
            System.out.println("\nAantal leveranciers : " + leverancierRepository.findAantalLeveranciers() + "\n");

            // JDBC 6.4 ResultSet - Werkelijkheid
            // Laat alle gegevens zien van alle leveranciers.
            for (Leverancier leverancier : leverancierRepository.findAll()) {
                System.out.println(leverancier);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
