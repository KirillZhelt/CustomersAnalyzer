package dev.kirillzhelt.customers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirillzhelt.customers.entity.util.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class CitizenDTO {

    @JsonProperty("citizen_id")
    private int citizenId;

    private String town;
    private String street;
    private String building;
    private int apartment;

    private String name;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate birthDate;

    private Gender gender;

    private List<Integer> relatives;

    public CitizenDTO() {}

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

    public List<Integer> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Integer> relatives) {
        this.relatives = relatives;
    }

    @Override
    public String toString() {
        return "CitizenDTO{" +
            "citizenId=" + citizenId +
            ", town='" + town + '\'' +
            ", street='" + street + '\'' +
            ", building='" + building + '\'' +
            ", apartment=" + apartment +
            ", name='" + name + '\'' +
            ", birthDate=" + birthDate +
            ", gender=" + gender +
            ", relatives=" + relatives +
            '}';
    }
}
