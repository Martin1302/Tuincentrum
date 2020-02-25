package be.vdab;

import be.vdab.repositories.LeverancierRepository;
import be.vdab.repositories.PlantenRepository;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        PlantenRepository plantenRepository = new PlantenRepository();
        try {
            // Verhoog de prijs van alle planten met 10 %
            System.out.println(plantenRepository.verhoogAllePrijzenMet10Percent());
            System.out.println("Alle planten aangepast.\n");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }


        LeverancierRepository leverancierRepository = new LeverancierRepository();
        try {
            // Zoek alle namen van de leveranciers.
            for (String naam : leverancierRepository.findAllNamen()) {
                System.out.println(naam);
            }

            // Bepaal hoeveel leveranceirs er zijn.
            System.out.println("\nAantal leveranciers : " + leverancierRepository.findAantalLeveranciers());
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
