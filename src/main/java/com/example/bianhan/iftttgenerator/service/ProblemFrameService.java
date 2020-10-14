package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Line;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Oval;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Phenomenon;
import com.example.bianhan.iftttgenerator.pojo.problemdiagram.Rect;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;
import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.SCDPATH;

@Service("pfService")
public class ProblemFrameService {
    public JSONObject getElementsOfPD(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);
        Map<String, List<String>> sensorMap = computeMap(PathConfiguration.DROOLSMAPPATH, "sensorMap", eo);
        Set<String> monitoredEntities = new HashSet<>();
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), intendMap, ontologyPath).get(index);
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

        int positionY = -1;
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
                        if(positionY == rect.getY1() + rect.getY2()/2) positionY += 100;
                        else positionY = rect.getY1() + rect.getY2()/2;
                        sensorRect.changeSize(400, positionY);
                        positionY = rect.getY1() + rect.getY2()/2;
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

    public JSONObject getSdPng(String requirementTexts, String ontologyPath, String scFolderPath, int index) throws IOException, DocumentException, InterruptedException {
        JSONObject result = new JSONObject();
        List<String> paths = new ArrayList<>();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);
        Map<String, List<String>> sensorMap = computeMap(PathConfiguration.DROOLSMAPPATH, "sensorMap", eo);
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), intendMap, ontologyPath).get(index);
        Map<String, List<IfThenRequirement>> intendMappingToIfThenRequirements = new HashMap<>();
        for(int i = 0;i < ifThenRequirements.size();i++){
            IfThenRequirement requirement = ifThenRequirements.get(i);
            String intend = requirement.getIntend();
            if(intend != null){
                List<IfThenRequirement> ifThenRequirementList = intendMappingToIfThenRequirements.containsKey(intend) ?
                        intendMappingToIfThenRequirements.get(intend) : new ArrayList<>();
                ifThenRequirementList.add(requirement);
                intendMappingToIfThenRequirements.put(intend, ifThenRequirementList);
            }
        }
        int pngIndex = 0;
        int ifThenIndex = 1;
        Iterator it = intendMappingToIfThenRequirements.keySet().iterator();
        while (it.hasNext()){
            String intend = (String) it.next();
            List<IfThenRequirement> ifThenRequirementList = intendMappingToIfThenRequirements.get(intend);
            List<ScenarioNode> scenarioNodes = new ArrayList<>();
            int chainIndex = 1;
            int intendLayer = 0;
            for(IfThenRequirement requirement : ifThenRequirementList){
                for(String trigger : requirement.getTriggerList()){
                    String entityName = trigger.split("\\.")[0];
                    String attributeValue = trigger.split("\\.")[1];
                    String relation = computeRelation(attributeValue);
                    if(!relation.equals("")){
                        String attribute = entityName + "." + attributeValue.split(relation)[0];
                        Iterator iit = sensorMap.keySet().iterator();
                        while (iit.hasNext()){
                            String sensorName = (String) iit.next();
                            if(sensorMap.get(sensorName).contains(attribute)){
                                scenarioNodes.add(new ScenarioNode("S:" + attributeValue,1,0,sensorName,chainIndex,ifThenIndex));
                                scenarioNodes.add(new ScenarioNode(attributeValue,2,1,entityName,chainIndex,ifThenIndex));
                                scenarioNodes.add(new ScenarioNode("S:" + attributeValue,1,3,sensorName,chainIndex,ifThenIndex));
                                scenarioNodes.add(new ScenarioNode(attributeValue,2,3,entityName,chainIndex,ifThenIndex));
                                break;
                            }
                        }
                    }
                    else {
                        scenarioNodes.add(new ScenarioNode(attributeValue,2,0,entityName,chainIndex,ifThenIndex));
                        scenarioNodes.add(new ScenarioNode(attributeValue,2,3,entityName,chainIndex,ifThenIndex));
                    }
                    chainIndex++;
                }
                for(int i = 0;i < requirement.getActionList().size();i++){
                    String action = requirement.getActionList().get(i);
                    String deviceName = action.split("\\.")[0];
                    String deviceEventOrState = action.split("\\.")[1];
                    String state = eo.getEvents().contains(deviceEventOrState) ? eo.getEventMappingToState().get(deviceEventOrState) : deviceEventOrState;
                    String pulse = eo.getStateMappingToAction().get(state);
                    scenarioNodes.add(new ScenarioNode(pulse,3 + i,2,"Machine",-1,ifThenIndex));
                    scenarioNodes.add(new ScenarioNode(deviceEventOrState,3 + i,4,deviceName,-1,ifThenIndex));
                    intendLayer = 4 + i ;
                }
//                for(String action : requirement.getActionList()){
//                    String deviceName = action.split("\\.")[0];
//                    String deviceEventOrState = action.split("\\.")[1];
//                    String state = eo.getEvents().contains(deviceEventOrState) ? eo.getEventMappingToState().get(deviceEventOrState) : deviceEventOrState;
//                    String pulse = eo.getStateMappingToAction().get(state);
//                    scenarioNodes.add(new ScenarioNode(pulse,3,0,"Machine",-1,ifThenIndex));
//                    scenarioNodes.add(new ScenarioNode(deviceEventOrState,3,2,deviceName,-1,ifThenIndex));
//                }
                ifThenIndex++;
            }
            scenarioNodes.add(new ScenarioNode(intend, intendLayer, 5, "User",-1, -1));
            ScenarioDiagram scenarioDiagram = new ScenarioDiagram(scenarioNodes);
            String scFilePath = scFolderPath + "ScenarioDiagram" + (pngIndex++);
            scenarioDiagram.toDotFile(eo, scFilePath + ".dot");
            String cmd = "neato -Gdpi=300 " + scFilePath + ".dot -n -Tpng -o " + scFilePath + ".png";
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            p.destroy();
            paths.add(scFilePath + ".png");
        }
        for(int i = 0;i < ifThenRequirements.size();i++){
            IfThenRequirement requirement = ifThenRequirements.get(i);
            List<ScenarioNode> scenarioNodes = new ArrayList<>();
            int chainIndex = 1;
            if(requirement.getIntend() == null){
                for(String trigger : requirement.getTriggerList()){
                    String entityName = trigger.split("\\.")[0];
                    String attributeValue = trigger.split("\\.")[1];
                    String relation = computeRelation(attributeValue);
                    if(!relation.equals("")){
                        String attribute = entityName + "." + attributeValue.split(relation)[0];
                        Iterator iit = sensorMap.keySet().iterator();
                        while (iit.hasNext()){
                            String sensorName = (String) iit.next();
                            if(sensorMap.get(sensorName).contains(attribute)){
                                scenarioNodes.add(new ScenarioNode("S:" + attributeValue,1,0,sensorName,chainIndex,ifThenIndex));
                                scenarioNodes.add(new ScenarioNode(attributeValue,2,1,entityName,chainIndex,ifThenIndex));
                                scenarioNodes.add(new ScenarioNode("S:" + attributeValue,1,3,sensorName,chainIndex,ifThenIndex));
                                scenarioNodes.add(new ScenarioNode(attributeValue,2,3,entityName,chainIndex,ifThenIndex));
                                break;
                            }
                        }
                    }
                    else {
                        scenarioNodes.add(new ScenarioNode(attributeValue,2,0,entityName,chainIndex,ifThenIndex));
                        scenarioNodes.add(new ScenarioNode(attributeValue,2,3,entityName,chainIndex,ifThenIndex));
                    }
                    chainIndex++;
                }
                for(int j = 0;j < requirement.getActionList().size();j++){
                    String action = requirement.getActionList().get(j);
                    String deviceName = action.split("\\.")[0];
                    String deviceEventOrState = action.split("\\.")[1];
                    String state = eo.getEvents().contains(deviceEventOrState) ? eo.getEventMappingToState().get(deviceEventOrState) : deviceEventOrState;
                    String pulse = eo.getStateMappingToAction().get(state);
                    scenarioNodes.add(new ScenarioNode(pulse,3 + j,2,"Machine",-1,ifThenIndex));
                    scenarioNodes.add(new ScenarioNode(deviceEventOrState,3 + j,4,deviceName,-1,ifThenIndex));
                }
//                for(String action : requirement.getActionList()){
//                    String deviceName = action.split("\\.")[0];
//                    String deviceEventOrState = action.split("\\.")[1];
//                    String state = eo.getEvents().contains(deviceEventOrState) ? eo.getEventMappingToState().get(deviceEventOrState) : deviceEventOrState;
//                    String pulse = eo.getStateMappingToAction().get(state);
//                    scenarioNodes.add(new ScenarioNode(pulse,3,0,"Machine",-1,ifThenIndex));
//                    scenarioNodes.add(new ScenarioNode(deviceEventOrState,3,2,deviceName,-1,ifThenIndex));
//                }
                ifThenIndex++;
                ScenarioDiagram scenarioDiagram = new ScenarioDiagram(scenarioNodes);
                String scFilePath = scFolderPath + "ScenarioDiagram" + (pngIndex++);
                scenarioDiagram.toDotFile(eo, scFilePath + ".dot");
                String cmd = "neato -Gdpi=300 " + scFilePath + ".dot -n -Tpng -o " + scFilePath + ".png";
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
                p.destroy();
                paths.add(scFilePath + ".png");
            }
        }
        result.put("paths", paths);
        return result;
    }

    public void ToPng(String filePath, HttpServletResponse response) {
        // TODO Auto-generated method stub
        FileInputStream is = null;
        File filePic = new File(filePath);
        if(filePic.exists()) {
            try {
                is = new FileInputStream(filePic);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("image/png");
            if(is != null) {
                try {
                    int i = is.available();
                    byte data[] = new byte[i];
                    is.read(data);
                    is.close();
                    response.setContentType("image/png");
                    OutputStream toClient = response.getOutputStream();
                    toClient.write(data);
                    toClient.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }



    public static void main(String[] args) throws IOException, DocumentException, InterruptedException {
        ProblemFrameService problemFrameService = new ProblemFrameService();
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        String re = "IF Person.distanceFromPro<2 THEN Blind.bclosed,Projector.pon//IF Air.humidity>30 THEN allow ventilating the room";
        System.out.println(problemFrameService.getSdPng(re, ontologyPath, SCDPATH,0));
    }
}
