package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Line;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Oval;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Phenomenon;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Rect;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("pfService")
public class ProblemFrameService {
    public JSONObject getElementsOfPD(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        List<String> requirements = new ArrayList<>();
        List<String> tempRequirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        List<String> addRequirementTexts = new ArrayList<>();
        for(String requirement : tempRequirements){
            List<Device> devices = eo.getDevicesAffectingEnvironment();
            if(requirement.contains("PREFERRED") && requirement.contains("IS")){
                String entity = requirement.split(" ")[1];
                int value = Integer.parseInt(requirement.split(" ")[3]);
                for(int j = 0;j < devices.size();j++){
                    Device device = devices.get(j);
                    Map map = device.getStateMappingToAffectedEntities();
                    if(device.getAffectedAttributeNames().contains(entity)){
                        for(int k = 0;k < device.getStates().size();k++){
                            String state = device.getStates().get(k);
                            List<AffectedAttribute> monitoredEntities = (List<AffectedAttribute>) map.get(state);
                            for(int m = 0;m < monitoredEntities.size();m++){
                                AffectedAttribute monitoredEntity = monitoredEntities.get(m);
                                if(monitoredEntity.getAttributeName().equals(entity)){
                                    if(monitoredEntity.getAdjustRate() > 0){
                                        addRequirementTexts.add("IF " + entity + "<" + value + " THEN " + device.getDeviceName() + "." + state);
                                    }
                                    else if(monitoredEntity.getAdjustRate() < 0){
                                        addRequirementTexts.add("IF " + entity + ">=" + value + " THEN " + device.getDeviceName() + "." + state);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        requirements.addAll(tempRequirements);
        requirements.addAll(addRequirementTexts);
        for(String requirement : requirements){
            if(requirement.contains("IF") && requirement.contains("THEN") && !requirement.contains(" FOR ")){
                requirement = requirement.substring(3);
                String trigger = requirement.split(" THEN ")[0];
                String action = requirement.split(" THEN ")[1];
                if(trigger.contains(" && ")){
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" && "));
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, null));
                }
                else if(trigger.contains(" || ")){
                    for(int i = 0;i < trigger.split(" || ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger.split(" || ")[i]);
                        if(action.contains(",")) actions = Arrays.asList(action.split(","));
                        else actions.add(action);
                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null));
                    }
                }
                else {
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers.add(trigger);
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, null));
                }
            }
        }


        List<Oval> ovals = new ArrayList<>();
        List<Rect> rects = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        List<Phenomenon> phenomena = new ArrayList<>();
        List<Phenomenon> referencePhenomena = new ArrayList<>();
        int ovalNum = 1;
        int recNum = 1;
        int pheNum = 1;
        int intNum = 1;
        int refNum = 1;
        int conNum = 1;
        Rect machine = new Rect(100,400);
        machine.setText("Machine");
        machine.setShortName("M");
        machine.setState(2);
        rects.add(machine);
        for(IfThenRequirement requirement : ifThenRequirements){
            Oval oval = new Oval(900,100 * ovalNum);
            oval.setText("Requirement" + ovalNum);
            oval.setDes(1);
            oval.setBiaohao(ovalNum);
            ovals.add(oval);
            ovalNum++;
            for(String trigger : requirement.getTriggerList()){
                String entityName = trigger.split("\\.")[0];
                String attributeValue = trigger.split("\\.")[1];
                Rect rect = new Rect(500, 150 * recNum);
                rect.setState(1);
                rect.setText(entityName);
                rect.setShortName(entityName);
                if(!rects.contains(rect)){
                    rects.add(rect);
                    recNum++;
                }
                else rect = rects.get(rects.indexOf(rect));
                Line interfacee = new Line(machine, rect,0);
                Line reference = new Line(oval, rect,1);
                Phenomenon phenomenon = new Phenomenon(attributeValue,"state",rect,machine,pheNum);
                pheNum++;
                phenomenon.setRequirement(oval);
                if(!lines.contains(reference)){
                    reference.setName("ref" + (refNum++));
                    reference.addPhenomenon(phenomenon);
                    lines.add(reference);
                }
                else {
                    lines.get(lines.indexOf(reference)).addPhenomenon(phenomenon);
                }
                if(!lines.contains(interfacee)){
                    interfacee.setName("int" + (intNum++));
                    interfacee.addPhenomenon(phenomenon);
                    lines.add(interfacee);
                }
                else {
                    lines.get(lines.indexOf(interfacee)).addPhenomenon(phenomenon);
                }
                phenomena.add(phenomenon);
                referencePhenomena.add(phenomenon);
            }

            for(String action : requirement.getActionList()){
                String deviceName = action.split("\\.")[0];
                String deviceEventOrState = action.split("\\.")[1];
                Rect rect = new Rect(500, 150 * recNum);
                rect.setState(1);
                rect.setText(deviceName);
                rect.setShortName(deviceName);
                if(!rects.contains(rect)){
                    rects.add(rect);
                    recNum++;
                }
                else rect = rects.get(rects.indexOf(rect));
                Line interfacee = new Line(machine, rect,0);
                Line constraint = new Line(oval, rect,2);

                Phenomenon constraintPhenomenon;
                Phenomenon interfacePhenomenon;

                if(eo.getEvents().contains(deviceEventOrState)){
                    constraintPhenomenon = new Phenomenon(deviceEventOrState,"event",rect,machine,pheNum);
                    pheNum++;
                    interfacePhenomenon = new Phenomenon(eo.getEventMappingToAction().get(deviceEventOrState),"event",machine,rect,pheNum);
                    pheNum++;
                }
                else{
                    constraintPhenomenon = new Phenomenon(deviceEventOrState,"state",rect,machine,pheNum);
                    pheNum++;
                    interfacePhenomenon = new Phenomenon(eo.getStateMappingToAction().get(deviceEventOrState),"event",machine,rect,pheNum);
                    pheNum++;
                }
                constraintPhenomenon.setConstraining(true);
                constraintPhenomenon.setRequirement(oval);
                interfacePhenomenon.setRequirement(oval);
                if(!lines.contains(constraint)){
                    constraint.setName("con" + (conNum++));
                    constraint.addPhenomenon(constraintPhenomenon);
                    lines.add(constraint);
                }
                else {
                    lines.get(lines.indexOf(constraint)).addPhenomenon(constraintPhenomenon);
                }
                if(!lines.contains(interfacee)){
                    interfacee.setName("int" + (intNum++));
                    interfacee.addPhenomenon(interfacePhenomenon);
                    lines.add(interfacee);
                }
                else {
                    lines.get(lines.indexOf(interfacee)).addPhenomenon(interfacePhenomenon);
                }
                phenomena.add(constraintPhenomenon);
                phenomena.add(interfacePhenomenon);
                referencePhenomena.add(constraintPhenomenon);
            }
        }
        machine.changeSize(200, 75 * recNum);
        for(int i = 0;i < ovals.size();i++){
            Oval oval = ovals.get(i);
            oval.changeSize(900, 150 * (i + 1) * recNum / ovalNum);
        }
//        JSONArray ovalJS = JSONArray.fromObject(ovals);
//        JSONArray rectJS = JSONArray.fromObject(rects);
//        JSONArray lineJS = JSONArray.fromObject(lines);
//        JSONArray phenomenonJS = JSONArray.fromObject(phenomena);
//        JSONArray referenceJS = JSONArray.fromObject(referencePhenomena);
        result.put("ovals", ovals);
        result.put("rects", rects);
        result.put("lines", lines);
        result.put("phenomena", phenomena);
        result.put("referencePhenomena", referencePhenomena);
        return result;
    }

    public static void main(String[] args) {
        String s = new String("aaa");
        List<String> ss = new ArrayList<>();
        ss.add("aaa");
        System.out.println(ss.indexOf(s));
    }
}
