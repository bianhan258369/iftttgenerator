package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Device {
    private String deviceName;
    private String initState;
    private List<String> states;
    private List<String> affectedAttributeNames;
    private Map<String, List<AffectedAttribute>> stateMappingToAffectedEntities;//coldOn->{[air.temperature,-0.2,second],[air.humidity,0.1,second]}

    public Device(String deviceName, String initState, List<String> states, List<String> affectedAttributeNames, Map<String, List<AffectedAttribute>> stateMappingToAffectedEntities) {
        this.deviceName = deviceName;
        this.initState = initState;
        this.states = states;
        this.affectedAttributeNames = affectedAttributeNames;
        this.stateMappingToAffectedEntities = stateMappingToAffectedEntities;
    }
}
