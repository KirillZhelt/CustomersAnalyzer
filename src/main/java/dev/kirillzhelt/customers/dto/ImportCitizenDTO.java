package dev.kirillzhelt.customers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirillzhelt.customers.entity.util.Gender;
import dev.kirillzhelt.customers.validator.constraint.BeforeTodayConstraint;
import dev.kirillzhelt.customers.validator.constraint.OneNumberOrLetterConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class ImportCitizenDTO {

    @JsonProperty("citizen_id")
    @NotNull
    @Min(0)
    private Integer citizenId;

    @NotNull
    @Size(min = 1, max = 256)
    @OneNumberOrLetterConstraint
    private String town;

    @NotNull
    @Size(min = 1, max = 256)
    @OneNumberOrLetterConstraint
    private String street;

    @NotNull
    @Size(min = 1, max = 256)
    @OneNumberOrLetterConstraint
    private String building;

    @NotNull
    @Min(0)
    private Integer apartment;

    @NotNull
    @Size(min = 1, max = 256)
    private String name;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @NotNull
    @BeforeTodayConstraint
    private LocalDate birthDate;

    @NotNull
    private Gender gender;

    @NotNull
    private List<Integer> relatives;

    public ImportCitizenDTO() {}

    public Integer getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Integer citizenId) {
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

    public Integer getApartment() {
        return apartment;
    }

    public void setApartment(Integer apartment) {
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
