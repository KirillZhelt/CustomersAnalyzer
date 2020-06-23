package dev.kirillzhelt.customers.dto;


import java.util.List;

public class ImportDTO {

    private List<CitizenDTO> citizens;

    public ImportDTO() {}

    public List<CitizenDTO> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<CitizenDTO> citizens) {
        this.citizens = citizens;
    }

    @Override
    public String toString() {
        return "ImportDTO{" +
            "citizens=" + citizens +
            '}';
    }
}
