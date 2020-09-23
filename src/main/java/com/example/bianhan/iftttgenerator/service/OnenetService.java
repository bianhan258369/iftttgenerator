package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.AffectedAttribute;
import com.example.bianhan.iftttgenerator.pojo.Device;
import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service("onnetService")
public class OnenetService {
    final String MAPPATH = "onenet_map.txt";

    public String toOnenet(String requirementTexts, String ontologyPath) throws IOException, DocumentException{
        StringBuilder sb = new StringBuilder("");
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        List<String> requirements = new ArrayList<>();
        List<String> tempRequirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        List<String> addRequirementTexts = new ArrayList<>();
        for(String requirement : tempRequirements){
            List<Device> devices = eo.getDevicesAffectingEnvironment();
            if(requirement.contains("PREFERRED") && requirement.contains("IS")){
                String attribute = requirement.split(" ")[1];
                int value = Integer.parseInt(requirement.split(" ")[3]);
                for(int j = 0;j < devices.size();j++){
                    Device device = devices.get(j);
                    Map map = device.getStateMappingToAffectedEntities();
                    if(device.getAffectedAttributeNames().contains(attribute)){
                        for(int k = 0;k < device.getStates().size();k++){
                            String state = device.getStates().get(k);
                            List<AffectedAttribute> monitoredEntities = (List<AffectedAttribute>) map.get(state);
                            for(int m = 0;m < monitoredEntities.size();m++){
                                AffectedAttribute monitoredEntity = monitoredEntities.get(m);
                                if(monitoredEntity.getAttributeName().equals(attribute)){
                                    if(monitoredEntity.getAdjustRate() > 0){
                                        addRequirementTexts.add("IF " + attribute + "<" + value + " THEN " + device.getDeviceName() + "." + state);
                                    }
                                    else if(monitoredEntity.getAdjustRate() < 0){
                                        addRequirementTexts.add("IF " + attribute + ">=" + value + " THEN " + device.getDeviceName() + "." + state);
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

        BufferedReader br = new BufferedReader(new FileReader(MAPPATH));
        Map<String, List<String>> triggerMap = new HashMap<>();
        Map<String, List<String>> actionMap = new HashMap<>();
        String line = "";
        while ((line = br.readLine()) != null){
            List<String> temp = new ArrayList<>();
            String left = line.split("->")[0];
            String right = line.split("->")[1];
            for(int i = 0;i < right.split("//").length;i++) temp.add(right.split("//")[i]);
            if(left.startsWith("M.")) actionMap.put(left, temp);
            else triggerMap.put(left,temp);
        }

        for(String requirement : requirements){
            if(requirement.contains("ALWAYS")){
                sb.append(transformAlwaysRequirements(requirement, eo, actionMap));
            }

            else if(requirement.contains("NEVER")){
                sb.append(transformNeverRequirements(requirement,eo,actionMap));
            }

            else if(requirement.contains("IF") && requirement.contains("THEN") && !requirement.contains("SHOULD")){
                requirement = requirement.substring(3);
                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
                String action = requirement.split(" THEN ")[1];
                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                if(trigger.contains(" AND ")){
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" AND "));
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
                else if(trigger.contains(" OR ")){
                    for(int i = 0;i < trigger.split(" OR ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger.split(" OR ")[i]);
                        if(action.contains(",")) actions = Arrays.asList(action.split(","));
                        else actions.add(action);
                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                    }
                }
                else {
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers.add(trigger);
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
            }

            else if(requirement.contains("IF") && requirement.contains("THEN") && requirement.contains("SHOULD")){
                requirement = requirement.substring(3);
                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
                List<String> actions = new ArrayList<>();
                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                String attributeName = requirement.split(" THEN ")[1].split(" ")[0];
                for(int i = 0;i < eo.getDevicesAffectingEnvironment().size();i++){
                    Device device = eo.getDevicesAffectingEnvironment().get(i);
                    String deviceName = device.getDeviceName();
                    Iterator it = device.getStateMappingToAffectedEntities().keySet().iterator();
                    while (it.hasNext()){
                        String state = (String) it.next();
                        List<AffectedAttribute> affectedAttributes = device.getStateMappingToAffectedEntities().get(state);
                        for(AffectedAttribute affectedAttribute : affectedAttributes){
                            if(affectedAttribute.getAttributeName().equals(attributeName)){
                                if(requirement.contains("INCREASE") && affectedAttribute.getAdjustRate() > 0){
                                    actions.add(deviceName + "." + state);
                                }
                                else if(requirement.contains("DECREASE") && affectedAttribute.getAdjustRate() < 0){
                                    actions.add(deviceName + "." + state);
                                }
                            }
                        }
                    }
                }
                if(trigger.contains(" AND ")){
                    List<String> triggers = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" AND "));
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
                else if(trigger.contains(" OR ")){
                    for(int i = 0;i < trigger.split(" OR ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        triggers.add(trigger.split(" OR ")[i]);
                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                    }
                }
                else {
                    List<String> triggers = new ArrayList<>();
                    triggers.add(trigger);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
            }
        }

        for(IfThenRequirement requirement : ifThenRequirements) {
            if (requirement.getTime() == null) {
                sb.append("if (");
                for (String trigger : requirement.getTriggerList()){
                    String relation = "";
                    if (trigger.contains(">=")) relation = ">=";
                    else if (trigger.contains("<=")) relation = "<=";
                    else if (trigger.contains("<")) relation = "<";
                    else if (trigger.contains(">")) relation = ">";
                    else if (trigger.contains("!=")) relation = "!=";
                    else if (trigger.contains("=")) relation = "=";
                    if (!relation.equals("")) {
                        String attribute = trigger.split(relation)[0];
                        if (triggerMap.containsKey(attribute) && triggerMap.get(attribute).contains("env")) {
                            String envVar = triggerMap.get(attribute).get(0);
                            sb.append(trigger.replaceAll(attribute, envVar));
                            if(!trigger.equals(requirement.getTriggerList().get(requirement.getTriggerList().size() - 1))) sb.append(" && ");
                        } else {
                            for (int i = 0; i < triggerMap.get(trigger).size(); i++) {
                                sb.append(triggerMap.get(trigger).get(i));
                                if(i != triggerMap.get(trigger).size() - 1) sb.append(" && ");
                            }
                            if(!trigger.equals(requirement.getTriggerList().get(requirement.getTriggerList().size() - 1))) sb.append(" && ");
                        }
                    } else {
                        String left = trigger.split("\\.")[0];
                        String right = trigger.split("\\.")[1];
                        if (eo.getEvents().contains(right)) right = eo.getEventMappingToState().get(right);
                        trigger = left + "." + right;
                        for (int i = 0; i < triggerMap.get(trigger).size(); i++) {
                            sb.append(triggerMap.get(trigger).get(i));
                            if(i != triggerMap.get(trigger).size() - 1) sb.append(" && ");
                        }
                        if(!trigger.equals(requirement.getTriggerList().get(requirement.getTriggerList().size() - 1))) sb.append(" && ");
                    }
                }
                sb.append("){");
                sb.append("\r\n");
                for (String action : requirement.getActionList()) {
                    String left = action.split("\\.")[0];
                    String right = action.split("\\.")[1];
                    if (eo.getEvents().contains(right)) {
                        right = eo.getEventMappingToState().get(right);
                    }
                    action = "M." + eo.getStateMappingToAction().get(right);
                    List<String> actions = actionMap.get(action);
                    for(int i = 0;i < actions.size();i++){
                        sb.append(actions.get(i) + ";");
                        sb.append("\r\n");
                    }
                }
                sb.append("}");
                sb.append("\r\n");
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    private String transformAlwaysRequirements(String requirement, EnvironmentOntology eo, Map<String, List<String>> actionMap){
        StringBuilder sb  =  new StringBuilder();
        String state = requirement.split(" ")[0].split("\\.")[1];
        String action = "M." + eo.getStateMappingToAction().get(state);
        sb.append("if (true){");
        sb.append("\r\n");
        sb.append("Test.addDatapoints(map, type, proDistSensorId, proDistSenApi_key, proDistVal, proDistDatastrId);");
        sb.append("\r\n");
        List<String> actions = actionMap.get(action);
        for(int i = 0;i < actions.size();i++){
            sb.append(actions.get(i) + ";");
            sb.append("\r\n");
        }
        sb.append("}");
        sb.append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    private String transformNeverRequirements(String requirement, EnvironmentOntology eo, Map<String, List<String>> actionMap){
        StringBuilder sb = new StringBuilder();
        if(requirement.contains("HAPPEN")){
            String device = requirement.split(" ")[0].split("\\.")[0];
            String event = requirement.split(" ")[0].split("\\.")[1];
            String action = "M." + eo.getStateMappingToAction().get(eo.getReverseState(device,eo.getEventMappingToState().get(event)));
            sb.append("if (true){");
            sb.append("\r\n");
            sb.append("Test.addDatapoints(map, type, proDistSensorId, proDistSenApi_key, proDistVal, proDistDatastrId);");
            sb.append("\r\n");
            List<String> actions = actionMap.get(action);
            for(int i = 0;i < actions.size();i++){
                sb.append(actions.get(i) + ";");
                sb.append("\r\n");
            }
            sb.append("}");
            sb.append("\r\n");
            sb.append("\r\n");
        }
        else if(requirement.contains("ACTIVE")){
            String device = requirement.split(" ")[0].split("\\.")[0];
            String state = requirement.split(" ")[0].split("\\.")[1];
            String action = "M." + eo.getStateMappingToAction().get(eo.getReverseState(device, state));
            sb.append("if (true){");
            sb.append("\r\n");
            sb.append("Test.addDatapoints(map, type, proDistSensorId, proDistSenApi_key, proDistVal, proDistDatastrId);");
            sb.append("\r\n");
            List<String> actions = actionMap.get(action);
            for(int i = 0;i < actions.size();i++){
                sb.append(actions.get(i) + ";");
                sb.append("\r\n");
            }
            sb.append("}");
            sb.append("\r\n");
            sb.append("\r\n");
            return sb.toString();
        }
        return sb.toString();
    }

    public JSONObject refineRequirements(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        List<String> tempRequirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        for(String requirement : tempRequirements){
            if(requirement.contains("IF") && requirement.contains("THEN") && !requirement.contains("SHOULD")){
                requirement = requirement.substring(3);
                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
                String action = requirement.split(" THEN ")[1];
                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                if(trigger.contains(" AND ")){
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" AND "));
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
                else if(trigger.contains(" OR ")){
                    for(int i = 0;i < trigger.split(" OR ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger.split(" OR ")[i]);
                        if(action.contains(",")) actions = Arrays.asList(action.split(","));
                        else actions.add(action);
                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                    }
                }
                else {
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers.add(trigger);
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
            }
        }
        List<String> refinedRequirements = getRefinedRequirements(ifThenRequirements,ontologyPath);
        result.put("refined", refinedRequirements);
        return result;
    }

    /**
     *
     * @param ifThenRequirements
     * @param ontologyPath
     * @return
     * @throws IOException
     * @throws DocumentException
     * need to be modified
     */
    private List<String> getRefinedRequirements(List<IfThenRequirement> ifThenRequirements, String ontologyPath) throws IOException, DocumentException {
        List<String> refinedRequirements = new ArrayList<>();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, List<String>> deviceMappingToStates = new HashMap<>();
        for(IfThenRequirement requirement : ifThenRequirements){
            if(requirement.getTime() == null){
                List<String> actions = requirement.getActionList();
                for(String action : actions){
                    String deviceName = action.split("\\.")[0];
                    String temp = action.split("\\.")[1];
                    String state = eo.getEvents().contains(temp) ? eo.getEventMappingToState().get(temp) : temp;
                    if(!deviceMappingToStates.containsKey(deviceName)) deviceMappingToStates.put(deviceName, new ArrayList<>());
                    if(!deviceMappingToStates.get(deviceName).contains(state)) deviceMappingToStates.get(deviceName).add(state);
                }
            }
        }
        List<String> devicesShouldBeRefined = new ArrayList<>();
        Iterator it = deviceMappingToStates.keySet().iterator();
        while (it.hasNext()){
            String deviceName = (String) it.next();
            if(!deviceMappingToStates.get(deviceName).contains(eo.getDeviceMappingToInitState().get(deviceName))) devicesShouldBeRefined.add(deviceName);
        }
        for(String deviceName : devicesShouldBeRefined){
            Map<String, List<String>> attributeMappingToValue = new HashMap<>();//[air.temperature->(>30,<10)]
            for(IfThenRequirement requirement : ifThenRequirements){
                if(requirement.getTime() == null){
                    List<String> actions = requirement.getActionList();
                    for(String action : actions){
                        if(action.split("\\.")[0].equals(deviceName) && requirement.getTriggerList().size() == 1){
                            String relation = "";
                            String trigger = requirement.getTriggerList().get(0);
                            if(trigger.contains("<=")) relation = "<=";
                            else if(trigger.contains(">=")) relation = ">=";
                            else if(trigger.contains(">")) relation = ">";
                            else if(trigger.contains("<")) relation = "<";
                            else if(trigger.contains("!=")) relation = "!=";
                            else if(trigger.contains("=")) relation = "=";
                            String attribute = trigger.split(relation)[0];
                            String value = relation + trigger.split(relation)[1];
                            if(!attributeMappingToValue.containsKey(attribute)) attributeMappingToValue.put(attribute, new ArrayList<>());
                            if(!attributeMappingToValue.get(attribute).contains(value)) attributeMappingToValue.get(attribute).add(value);
                            break;
                        }
                    }
                }
            }
            if(attributeMappingToValue.keySet().size() > 1) continue;
            String attribute = "";
            List<String> values = new ArrayList<>();
            it = attributeMappingToValue.keySet().iterator();
            while (it.hasNext()){
                attribute = (String) it.next();
                values = attributeMappingToValue.get(attribute);
            }
            List<String> triggers = computeReverse(values);
            for(String trigger : triggers){
                refinedRequirements.add("IF " + attribute + trigger + " THEN " + deviceName + "." + eo.getDeviceMappingToInitState().get(deviceName));
            }
        }

        StringBuilder returnInit = new StringBuilder("");
        it = eo.getDeviceMappingToInitState().keySet().iterator();
        while (it.hasNext()){
            String deviceName = (String) it.next();
            String initState = eo.getDeviceMappingToInitState().get(deviceName);
            returnInit.append(deviceName + "." + initState);
            if(it.hasNext()) returnInit.append(",");
        }
        refinedRequirements.add("IF person.number=0 FOR 30m THEN " + returnInit.toString());
        return refinedRequirements;
    }

    /**
     *
     * @param values [>30,>40,<20,<10]
     * @return[>20,<30]
     */
    private List<String> computeReverse(List<String> values){
        List<String> result = new ArrayList<>();
        if(values.size() == 1){
            String value = values.get(0);
            if(value.contains(">=")) result.add("<" + value.substring(2));
            else if(value.contains("<=")) result.add(">" + value.substring(2));
            else if(value.contains(">")) result.add("<=" + value.substring(1));
            else if(value.contains("<")) result.add(">=" + value.substring(1));
            else if(value.contains("!=")) result.add("=" + value.substring(2));
            else if(value.contains("=")) result.add("!=" + value.substring(1));
        }
        else {
            double start = Double.NEGATIVE_INFINITY;
            double end = Double.POSITIVE_INFINITY;
            for(String value : values){
                if(value.contains(">")){
                    double temp = Double.parseDouble(value.substring(1));
                    if(end > temp) end = temp;
                }
                else if(value.contains(">=")){
                    double temp = Double.parseDouble(value.substring(2));
                    if(end > temp) end = temp;
                }
                else if(value.contains("<")){
                    double temp = Double.parseDouble(value.substring(1));
                    if(start<temp) start = temp;
                }
                else if(value.contains("<=")){
                    double temp = Double.parseDouble(value.substring(2));
                    if(start<temp) start = temp;
                }
            }
            if(start == Double.MIN_VALUE && end == Double.MAX_VALUE || start >= end){

            }
            else if(start == Double.MIN_VALUE){
                result.add("<" + end);
            }
            else if(end == Double.MAX_VALUE){
                result.add(">" + start);
            }
            else {
                result.add(">" + start);
                result.add("<" + end);
            }

        }
        return result;
    }


    public static void main(String[] args) throws IOException, DocumentException {
        String re = "IF air.temperature>30 THEN air.temperature SHOULD DECREASE";
        OnenetService onenetService = new OnenetService();
        System.out.println(onenetService.toOnenet(re, "ontology_SmartConferenceRoom.xml"));
    }
}
