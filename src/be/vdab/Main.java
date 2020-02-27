package be.vdab;

import be.vdab.domain.Leverancier;
import be.vdab.repositories.LeverancierRepository;
import be.vdab.repositories.PlantenRepository;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

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


        // JDBC 7.1 SQL Statements met parameters
        // Toon alle leveranciers in een bepaalde woonplaats.
        System.out.println("\nWoonplaats :");
        Scanner scanner = new Scanner(System.in);
        String woonplaats = scanner.nextLine();
        try {
            for (Leverancier leverancier : leverancierRepository.findByWoonplaats(woonplaats)) {
                System.out.println(leverancier);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 8.0 Record lezen aan de hand van ID.
        // Vraag een leverancier op aan de hand van een id.
        System.out.println("\nLeverancier Id :");
        Long id = scanner.nextLong();
        try {
            Optional<Leverancier> optionalLeverancier = leverancierRepository.findById(id);
            if (optionalLeverancier.isPresent()) {
                System.out.println(optionalLeverancier.get());
            } else {
                System.out.println("Leverancier met id = " + id + " niet gevonden!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 9 Stored procedures
        // Method die alle planten opzoekt waarin een bepaald woord voorkomt.
        System.out.println("\nType een woord in waarop je alle planten mee zal zoeken waarin dit woord voorkomt:");
        scanner.skip("\n");
        String woord = scanner.nextLine();
        try {
            for (String plant : plantenRepository.findNamenByWoord(woord)) {
                System.out.println(plant);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
