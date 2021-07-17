package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class BinarySensor {
    private String type;
    private String monitor;
    private String trueValue;
    private String falseValue;

    public BinarySensor(String type, String monitor, String trueValue, String falseValue) {
        this.type = type;
        this.monitor = monitor;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    public BinarySensor(String registry){
        this.type = registry.split("->")[1];
        this.monitor = registry.split("->")[2];
        this.trueValue = registry.split("->")[3];
        this.falseValue = registry.split("->")[4];
    }
}
