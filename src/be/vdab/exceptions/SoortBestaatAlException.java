package be.vdab.exceptions;

// JDBC 11 Isolation level
// Exception voor het geval wanneer een soort zal toegevoegd worden aan de tabel soorten die al bestaat.
public class SoortBestaatAlException extends RuntimeException {
    private static final Long serialVersionUID = 1L;
}
