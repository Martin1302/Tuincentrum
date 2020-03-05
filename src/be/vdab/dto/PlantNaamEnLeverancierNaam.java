package be.vdab.dto;

// DTO Data Transfer Object
public class PlantNaamEnLeverancierNaam {
    private final String plantNaam;
    private final String leverancierNaam;

    // Constructor
    public PlantNaamEnLeverancierNaam(String plantNaam, String leverancierNaam) {
        this.plantNaam = plantNaam;
        this.leverancierNaam = leverancierNaam;
    }

    public String getPlantNaam() {
        return plantNaam;
    }

    public String getLeverancierNaam() {
        return leverancierNaam;
    }
}
