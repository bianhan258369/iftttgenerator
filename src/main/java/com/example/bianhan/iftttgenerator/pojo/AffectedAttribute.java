package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class AffectedAttribute {
    private String attributeName;//air.temperature
    private Double adjustRate;
    private String adjustMethod;

    public AffectedAttribute(String attributeName, Double adjustRate, String adjustMethod) {
        this.attributeName = attributeName;
        this.adjustRate = adjustRate;
        this.adjustMethod = adjustMethod;
    }

    @Override
    public String toString() {
        return "AffectedAttribute{" +
                "attributeName='" + attributeName + '\'' +
                ", adjustRate=" + adjustRate +
                ", adjustMethod='" + adjustMethod + '\'' +
                '}';
    }
}
