package be.vdab;

import be.vdab.domain.Leverancier;
import be.vdab.dto.PlantNaamEnLeverancierNaam;
import be.vdab.exceptions.PlantNietGevondenException;
import be.vdab.exceptions.PrijsTeLaagException;
import be.vdab.exceptions.SoortBestaatAlException;
import be.vdab.repositories.LeverancierRepository;
import be.vdab.repositories.PlantenRepository;
import be.vdab.repositories.SoortenRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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


        // JDBC 10 Transactions
        // Method die de prijs van alle planten boven 100 € met 10% verhoogt en beneden 100 € met 5%
        System.out.println("\nAlle planten prijzen boven 100 € worden verhoogd met 10%. Die eronder met 5%. Alles in één transactie.");
        try {
            plantenRepository.verhoogPrijzenBovenEnOnder100€();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 11 Isolation level
        // Method die een soort zal toevoegen aan tabel soorten.
        System.out.println("\nVoeg een nieuwe planten soort toe : naam = ");
        String naam = scanner.nextLine();
        SoortenRepository soortenRepository = new SoortenRepository();
        try {
            soortenRepository.create(naam);
            System.out.println("Nieuwe planten soort toegevoegd.");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } catch (SoortBestaatAlException ex) {
            System.out.println("De planten soort bestaat al.");
        }


        // JDBC 12 Autonum kolommen
        // Method die een soort zal toevoegen aan tabel soorten. Quasi identiek aan JDBC 11.
        System.out.println("\nVoeg een nieuwe planten soort toe : naam =  en return the autonum gegenereerde ID.");
        naam = scanner.nextLine();
        try {
            long nieuweID = soortenRepository.create2(naam);
            System.out.println("Nieuwe planten soort toegevoegd. Het autonum field is : " + nieuweID);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } catch (SoortBestaatAlException ex) {
            System.out.println("De planten soort bestaat al.");
        }


        // JDBC 13.1 Datum tijd expliciet
        // Method die de leveranciers terug geeft die actief zijn na een bepaalde datum.
        System.out.println("\nAlle leveranciers die actief zijn na 01/01/2000 :");
        try {
            for (Leverancier leverancier : leverancierRepository.findLeveranciersBySinds2000()) {
                System.out.println(leverancier);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 13.2 Datum tijd als parameter
        // Method die de leveranciers terug geeft die actief zijn na een bepaalde datum.
        System.out.println("\nAlle leveranciers die actief zijn na uw ingegeven datum (dd/mm/yyyy) :");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
        LocalDate datum = LocalDate.parse(scanner.nextLine(), formatter);
        try {
            for (Leverancier leverancier : leverancierRepository.findLeveranciersBySindsVanaf(datum)) {
                System.out.println(leverancier);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }

        // JDBC 13.3 Functies op datum en tijd
        // Method die de leveranciers terug geeft die actief werden in het jaar 2000.
        System.out.println("\nAlle leveranciers die actief geworden zijn in het jaar 2000.");
        try {
            for (Leverancier leverancier : leverancierRepository.findLeveranciersGewordenInHetJaar()) {
                System.out.println(leverancier);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 14.1  Lock
        // Method die de prijs van een plant maximaal tot de helft verminderd.
        System.out.println("\nGeef de ID van de plant wiens prijs verlaagt wordt :");
        long plantId = scanner.nextLong();
        System.out.println("Geef nieuwe prijs voor de plant :");
        BigDecimal nieuwePrijs = scanner.nextBigDecimal();
        try {
            plantenRepository.verlaagPrijs(plantId, nieuwePrijs);
            System.out.println("Prijs aangepast");
        } catch (PlantNietGevondenException ex) {
            System.out.println("Plant niet gevonden");
        } catch (PrijsTeLaagException ex) {
            System.out.println("Nieuwe prijs voldoet niet aan minimum prijs");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 14.2  Lock
        // Method die de prijs van een plant maximaal tot de helft verminderd.
        System.out.println("\nGeef de ID van de plant wiens prijs verlaagt wordt :");
        plantId = scanner.nextLong();
        System.out.println("Geef nieuwe prijs voor de plant :");
        nieuwePrijs = scanner.nextBigDecimal();
        try {
            plantenRepository.verlaagPrijs2(plantId, nieuwePrijs);
            System.out.println("Prijs aangepast");
        } catch (PlantNietGevondenException ex) {
            System.out.println("Plant niet gevonden");
        } catch (PrijsTeLaagException ex) {
            System.out.println("Nieuwe prijs voldoet niet aan minimum prijs");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 15.2  Het SQL Keyword
        // Method die de namen van de planten laat zien aan de hand van id's ingegeven door de gebruiker
        System.out.println("Geef een aantal planten Id's op waarvan je de planten naam wilt weten (0 om te stoppen):");
        Set<Long> plantenIds = new HashSet<>();
        long plantenId = scanner.nextInt();
        while (plantenId != 0) {
            plantenIds.add(plantenId);
            plantenId = scanner.nextInt();
        }
        try {
            for (String plantenNaam : plantenRepository.findNamenByIds(plantenIds)) {
                System.out.println(plantenNaam);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 15.3  Join
        // Method die de naam van de plant en de naam van de leverancier geeft voor rode planten.
        System.out.println("\nNaam van alle rode planten en hun leverancier naam : ");
        try {
            for (PlantNaamEnLeverancierNaam naamDuo : plantenRepository.findRodePlantenEnHunLeveranciers()) {
                System.out.println(naamDuo.getPlantNaam() + " - " + naamDuo.getLeverancierNaam());
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 15.4  Batch update (meerdere netwerk paketten)
        // Method die een reeks nieuwe soorten toevoegt aan tabel soorten.
        System.out.println("\nVoeg een aantal nieuwe soorten toe (STOP om te eindigen) :");
        List<String> nieuweSoortNamen = new ArrayList<>();
        String nieuweSoortNaam = scanner.nextLine();
        while (!"STOP".equals(nieuweSoortNaam)) {
            nieuweSoortNamen.add(nieuweSoortNaam);
            nieuweSoortNaam = scanner.nextLine();
        }
        try {
            // Voeg de nieuwe soorten toe
            soortenRepository.create(nieuweSoortNamen);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        // JDBC 15.4  Batch update (één netwerk pakket)  Zoals hierboven maar dan met echte batch update
        // Method die een reeks nieuwe soorten toevoegt aan tabel soorten en de ID's geef.
        System.out.println("\nVoeg een aantal nieuwe soorten toe (STOP om te eindigen)  en toon hun auto gegenereerde Ids");
        nieuweSoortNamen.clear();
        nieuweSoortNaam = scanner.nextLine();
        while (!"STOP".equals(nieuweSoortNaam)) {
            nieuweSoortNamen.add(nieuweSoortNaam);
            nieuweSoortNaam = scanner.nextLine();
        }
        try {
            // Voeg de nieuwe soorten toe en laat de Ids zien.
            List<Long> gegenereerdeIds = soortenRepository.create2(nieuweSoortNamen);
            for (long gegenereerdeId : gegenereerdeIds) {
                System.out.println(gegenereerdeId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
