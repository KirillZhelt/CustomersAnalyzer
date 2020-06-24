package dev.kirillzhelt.customers.dto;


import java.util.List;

public class ImportDTO {

    private List<ImportCitizenDTO> citizens;

    public ImportDTO() {}

    public List<ImportCitizenDTO> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<ImportCitizenDTO> citizens) {
        this.citizens = citizens;
    }

    @Override
    public String toString() {
        return "ImportDTO{" +
            "citizens=" + citizens +
            '}';
    }
}
