package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Line;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Oval;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Phenomenon;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Rect;
import com.example.bianhan.iftttgenerator.util.Configuration;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeIfThenRequirements;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeMap;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeRequirements;

@Service("pfService")
public class ProblemFrameService {
    public JSONObject getElementsOfPD(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(Configuration.DROOLSMAPPATH, "intendMap", eo);
        Map<String, List<String>> sensorMap = computeMap(Configuration.DROOLSMAPPATH, "sensorMap", eo);
        Set<String> monitoredEntities = new HashSet<>();
        List<String> requirements = computeRequirements(requirementTexts, ontologyPath);
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath);
        List<Oval> ovals = new ArrayList<>();
        List<Rect> rects = new ArrayList<>();
        List<Line> lines = new ArrayList<>();//references and constraints
        List<Line> linesWithSensors = new ArrayList<>();
        List<Line> linesWithoutSensors = new ArrayList<>();
        List<Rect> sensorRects = new ArrayList<>();
        List<Rect> rectsWithSensors = new ArrayList<>();
        List<Line> interfacesWithSensors = new ArrayList<>();
        List<Line> interfacesWithoutSensors = new ArrayList<>();
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
                Rect rect = new Rect(800, 150 * recNum);
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
                if(!interfacesWithoutSensors.contains(interfacee)){
                    interfacee.setName("int" + (intNum++));
                    interfacee.addPhenomenon(phenomenon);
                    interfacesWithoutSensors.add(interfacee);
                }
                else {
                    interfacesWithoutSensors.get(interfacesWithoutSensors.indexOf(interfacee)).addPhenomenon(phenomenon);
                }
                phenomena.add(phenomenon);
                referencePhenomena.add(phenomenon);
            }

            for(String action : requirement.getActionList()){
                String deviceName = action.split("\\.")[0];
                String deviceEventOrState = action.split("\\.")[1];
                Rect rect = new Rect(800, 150 * recNum);
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
                if(!interfacesWithoutSensors.contains(interfacee)){
                    interfacee.setName("int" + (intNum++));
                    interfacee.addPhenomenon(interfacePhenomenon);
                    interfacesWithoutSensors.add(interfacee);
                }
                else {
                    interfacesWithoutSensors.get(interfacesWithoutSensors.indexOf(interfacee)).addPhenomenon(interfacePhenomenon);
                }
                phenomena.add(constraintPhenomenon);
                phenomena.add(interfacePhenomenon);
                referencePhenomena.add(constraintPhenomenon);
            }
        }
        machine.changeSize(200, 75 * recNum);
        for(int i = 0;i < ovals.size();i++){
            Oval oval = ovals.get(i);
            oval.changeSize(1100, 150 * (i + 1) * recNum / ovalNum);
        }

        for(int i = 0;i < rects.size();i++){
            Rect rect = rects.get(i);
            Iterator it = sensorMap.keySet().iterator();
            while (it.hasNext()){
                String sensorName = (String) it.next();
                List<String> attributes = sensorMap.get(sensorName);
                for (String attribute : attributes){
                    String entity = attribute.split("\\.")[0];
                    if(rect.getText().equals(entity)){
                        Rect sensorRect = new Rect(0,0);
                        sensorRect.setState(1);
                        sensorRect.changeSize(400, rect.getY1() + rect.getY2()/2);
                        sensorRect.setText(sensorName);
                        sensorRect.setShortName(sensorName);
                        if(!sensorRects.contains(sensorRect)) sensorRects.add(sensorRect);
                        for(Line line : interfacesWithoutSensors){
                            if(line.getState() ==0 && line.getTo() instanceof Rect && (line.getTo()).equals(rect)){
                                for(Phenomenon phenomenon : line.getPhenomena()){
                                    if(phenomenon.getName().contains(attribute.split("\\.")[1])){
                                        Line line1 = new Line(machine, sensorRect, 0);
                                        Line line2 = new Line(sensorRect, rect, 3);
                                        if(!interfacesWithSensors.contains(line1)){
                                            line1.setName("int" + (intNum++));
                                            line1.addPhenomenon(phenomenon);
                                            interfacesWithSensors.add(line1);
                                        }
                                        if(!interfacesWithSensors.contains(line2)){
                                            line2.setName("int" + (intNum++));
                                            line2.addPhenomenon(phenomenon);
                                            interfacesWithSensors.add(line2);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        Iterator it = sensorMap.keySet().iterator();
        while (it.hasNext()){
            String sensorName = (String) it.next();
            List<String> attributes = sensorMap.get(sensorName);
            for (String attribute : attributes){
                String entity = attribute.split("\\.")[0];
                monitoredEntities.add(entity);
            }
        }

        for (Line line : interfacesWithoutSensors){
            if(!monitoredEntities.contains(((Rect)line.getTo()).getText())) interfacesWithSensors.add(line);
        }

        linesWithSensors.addAll(lines);
        linesWithSensors.addAll(interfacesWithSensors);
        linesWithoutSensors.addAll(lines);
        linesWithoutSensors.addAll(interfacesWithoutSensors);
        rectsWithSensors.addAll(rects);
        rectsWithSensors.addAll(sensorRects);

        result.put("ovals", ovals);
        result.put("rectsWithSensors", rectsWithSensors);
        result.put("rectsWithoutSensors", rects);
        result.put("linesWithSensors", linesWithSensors);
        result.put("linesWithoutSensors", linesWithoutSensors);
        result.put("phenomena", phenomena);
        result.put("referencePhenomena", referencePhenomena);
        rects.addAll(sensorRects);

        return result;
    }



    public static void main(String[] args) {
        String s = new String("aaa");
        List<String> ss = new ArrayList<>();
        ss.add("aaa");
        System.out.println(ss.indexOf(s));
    }
}
