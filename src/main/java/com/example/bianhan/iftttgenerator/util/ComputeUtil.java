package com.example.bianhan.iftttgenerator.util;

import com.example.bianhan.iftttgenerator.pojo.AffectedAttribute;
import com.example.bianhan.iftttgenerator.pojo.Device;
import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import org.dom4j.DocumentException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ComputeUtil {
    /**
     *
     * @param values [>30,>40,<20,<10]
     * @return[>20,<30]
     */
    public static List<String> computeReverseRange(List<String> values){
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
                if(value.contains(">=")){
                    double temp = Double.parseDouble(value.substring(2));
                    if(end > temp) end = temp;
                }
                else if(value.contains("<=")){
                    double temp = Double.parseDouble(value.substring(2));
                    if(start<temp) start = temp;
                }
                else if(value.contains(">")){
                    double temp = Double.parseDouble(value.substring(1));
                    if(end > temp) end = temp;
                }
                else if(value.contains("<")){
                    double temp = Double.parseDouble(value.substring(1));
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

    public static String computeRelation(String value){
        String relation = "";
        if(value.contains("<=")) relation = "<=";
        else if(value.contains(">=")) relation = ">=";
        else if(value.contains(">")) relation = ">";
        else if(value.contains("<")) relation = "<";
        else if(value.contains("!=")) relation = "!=";
        else if(value.contains("=")) relation = "=";
        return relation;
    }

    public static String computeReverseRelation(String value){
        String relation = "";
        if(value.contains("<=")) relation = ">";
        else if(value.contains(">=")) relation = "<";
        else if(value.contains(">")) relation = "<=";
        else if(value.contains("<")) relation = ">=";
        else if(value.contains("!=")) relation = "=";
        else if(value.contains("=")) relation = "!=";
        return relation;
    }


    public static Map computeMap(String mapPath, String type, EnvironmentOntology eo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(mapPath));
        Map<String, String> intendMap = new HashMap<>();
        Map<String, List<String>> triggerMap = new HashMap<>();
        Map<String, List<String>> actionMap = new HashMap<>();
        Map<String, String> paraTypeMap = new HashMap<>();
        Map<String, List<String>> sensorMap = new HashMap<>();
        String line = "";
        while ((line = br.readLine()) != null){
            line = line.trim();
            if(line.equals("")) continue;
            System.out.println(line);
            List<String> temp = new ArrayList<>();
            String left = line.split("->")[0];
            String right = line.split("->")[1];
            for(int i = 0;i < right.split("//").length;i++) temp.add(right.split("//")[i]);
            if(left.startsWith("M.")){
                actionMap.put(left, temp);
                String action = left.split("\\.")[1];
                String state = eo.getActionMappingToState().get(action);
                String deviceName = eo.getDeviceNameByState(state);
                if(line.indexOf("->") != line.lastIndexOf("->")){
                    String intend = line.split("->")[2];
                    if(!intendMap.containsKey(intend))  intendMap.put(intend, deviceName + "." + state);
                    else intendMap.put(intend, intendMap.get(intend) + "," + deviceName + "." + state);
                }
            }
            else if(left.startsWith("$")) paraTypeMap.put(left, right);
            else if(line.startsWith("s:")){
                line = line.substring(2);
                String sensor = line.split("->")[0];
                String attribute = line.split("->")[1];
                List<String> attributes = sensorMap.containsKey(sensor) ?  sensorMap.get(sensor) : new ArrayList<>();
                attributes.add(attribute);
                sensorMap.put(sensor, attributes);
            }
            else triggerMap.put(left,temp);
        }
        if(type.equals("triggerMap")) return triggerMap;
        else if(type.equals("intendMap")) return intendMap;
        else if(type.equals("actionMap")) return actionMap;
        else if(type.equals("paraTypeMap")) return paraTypeMap;
        else if(type.equals("sensorMap")) return sensorMap;
        else return null;
    }

    public static List<IfThenRequirement> computeIfThenRequirements(List<String> requirements, Map<String, String> intendMap, String ontologyPath) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        List<Device> devices = eo.getDevicesAffectingEnvironment();
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        for (String requirement : requirements) {
            requirement = requirement.trim();
            if(requirement.equals("")) continue;
            else if(requirement.contains("ALWAYS") && (requirement.contains("ABOVE") || requirement.contains("BELOW"))){
                String attribute = requirement.split(" ")[0];
                Double value = Double.parseDouble(requirement.split(" ")[5]);
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
                                    if(monitoredEntity.getAdjustRate() > 0 && requirement.contains("ABOVE")){
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        triggers.add(attribute + "<" + value);
                                        actions.add(device.getDeviceName() + "." + state);
                                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null, requirement));
                                    }
                                    else if(monitoredEntity.getAdjustRate() < 0 && requirement.contains("BELOW")){
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        triggers.add(attribute + ">" + value);
                                        actions.add(device.getDeviceName() + "." + state);
                                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null, requirement));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if(requirement.contains("NEVER") && (requirement.contains("ABOVE") || requirement.contains("BELOW"))){
                String attribute = requirement.split(" ")[0];
                Double value = Double.parseDouble(requirement.split(" ")[5]);
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
                                    if(monitoredEntity.getAdjustRate() > 0 && requirement.contains("BELOW")){
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        triggers.add(attribute + "<" + value);
                                        actions.add(device.getDeviceName() + "." + state);
                                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null, requirement));
                                    }
                                    else if(monitoredEntity.getAdjustRate() < 0 && requirement.contains("ABOVE")){
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        triggers.add(attribute + ">" + value);
                                        actions.add(device.getDeviceName() + "." + state);
                                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null, requirement));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if(requirement.contains("PREFERRED") && requirement.contains("IS")){
                String attribute = requirement.split(" ")[1];
                Double value = Double.parseDouble(requirement.split(" ")[3]);
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
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        triggers.add(attribute + "<" + value);
                                        actions.add(device.getDeviceName() + "." + state);
                                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null, requirement));
                                    }
                                    else if(monitoredEntity.getAdjustRate() < 0){
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        triggers.add(attribute + ">=" + value);
                                        actions.add(device.getDeviceName() + "." + state);
                                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, null, requirement));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (requirement.contains("IF") && requirement.contains("THEN") && !requirement.contains("SHOULD")) {
                requirement = requirement.substring(3);
                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
                String action = requirement.split(" THEN ")[1];
                String intend = null;
                if (intendMap.containsKey(action)){
                    intend = action;
                    action = intendMap.get(action);
                }
                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                if (trigger.contains(" AND ")) {
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" AND "));
                    if (action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                } else if (trigger.contains(" OR ")) {
                    for (int i = 0; i < trigger.split(" OR ").length; i++) {
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger.split(" OR ")[i]);
                        if (action.contains(",")) actions = Arrays.asList(action.split(","));
                        else actions.add(action);
                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, time,intend));
                    }
                } else {
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers.add(trigger);
                    if (action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time,intend));
                }
//            } else if (requirement.contains("IF") && requirement.contains("THEN") && requirement.contains("SHOULD")) {
//                requirement = requirement.substring(3);
//                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
//                List<String> actions = new ArrayList<>();
//                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
//                String attributeName = requirement.split(" THEN ")[1].split(" ")[0];
//                for (int i = 0; i < eo.getDevicesAffectingEnvironment().size(); i++) {
//                    Device device = eo.getDevicesAffectingEnvironment().get(i);
//                    String deviceName = device.getDeviceName();
//                    Iterator it = device.getStateMappingToAffectedEntities().keySet().iterator();
//                    while (it.hasNext()) {
//                        String state = (String) it.next();
//                        List<AffectedAttribute> affectedAttributes = device.getStateMappingToAffectedEntities().get(state);
//                        for (AffectedAttribute affectedAttribute : affectedAttributes) {
//                            if (affectedAttribute.getAttributeName().equals(attributeName)) {
//                                if (requirement.contains("INCREASE") && affectedAttribute.getAdjustRate() > 0) {
//                                    actions.add(deviceName + "." + state);
//                                } else if (requirement.contains("DECREASE") && affectedAttribute.getAdjustRate() < 0) {
//                                    actions.add(deviceName + "." + state);
//                                }
//                            }
//                        }
//                    }
//                }
//                if (trigger.contains(" AND ")) {
//                    List<String> triggers = new ArrayList<>();
//                    triggers = Arrays.asList(trigger.split(" AND "));
//                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
//                } else if (trigger.contains(" OR ")) {
//                    for (int i = 0; i < trigger.split(" OR ").length; i++) {
//                        List<String> triggers = new ArrayList<>();
//                        triggers.add(trigger.split(" OR ")[i]);
//                        ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
//                    }
//                } else {
//                    List<String> triggers = new ArrayList<>();
//                    triggers.add(trigger);
//                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
//                }
            }
        }
        return ifThenRequirements;
    }

    public static String modifyDot(String dot){
        dot = dot.replaceAll("S:","");
        dot = dot.replaceAll("\\>","\\\\>");
        dot = dot.replaceAll("\\<","\\\\<");
        return dot;
    }
}
