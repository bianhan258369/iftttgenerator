package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class NumericSensor {
    private String type;
    private String monitor;

    public NumericSensor(String type, String monitor) {
        this.type = type;
        this.monitor = monitor;
    }

    public NumericSensor(String registry) {
        this.type = registry.split("->")[1];
        this.monitor = registry.split("->")[2];;
    }
}
