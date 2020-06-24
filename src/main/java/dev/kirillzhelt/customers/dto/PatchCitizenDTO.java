package dev.kirillzhelt.customers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirillzhelt.customers.entity.util.Gender;

import java.time.LocalDate;
import java.util.List;

public class PatchCitizenDTO {

    private String town;
    private String street;
    private String building;
    private Integer apartment;

    private String name;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate birthDate;

    private Gender gender;

    private List<Integer> relatives;


}
