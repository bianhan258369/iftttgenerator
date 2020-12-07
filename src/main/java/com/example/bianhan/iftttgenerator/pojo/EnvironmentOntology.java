package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Data
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
                    Double energy = Double.parseDouble(adjust_entity.attributeValue("energy"));
                    AffectedAttribute affectedAttribute = new AffectedAttribute(affectedAttributeName, rate, method, energy);
                    affectedAttributeList.add(affectedAttribute);
                }
                stateMappingToAffectedEntities.put(stateName, affectedAttributeList);
            }
            devicesAffectingEnvironment.add(new Device(deviceName,initState, states, affectedAttributeNames, stateMappingToAffectedEntities));
        }
    }

    public String getReverseState(String device, String state){
        List<String> stateList = deviceMappingToStates.get(device);
        stateList.remove(state);
        return stateList.get(0);
    }

    public String getDeviceNameByState(String state){
        Iterator it = deviceMappingToStates.keySet().iterator();
        while (it.hasNext()){
            String deviceName = (String) it.next();
            List<String> states = deviceMappingToStates.get(deviceName);
            if(states.contains(state)) return deviceName;
        }
        return null;
    }


    public static void main(String[] args) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology("ontology_SmartConferenceRoom.xml");
        System.out.println(eo.getStates());
    }
}
