package com.example.bianhan.iftttgenerator.pojo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EnvironmentOntology {
    private String ontologyPath;
    private Set<String> attributes;
    private Set<String> events;
    private Set<String> states;
    private Set<String> devices;
    private List<Device> devicesAffectingEnvironment;
    private Map<String, String> deviceMappingToInitState;
    private Map<String, String> actionMappingToState;//aoffPulse->aoff
    private Map<String, String> eventMappingToState;//aturnoff->aoff
    private Map<String, String> eventMappingToAction;//aturnoff->aoffPulse
    private Map<String, String> stateMappingToAction;//aoff->aoffPulse
    private Map<String, List<String>> deviceMappingToStates;

    public EnvironmentOntology(String ontologyPath) throws IOException, DocumentException {
        attributes = new HashSet<>();
        events = new HashSet<>();
        states = new HashSet<>();
        devices = new HashSet<>();
        devicesAffectingEnvironment = new ArrayList<>();
        actionMappingToState = new HashMap<>();
        eventMappingToState = new HashMap<>();
        eventMappingToAction = new HashMap<>();
        stateMappingToAction = new HashMap<>();
        deviceMappingToStates = new HashMap<>();
        deviceMappingToInitState = new HashMap<>();
        this.ontologyPath = ontologyPath;
        SAXReader saxReader = new SAXReader();
        File file = new File(ontologyPath);
        Document document = saxReader.read(file);
        Element root = document.getRootElement();
        List<Element> environmentElements = root.element("environment").elements();
        for (Element attribute : environmentElements) {
            attributes.add(attribute.attributeValue("name"));
        }
        List<Element> deviceElements = root.element("statemachines").elements();
        for (Element device : deviceElements) {
            String deviceName = device.attributeValue("name");
            devices.add(deviceName);
            List<String> stateList = new ArrayList<>();
            List<Element> stateElements = device.element("states").elements();
            for (Element state : stateElements) {
                String stateName = state.getText();
                if(state.attributeValue("init").equals("true")) deviceMappingToInitState.put(deviceName, stateName);
                states.add(stateName);
                stateList.add(stateName);
            }
            deviceMappingToStates.put(deviceName, stateList);
            List<Element> eventElements = device.element("events").elements();
            for (Element event : eventElements) {
                events.add(event.getText());
            }
            List<Element> transitionElements = device.element("transitions").elements();
            for (Element transition : transitionElements) {
                actionMappingToState.put(transition.attributeValue("action"), transition.attributeValue("to"));
                eventMappingToState.put(transition.attributeValue("event"), transition.attributeValue("to"));
                eventMappingToAction.put(transition.attributeValue("event"), transition.attributeValue("action"));
                stateMappingToAction.put(transition.attributeValue("to"), transition.attributeValue("action"));
            }
        }
        deviceElements = root.element("devices").elements();
        for(Element device : deviceElements){
            Map<String, List<AffectedAttribute>>  stateMappingToAffectedEntities = new HashMap<>();
            List<String> states = new ArrayList<>();
            List<String> affectedAttributeNames = new ArrayList<>();
            String deviceName = device.attributeValue("name");
            String initState = "";
            List<Element> stateElements = device.element("states").elements();
            for(Element state : stateElements){
                String stateName = state.attributeValue("name");
                if(state.attributeValue("init").equals("true")) initState = stateName;
                states.add(stateName);
                List<AffectedAttribute> affectedAttributeList = new ArrayList<>();
                for(Element adjust_entity : state.elements()){
                    String affectedAttributeName = adjust_entity.attributeValue("name");
                    affectedAttributeNames.add(affectedAttributeName);
                    Double rate = Double.parseDouble(adjust_entity.attributeValue("rate"));
                    String method = adjust_entity.attributeValue("method");
                    AffectedAttribute affectedAttribute = new AffectedAttribute(affectedAttributeName, rate, method);
                    affectedAttributeList.add(affectedAttribute);
                }
                stateMappingToAffectedEntities.put(stateName, affectedAttributeList);
            }
            devicesAffectingEnvironment.add(new Device(deviceName,initState, states, affectedAttributeNames, stateMappingToAffectedEntities));
        }
    }

    public Set<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<String> attributes) {
        this.attributes = attributes;
    }

    public Set<String> getStates() {
        return states;
    }

    public void setStates(Set<String> states) {
        this.states = states;
    }

    public Set<String> getDevices() {
        return devices;
    }

    public void setDevices(Set<String> devices) {
        this.devices = devices;
    }

    public Map<String, String> getActionMappingToState() {
        return actionMappingToState;
    }

    public void setActionMappingToState(Map<String, String> actionMappingToState) {
        this.actionMappingToState = actionMappingToState;
    }

    public Set<String> getEvents() {
        return events;
    }

    public void setEvents(Set<String> events) {
        this.events = events;
    }

    public Map<String, String> getEventMappingToState() {
        return eventMappingToState;
    }

    public void setEventMappingToState(Map<String, String> eventMappingToState) {
        this.eventMappingToState = eventMappingToState;
    }

    public Map<String, String> getEventMappingToAction() {
        return eventMappingToAction;
    }

    public void setEventMappingToAction(Map<String, String> eventMappingToAction) {
        this.eventMappingToAction = eventMappingToAction;
    }

    public Map<String, String> getStateMappingToAction() {
        return stateMappingToAction;
    }

    public void setStateMappingToAction(Map<String, String> stateMappingToAction) {
        this.stateMappingToAction = stateMappingToAction;
    }

    public List<Device> getDevicesAffectingEnvironment() {
        return devicesAffectingEnvironment;
    }

    public void setDevicesAffectingEnvironment(List<Device> devicesAffectingEnvironment) {
        this.devicesAffectingEnvironment = devicesAffectingEnvironment;
    }

    public Map<String, List<String>> getDeviceMappingToStates() {
        return deviceMappingToStates;
    }

    public void setDeviceMappingToStates(Map<String, List<String>> deviceMappingToStates) {
        this.deviceMappingToStates = deviceMappingToStates;
    }

    public String getReverseState(String device, String state){
        List<String> stateList = deviceMappingToStates.get(device);
        stateList.remove(state);
        return stateList.get(0);
    }

    public Map<String, String> getDeviceMappingToInitState() {
        return deviceMappingToInitState;
    }

    public void setDeviceMappingToInitState(Map<String, String> deviceMappingToInitState) {
        this.deviceMappingToInitState = deviceMappingToInitState;
    }

    public static void main(String[] args) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology("ontology_SmartConferenceRoom.xml");
        System.out.println(eo.getStates());
    }
}
