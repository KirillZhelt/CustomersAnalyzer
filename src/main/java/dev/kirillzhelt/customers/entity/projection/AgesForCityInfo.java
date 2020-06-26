package dev.kirillzhelt.customers.entity.projection;

import org.springframework.data.annotation.Transient;

import java.util.List;

public class AgesForCityInfo {

    private final String town;

    private final Double p50;
    private final Double p75;
    private final Double p99;

    public AgesForCityInfo(String town, Double p50, Double p75, Double p99) {
        this.town = town;
        this.p50 = p50;
        this.p75 = p75;
        this.p99 = p99;
    }

    public String getTown() {
        return town;
    }

    public Double getP50() {
        return p50;
    }

    public Double getP75() {
        return p75;
    }

    public Double getP99() {
        return p99;
    }
}
