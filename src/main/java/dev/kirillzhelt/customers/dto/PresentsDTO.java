package dev.kirillzhelt.customers.dto;

public class PresentsDTO {

    private Integer citizenId;
    private Integer presents;

    public PresentsDTO() {}

    public PresentsDTO(Integer citizenId, Integer presents) {
        this.citizenId = citizenId;
        this.presents = presents;
    }

    public Integer getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Integer citizenId) {
        this.citizenId = citizenId;
    }

    public Integer getPresents() {
        return presents;
    }

    public void setPresents(Integer presents) {
        this.presents = presents;
    }

    @Override
    public String toString() {
        return "PresentsDTO{" +
            "citizenId=" + citizenId +
            ", presents=" + presents +
            '}';
    }
}
