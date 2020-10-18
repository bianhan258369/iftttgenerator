package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("checkService")
public class CheckService {
    public List<String> consistencyCheck(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), intendMap, ontologyPath).get(index);
        List<String> errors = new ArrayList<>();
        Map<String, List<List<String>>> entityMappingToTriggers = new HashMap<>();
        for(int i = 0;i < ifThenRequirements.size();i++){
            IfThenRequirement requirement = ifThenRequirements.get(i);
            List<String> actions = requirement.getActionList();
            for(int j = 0;j < actions.size();j++){
                String action = actions.get(j);
                System.out.println(action);
                String left = action.split("\\.")[0];
                String right = action.split("\\.")[1];
                if(eo.getEvents().contains(right)) actions.set(j, left + "." + eo.getEventMappingToState().get(right));
                if(eo.getActionMappingToState().containsKey(right)) actions.set(j, left + "." + eo.getStateMappingToAction().get(right));
            }
            requirement.setActionList(actions);
            ifThenRequirements.set(i, requirement);
        }

        for(IfThenRequirement requirement : ifThenRequirements){
            for(String state : requirement.getActionList()){
                String entity = state.split("\\.")[0];
                List<List<String>> triggerLists = entityMappingToTriggers.containsKey(entity) ? entityMappingToTriggers.get(entity) : new ArrayList<>();
                triggerLists.add(requirement.getTriggerList());
                entityMappingToTriggers.put(entity, triggerLists);
            }
        }
        System.out.println(entityMappingToTriggers);

        Iterator it = entityMappingToTriggers.keySet().iterator();
        while (it.hasNext()){
            boolean flag = false;
            String key = (String) it.next();
            List<List<String>> triggerLists = entityMappingToTriggers.get(key);
            if(triggerLists.size() > 1){
                for(int i = 0;i < triggerLists.size() - 1;i++){
                    List<String> triggerList1 = triggerLists.get(i);
                    List<String> triggerList2 = triggerLists.get(i + 1);
                    for(String trigger1 : triggerList1){
                        for(String trigger2 : triggerList2){
                            if(isNotTriggerConflict(trigger1, trigger2))flag = true;
                        }
                    }
                }
                if(!flag) errors.add("triggers on " + key + " have conflicts");
            }
        }
        return errors;
    }

    public void exportSMT(String smtFilePath ,String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        List<String> formulas = getZ3Fomulas(requirementTexts, ontologyPath, index);
        BufferedWriter bw = new BufferedWriter(new FileWriter(smtFilePath));
        for(String formula : formulas){
            bw.write(formula);
            bw.newLine();
            bw.flush();
        }
        bw.flush();
        bw.close();
    }

    private boolean isNotTriggerConflict(String trigger1, String trigger2){
        String relation1 = computeRelation(trigger1);
        String relation2 = computeRelation(trigger2);
        if(!relation1.equals("") && !relation2.equals("")){
            String attribute1 = trigger1.split(relation1)[0];
            String attribute2 = trigger2.split(relation2)[0];
            String value1 = trigger1.split(relation1)[1];
            String value2 = trigger2.split(relation2)[1];
            if(attribute1.equals(attribute2)){
                if(isNotRangeOverlapping(relation1, relation2, value1, value2)) return true;
            }
        }
        return false;
    }

    private boolean isNotRangeOverlapping(String relation1, String relation2, String value1, String value2){
        relation1 = simplifyRelation(relation1);
        relation2 = simplifyRelation(relation2);
        if(relation1.equals(">") && relation2.equals(">")) return false;
        else if(relation1.equals("<") && relation2.equals("<")) return false;
        else if(relation1.equals(">") && relation2.equals("<")){
            return (Double.parseDouble(value1) >= Double.parseDouble(value2));
        }
        else if(relation1.equals("<") && relation2.equals(">")) return isNotRangeOverlapping(relation2, relation1, value2, value1);
        else if(relation1.equals(">") && relation2.equals("=")){
            return (Double.parseDouble(value1) > Double.parseDouble(value2));
        }
        else if(relation1.equals("<") && relation2.equals("=")){
            return (Double.parseDouble(value1) < Double.parseDouble(value2));
        }
        else if(relation1.equals("=") && (relation2.equals("<") || relation2.equals(">"))) return isNotRangeOverlapping(relation2, relation1, value2, value1);
        else if((relation1.equals("=") && relation2.equals("!=")) || (relation1.equals("!=") && relation2.equals("="))) return !value1.equals(value2);
        return false;
    }

    private String simplifyRelation(String relation){
        if(relation.equals(">=")) return ">";
        else if(relation.equals("<=")) return "<";
        else return relation;
    }

    private List<String> getZ3Fomulas(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        List<String> result = new ArrayList<>();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);

        List<Requirement> requirements = initRequirements(Arrays.asList(requirementTexts.split("//")));

        Map<String, Integer> stateMappingToInteger = new HashMap<>();
        Map<String, Z3Fomula> deviceMappingToFormula = new HashMap<>();
        Iterator it = eo.getDeviceMappingToStates().keySet().iterator();
        while (it.hasNext()){
            String device = (String) it.next();
            List<String> deviceStates = eo.getDeviceMappingToStates().get(device);
            for(int i = 0;i < deviceStates.size();i++) stateMappingToInteger.put(device + "." + deviceStates.get(i),i);
        }

        for(Requirement requirement : requirements){
             if(requirement instanceof AlwaysNeverRequirement){
                AlwaysNeverRequirement alwaysNeverRequirement = (AlwaysNeverRequirement) requirement;
                if(alwaysNeverRequirement.getAttribute() == null){
                    String relation = alwaysNeverRequirement.getRelation();
                    String deviceEventOrState = alwaysNeverRequirement.getDeviceEventOrState();
                    String device = deviceEventOrState.split("\\.")[0];
                    String eventOrState = deviceEventOrState.split("\\.")[1];
                    String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                    int stateIndex = stateMappingToInteger.get(device + "." + state);
                    if(!deviceMappingToFormula.containsKey(device)) deviceMappingToFormula.put(device, new Z3Fomula(device));
                    Z3Fomula z3Fomula = deviceMappingToFormula.get(device);
                    z3Fomula.getAttributes().add(device);
                    if(relation.equals("ALWAYS")){
                        String expressions = "(= " + device + " " + stateIndex + ")";
                        z3Fomula.getExpressions().add(expressions);
                    }
                    else {
                        String expressions = "(not (= " + device + " " + stateIndex + "))";
                        z3Fomula.getExpressions().add(expressions);
                    }
                }
            }
        }

        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath).get(index);
        for(IfThenRequirement ifThenRequirement : ifThenRequirements){
            if(ifThenRequirement.getTime() == null){
                for(String action : ifThenRequirement.getActionList()){
                    String actionDevice = action.split("\\.")[0];
                    String eventOrState = action.split("\\.")[1];
                    String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                    if(!deviceMappingToFormula.containsKey(actionDevice)) deviceMappingToFormula.put(actionDevice, new Z3Fomula(actionDevice));
                    Z3Fomula z3Fomula = deviceMappingToFormula.get(actionDevice);
                    int stateIndex = stateMappingToInteger.get(actionDevice + "." +state);
                    //(=> (< Air.humidity 30) (= Window 0))
                    //(=> (= Blind 0) (= Window 0))
                    if(ifThenRequirement.getTriggerList().size() == 1){
                        String trigger = ifThenRequirement.getTriggerList().get(0);
                        String relation = computeRelation(trigger);
                        if(!relation.equals("")){
                            String attribute = trigger.split(relation)[0];
                            String value = trigger.split(relation)[1];
                            String expressions = "(=> (" + relation + " " + attribute + " " + value + ") " + "(= " + actionDevice + " " + stateIndex + "))";
                            z3Fomula.getAttributes().add(attribute);
                            z3Fomula.getExpressions().add(expressions);
                        }
                        else {
                            String triggerDevice = trigger.split("\\.")[0];
                            int triggerDeviceStateIndex = stateMappingToInteger.get(trigger);
                            String expressions = "(=> (= " + triggerDevice + " " + triggerDeviceStateIndex + ") " + "(= " + actionDevice + " " + stateIndex + "))";
                            z3Fomula.getAttributes().add(triggerDevice);
                            z3Fomula.getExpressions().add(expressions);
                        }
                    }
                    //(=> (and (< Air.humidity 30) (> Air.humidity 40)) (= Window 0))
                    //(=> (and (< Air.humidity 30) (= Blind 0)) (= Window 0))
                    else {
                        StringBuilder expressions = new StringBuilder("(=> (and ");
                        for(String trigger : ifThenRequirement.getTriggerList()){
                            String relation = computeRelation(trigger);
                            if(!relation.equals("")){
                                String attribute = trigger.split(relation)[0];
                                String value = trigger.split(relation)[1];
                                String temp = "(" + relation + " " + attribute + " " + value + ")";
                                expressions.append(temp);
                                z3Fomula.getAttributes().add(attribute);
                            }
                            else {
                                String triggerDevice = trigger.split("\\.")[0];
                                int triggerDeviceStateIndex = stateMappingToInteger.get(trigger);
                                String temp = "(= " + triggerDevice + " " + triggerDeviceStateIndex + ")";
                                expressions.append(temp);
                                z3Fomula.getAttributes().add(triggerDevice);
                            }
                        }
                        expressions.append(") " + "(= " + actionDevice + " " + stateIndex + "))");
                        z3Fomula.getExpressions().add(expressions.toString());
                    }
                }
            }
        }
        it = deviceMappingToFormula.keySet().iterator();
        while (it.hasNext()){
            String device = (String) it.next();
            Z3Fomula z3Fomula = deviceMappingToFormula.get(device);
            result.add(z3Fomula.toString());
        }
        result.add("(check-sat)");
        return result;
    }



    public static void main(String[] args) throws IOException, DocumentException {
        CheckService checkService = new CheckService();
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap("onenet_map.txt", "intendMap", eo);
        String re = "IF Air.humidity<30 AND Light.brightness<30 THEN Window.wclosed//IF Air.temperature>30 THEN Window.wopen//IF Projector.pon THEN Window.wclosed";
        System.out.println(checkService.getZ3Fomulas(re, ontologyPath, 0));
    }

}
