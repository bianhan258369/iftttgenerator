package com.example.bianhan.iftttgenerator.pojo;

public class AffectedAttribute {
    private String attributeName;//air.temperature
    private Double adjustRate;
    private String adjustMethod;

    public AffectedAttribute(String attributeName, Double adjustRate, String adjustMethod) {
        this.attributeName = attributeName;
        this.adjustRate = adjustRate;
        this.adjustMethod = adjustMethod;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Double getAdjustRate() {
        return adjustRate;
    }

    public void setAdjustRate(Double adjustRate) {
        this.adjustRate = adjustRate;
    }

    public String getAdjustMethod() {
        return adjustMethod;
    }

    public void setAdjustMethod(String adjustMethod) {
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
