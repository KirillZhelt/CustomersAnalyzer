package dev.kirillzhelt.customers.entity;

import org.springframework.data.annotation.Id;

public class Relative {

    @Id
    private Integer id;

    private int importId;
    private int citizenId;
    private int relativeId;

    public Relative() {}

    public Relative(int importId, int citizenId, int relativeId) {
        this.importId = importId;
        this.citizenId = citizenId;
        this.relativeId = relativeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public int getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(int citizenId) {
        this.citizenId = citizenId;
    }

    public int getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(int relativeId) {
        this.relativeId = relativeId;
    }

    @Override
    public String toString() {
        return "Relative{" +
            "id=" + id +
            ", importId=" + importId +
            ", citizenId=" + citizenId +
            ", relativeId=" + relativeId +
            '}';
    }
}
