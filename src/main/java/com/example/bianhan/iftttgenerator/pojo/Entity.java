package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class Entity {
    private String domainOrDeviceClass;
    private String device_id;
    private String entity_id;
    private String area;

    public Entity(String domainOrDeviceClass, String device_id, String entity_id, String area) {
        this.domainOrDeviceClass = domainOrDeviceClass;
        this.device_id = device_id;
        this.entity_id = entity_id;
        this.area = area;
    }

    public Entity(String registry) {
        this.domainOrDeviceClass = registry.split("->")[0];
        this.device_id = registry.split("->")[1];
        this.entity_id = registry.split("->")[2];
        this.area = registry.split("->")[3];
    }

    @Override
    public String toString() {
        return "Entity{" +
                "domainOrDeviceClass='" + domainOrDeviceClass + '\'' +
                ", device_id='" + device_id + '\'' +
                ", entity_id='" + entity_id + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
