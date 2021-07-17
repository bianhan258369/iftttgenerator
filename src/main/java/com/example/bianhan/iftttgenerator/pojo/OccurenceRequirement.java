package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class OccurenceRequirement extends Requirement{
    private List<String> deviceStates;

    @Override
    public String toString() {
        return deviceStates + " SHOULD NEVER OCCUR TOGEGHER";
    }

    public OccurenceRequirement(List<String> deviceStates) {
        this.deviceStates = deviceStates;
    }

    public OccurenceRequirement(String requirement, List<String> deviceStates) {
        super(requirement);
        this.deviceStates = deviceStates;
    }

    public OccurenceRequirement(String requirement, String room, List<String> deviceStates) {
        super(requirement, room);
        this.deviceStates = deviceStates;
    }
}
