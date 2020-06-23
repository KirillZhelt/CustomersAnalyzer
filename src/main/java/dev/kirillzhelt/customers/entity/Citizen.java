package dev.kirillzhelt.customers.entity;

import dev.kirillzhelt.customers.entity.util.Gender;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

public class Citizen {

    @Id
    private Integer id;

    private int importId;
    private int citizenId;
    private String town;
    private String street;
    private String building;
    private int apartment;
    private String name;
    private LocalDate birthDate;
    private Gender gender;

    public Citizen() {}

    public Citizen(int importId, int citizenId, String town, String street, String building, int apartment, String name, LocalDate birthDate, Gender gender) {
        this.importId = importId;
        this.citizenId = citizenId;
        this.town = town;
        this.street = street;
        this.building = building;
        this.apartment = apartment;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getApartment() {
        return apartment;
    }

    public void setApartment(int apartment) {
        this.apartment = apartment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Citizen{" +
            "id=" + id +
            ", importId=" + importId +
            ", citizenId=" + citizenId +
            ", town='" + town + '\'' +
            ", street='" + street + '\'' +
            ", building='" + building + '\'' +
            ", apartment=" + apartment +
            ", name='" + name + '\'' +
            ", birthDate=" + birthDate +
            ", gender=" + gender +
            '}';
    }
}
