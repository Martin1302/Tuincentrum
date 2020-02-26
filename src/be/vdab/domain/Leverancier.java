package be.vdab.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// JDBC 6.4 ResultSet - Werkelijkheid
public class Leverancier {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final long id;
    private final String naam;
    private final String adres;
    private final int postcode;
    private final String woonplaats;
    private final LocalDate sinds;

    // Constructor
    public Leverancier(long id, String naam, String adres, int postcode, String woonplaats, LocalDate sinds) {
        this.id = id;
        this.naam = naam;
        this.adres = adres;
        this.postcode = postcode;
        this.woonplaats = woonplaats;
        this.sinds = sinds;
    }

    @Override
    public String toString() {
        return id + ":" + naam + " (" + woonplaats + ") " + sinds.format(FORMATTER);
    }
}
