package com.example.bianhan.iftttgenerator.pojo;

import java.util.List;
import java.util.Map;

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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getInitState() {
        return initState;
    }

    public void setInitState(String initState) {
        this.initState = initState;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public List<String> getAffectedAttributeNames() {
        return affectedAttributeNames;
    }

    public void setAffectedAttributeNames(List<String> affectedAttributeNames) {
        this.affectedAttributeNames = affectedAttributeNames;
    }

    public Map<String, List<AffectedAttribute>> getStateMappingToAffectedEntities() {
        return stateMappingToAffectedEntities;
    }

    public void setStateMappingToAffectedEntities(Map<String, List<AffectedAttribute>> stateMappingToAffectedEntities) {
        this.stateMappingToAffectedEntities = stateMappingToAffectedEntities;
    }
}
