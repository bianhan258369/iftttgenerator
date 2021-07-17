package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class DeviceRegistryItem {
    private String domain;
    private String event;
    private String state;
    private String service;
    private String type;
    private String effect;

    public DeviceRegistryItem(String domain, String state,String event, String service, String type, String effect) {
        this.domain = domain;
        this.event = event;
        this.state = state;
        this.service = service;
        this.type = type;
        this.effect = effect;
    }

    public DeviceRegistryItem(String registry) {
        registry = registry.substring(2);
        this.domain = registry.split("->")[0].equals("null") ? null : registry.split("->")[0];
        this.event = registry.split("->")[1].equals("null") ? null : registry.split("->")[1];
        this.state = registry.split("->")[2].equals("null") ? null : registry.split("->")[2];
        this.service = registry.split("->")[3].equals("null") ? null : registry.split("->")[3];
        this.type = registry.split("->")[4].equals("null") ? null : registry.split("->")[4];
        this.effect = registry.split("->")[5].equals("null") ? null : registry.split("->")[5];
    }

    @Override
    public String toString() {
        return "DeviceRegistryItem{" +
                "domain='" + domain + '\'' +
                ", state='" + state + '\'' +
                ", service='" + service + '\'' +
                ", type='" + type + '\'' +
                ", effect='" + effect + '\'' +
                '}';
    }
}
