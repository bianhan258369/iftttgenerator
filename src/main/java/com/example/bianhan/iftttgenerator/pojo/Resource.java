package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.Objects;

@Data
public class Resource {
    private String resourceName;
    private Double value;

    public Resource(String resourceName, Double number) {
        this.resourceName = resourceName;
        this.value = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(resourceName, resource.resourceName) &&
                Objects.equals(value, resource.value);
    }
}
