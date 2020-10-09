package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class OccurenceRequirement extends Requirement{
    private List<String> states;

    @Override
    public String toString() {
        return states + " SHOULD NEVER OCCUR TOGEGHER";
    }

    public OccurenceRequirement(List<String> states) {
        this.states = states;
    }

    public OccurenceRequirement(String requirement, List<String> states) {
        super(requirement);
        this.states = states;
    }
}
