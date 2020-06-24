package dev.kirillzhelt.customers.entity.util;

public enum Gender {
    MALE("male"), FEMALE("female");

    private String value;

    Gender(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
