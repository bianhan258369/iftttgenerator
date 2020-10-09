package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class PreferredRequirement extends Requirement {
    private String attribute;//Air.temperature
    private Double value;

    @Override
    public String toString() {
        return "PREFERRED " + attribute + " IS " + value;
    }

    public PreferredRequirement(String attribute, Double value) {
        this.attribute = attribute;
        this.value = value;
    }

    public PreferredRequirement(String requirement, String attribute, Double value) {
        super(requirement);
        this.attribute = attribute;
        this.value = value;
    }
}