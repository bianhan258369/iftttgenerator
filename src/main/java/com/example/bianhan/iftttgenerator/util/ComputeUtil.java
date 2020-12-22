package com.example.bianhan.iftttgenerator.util;

import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.service.IFTTTService;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.*;

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
            int start = Integer.MIN_VALUE;
            int end = Integer.MAX_VALUE;
            for(String value : values){
                if(value.contains(">=")){
                    int temp = Integer.parseInt(value.substring(2));
                    if(end > temp) end = temp;
                }
                else if(value.contains("<=")){
                    int temp = Integer.parseInt(value.substring(2));
                    if(start<temp) start = temp;
                }
                else if(value.contains(">")){
                    int temp = Integer.parseInt(value.substring(1));
                    if(end > temp) end = temp;
                }
                else if(value.contains("<")){
                    int temp = Integer.parseInt(value.substring(1));
                    if(start<temp) start = temp;
                }
            }
            if(start == Integer.MIN_VALUE && end == Integer.MAX_VALUE || start >= end){

            }
            else if(start == Integer.MIN_VALUE){
                result.add("<" + end);
            }
            else if(end == Integer.MAX_VALUE){
                result.add(">" + start);
            }
            else {
                result.add(">" + start);
                result.add("<" + end);
            }

        }
        return result;
    }

    public static String getPythonFromJava(String java) throws IOException {
        java = java.trim().toLowerCase();
        Map<String, String> map = new HashMap();
        BufferedReader br = new BufferedReader(new FileReader("JavaMappingToPython.txt"));
        String line;
        while ((line = br.readLine()) != null){
            String from = line.split("->")[0].toLowerCase();
            String to = line.split("->")[1].toLowerCase();
            map.put(from, to);
        }
        if(map.containsKey(java)) return map.get(java);
        else {
            //air.temperature>30
            if(java.contains(">=") || java.contains(">")){
                String relation = computeRelation(java);
                return java.split(relation)[0] + "=over" + java.split(relation)[1];
            }
            //air.temperature<30
            else if(java.contains("<=") || java.contains("<")){
                String relation = computeRelation(java);
                return java.split(relation)[0] + "=below" + java.split(relation)[1];
            }
            //person.number=0
            else if(java.contains("=")) return java.split("=")[0] + "=equals" + java.split("=")[1];
        }
        return null;
    }

    public static String getJavaFromPython(String python) throws IOException {
        python = python.trim().toLowerCase();
        boolean notFlag = false;
        String result = null;
        Map<String, String> map = new HashMap();
        BufferedReader br = new BufferedReader(new FileReader("JavaMappingToPython.txt"));
        String line;
        while ((line = br.readLine()) != null){
            String from = line.split("->")[1].toLowerCase();
            String to = line.split("->")[0].toLowerCase();
            map.put(from, to);
        }
        if(python.contains("!")){
            notFlag = true;
            python = python.substring(1);
        }
        if(map.containsKey(python)){
            result = map.get(python);
            if (notFlag) result = "!" + result;
        }
        else {
            //air.temperature=over30
            if(python.contains("=over")){
                if(notFlag) result = python.split("=over")[0] + "<" + python.split("=over")[1];
                else result = python.split("=over")[0] + ">" + python.split("=over")[1];
            }
            //air.temperature=below30
            else if(python.contains("=below")){
                if(notFlag) result = python.split("=below")[0] + ">" + python.split("=below")[1];
                else result = python.split("=below")[0] + "<" + python.split("=below")[1];
            }
            else if(python.contains("=equals")){
                if(notFlag) result =  python.split("=equals")[0] + "!=" + python.split("=equals")[1];
                else result =  python.split("=equals")[0] + "=" + python.split("=equals")[1];
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

    public static Map computeEffectMap() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("DeviceRegitrationTable.txt"));
        String line = "";
        Map<String, String> effectMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.equals("") || line.startsWith("s:")) continue;
            String effect = line.split("->")[0];
            String state = line.split("->")[1];
            effectMap.put(effect, state);
        }
        return effectMap;
    }

    public static Map computeSensorMap() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("DeviceRegitrationTable.txt"));
        String line = "";
        Map<String, List<String>> sensorMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.equals("") || !line.startsWith("s:")) continue;
            line = line.substring(2);
            String sensor = line.split("->")[0];
            String attribute = line.split("->")[1];
            List<String> attributes = sensorMap.containsKey(sensor) ?  sensorMap.get(sensor) : new ArrayList<>();
            attributes.add(attribute);
            sensorMap.put(sensor, attributes);
        }
        return sensorMap;
    }

    public static Map computeMap(String mapPath, String type, EnvironmentOntology eo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(mapPath));
        Map<String, List<String>> triggerMap = new HashMap<>();
        Map<String, List<String>> actionMap = new HashMap<>();
        Map<String, String> paraTypeMap = new HashMap<>();
        String line = "";
        while ((line = br.readLine()) != null){
            line = line.trim();
            if(line.equals("")) continue;
            List<String> temp = new ArrayList<>();
            String left = line.split("->")[0];
            String right = line.split("->")[1];
            for(int i = 0;i < right.split("//").length;i++) temp.add(right.split("//")[i]);
            if(left.startsWith("M.")){
                actionMap.put(left, temp);
                String action = left.split("\\.")[1];
                String state = eo.getActionMappingToState().get(action);
                String deviceName = eo.getDeviceNameByState(state);
            }
            else if(left.startsWith("$")) paraTypeMap.put(left, right);
            else triggerMap.put(left,temp);
        }
        if(type.equals("triggerMap")) return triggerMap;
        else if(type.equals("actionMap")) return actionMap;
        else if(type.equals("paraTypeMap")) return paraTypeMap;
        else return null;
    }

    public static List<Requirement> initRequirements(List<String> inputRequirements) throws IOException, DocumentException {
        List<Requirement> requirements = new ArrayList<>();
        for (String requirement : inputRequirements) {
            requirement = requirement.trim();
            //empty
            if(requirement.equals("")) continue;
            //OccurenceRequirement
            else if(requirement.contains("OCCUR TOGETHER")){
                List<String> deviceStates = Arrays.asList(requirement.split(" SHOULD ")[0].split(","));
                requirements.add(new OccurenceRequirement(requirement, deviceStates));
            }
            //AlwaysNeverRequirement
            else if(requirement.contains("ALWAYS") || requirement.contains("NEVER")){
                String alwaysNever = requirement.contains("ALWAYS") ? "ALWAYS" : "NEVER";
                if(requirement.contains("ABOVE") || requirement.contains("BELOW")){
                    String attribute = requirement.split(" ")[0];
                    String relation = requirement.contains("ABOVE") ? "ABOVE" : "BELOW";
                    int value = Integer.parseInt(requirement.split(" ")[5]);
                    requirements.add(new AlwaysNeverRequirement(requirement, alwaysNever, attribute, relation, value));
                }
                else if(requirement.contains("ACTIVE") || requirement.contains("HAPPEN")){
                    String deviceEventOrState = requirement.split(" SHOULD ")[0];
                    requirements.add(new AlwaysNeverRequirement(requirement, alwaysNever, deviceEventOrState));
                }
            }
            //PreferredRequirement
            else if(requirement.contains("PREFERRED")){
                String attribute = requirement.split(" ")[1];
                int value = Integer.parseInt(requirement.split(" ")[3]);
                requirements.add(new PreferredRequirement(requirement, attribute, value));
            }
            //TriggerActionRequirement
            else if(requirement.contains("IF") && requirement.contains("THEN")){
                String tempRequirement = requirement.substring(3);
                String trigger = tempRequirement.contains(" FOR ") ? tempRequirement.split(" THEN ")[0].split(" FOR ")[0] : tempRequirement.split(" THEN ")[0];
                String action = tempRequirement.split(" THEN ")[1];
                String time = tempRequirement.contains(" FOR ") ? tempRequirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                requirements.add(new TriggerActionRequirement(requirement, trigger, action, time));
            }
        }
        return requirements;
    }

    public static List<List<IfThenRequirement>> computeIfThenRequirements(List<Requirement> requirements, Map<String, String> effectMap, String ontologyPath) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        List<Device> devices = eo.getDevicesAffectingEnvironment();
        List<List<IfThenRequirement>> results = new ArrayList<>();
        List<IfThenRequirement> random = new ArrayList<>();
        List<IfThenRequirement> saveEnergy = new ArrayList<>();
        List<IfThenRequirement> bestPerformance = new ArrayList<>();
        for (Requirement requirement : requirements) {
            String originalRequirement = requirement.getRequirement();
            //AlwaysNeverRequirement
            if(requirement instanceof AlwaysNeverRequirement){
                AlwaysNeverRequirement alwaysNeverRequirement = (AlwaysNeverRequirement) requirement;
                if(alwaysNeverRequirement.getAttribute() != null){
                    String attribute = alwaysNeverRequirement.getAttribute();
                    int value = alwaysNeverRequirement.getValue();
                    Double minEnergy = Double.MAX_VALUE;
                    Double maxRate = Double.MIN_VALUE;
                    IfThenRequirement minEnergyReq = null;
                    IfThenRequirement maxRateReq = null;
                    boolean randomAddFlag = true;
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
                                        List<String> triggers = new ArrayList<>();
                                        List<String> actions = new ArrayList<>();
                                        if(monitoredEntity.getAdjustRate() > 0 && alwaysNeverRequirement.getAlwaysNever().equals("ALWAYS") && alwaysNeverRequirement.getRelation().equals("ABOVE")
                                        || monitoredEntity.getAdjustRate() > 0 && alwaysNeverRequirement.getAlwaysNever().equals("NEVER") && alwaysNeverRequirement.getRelation().equals("BELOW")){
                                            triggers.add(attribute + "<" + value);
                                            actions.add(device.getDeviceName() + "." + state);
                                            if(randomAddFlag){
                                                random.add(new IfThenRequirement(triggers, actions, null, originalRequirement));
                                                randomAddFlag = false;
                                            }
                                            if(Math.abs(monitoredEntity.getAdjustRate()) > maxRate){
                                                maxRate = Math.abs(monitoredEntity.getAdjustRate());
                                                maxRateReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                            }
                                            if(Math.abs(monitoredEntity.getEnergy()) < minEnergy){
                                                minEnergy = Math.abs(monitoredEntity.getEnergy());
                                                minEnergyReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                            }
                                        }
                                        else if(monitoredEntity.getAdjustRate() < 0 && alwaysNeverRequirement.getAlwaysNever().equals("ALWAYS") && alwaysNeverRequirement.getRelation().equals("BELOW")
                                        || monitoredEntity.getAdjustRate() < 0 && alwaysNeverRequirement.getAlwaysNever().equals("NEVER") && alwaysNeverRequirement.getRelation().equals("ABOVE")){
                                            triggers.add(attribute + ">" + value);
                                            actions.add(device.getDeviceName() + "." + state);
                                            if(randomAddFlag){
                                                random.add(new IfThenRequirement(triggers, actions, null, originalRequirement));
                                                randomAddFlag = false;
                                            }
                                            if(Math.abs(monitoredEntity.getAdjustRate()) > maxRate){
                                                maxRate = Math.abs(monitoredEntity.getAdjustRate());
                                                maxRateReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                            }
                                            if(Math.abs(monitoredEntity.getEnergy()) < minEnergy){
                                                minEnergy = Math.abs(monitoredEntity.getEnergy());
                                                minEnergyReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    bestPerformance.add(maxRateReq);
                    saveEnergy.add(minEnergyReq);
                }
            }
            //PreferredRequirement
            else if(requirement instanceof PreferredRequirement){
                PreferredRequirement preferredRequirement = (PreferredRequirement) requirement;
                String attribute = preferredRequirement.getAttribute();
                int value = preferredRequirement.getValue();
                Double minEnergy = Double.MAX_VALUE;
                Double maxRate = Double.MIN_VALUE;
                IfThenRequirement minEnergyReq = null;
                IfThenRequirement maxRateReq = null;
                boolean randomAddFlag = true;
                for(int j = 0;j < devices.size();j++){
                    Device device = devices.get(j);
                    Map map = device.getStateMappingToAffectedEntities();
                    if(device.getAffectedAttributeNames().contains(attribute)){
                        for(int k = 0;k < device.getStates().size();k++){
                            String state = device.getStates().get(k);
                            List<AffectedAttribute> monitoredEntities = (List<AffectedAttribute>) map.get(state);
                            for(int m = 0;m < monitoredEntities.size();m++){
                                AffectedAttribute monitoredEntity = monitoredEntities.get(m);
                                if(monitoredEntity.getAttributeName().equals(attribute) && monitoredEntity.getAdjustRate() > 0){
                                    List<String> triggers = new ArrayList<>();
                                    List<String> actions = new ArrayList<>();
                                    triggers.add(attribute + "<" + value);
                                    actions.add(device.getDeviceName() + "." + state);
                                    if(randomAddFlag){
                                        random.add(new IfThenRequirement(triggers, actions, null, originalRequirement));
                                        randomAddFlag = false;
                                    }
                                    if(Math.abs(monitoredEntity.getAdjustRate()) > maxRate){
                                        maxRate = Math.abs(monitoredEntity.getAdjustRate());
                                        maxRateReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                    }
                                    if(Math.abs(monitoredEntity.getEnergy()) < minEnergy){
                                        minEnergy = Math.abs(monitoredEntity.getEnergy());
                                        minEnergyReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                    }
                                }
                            }
                        }
                    }
                }
                bestPerformance.add(maxRateReq);
                saveEnergy.add(minEnergyReq);

                minEnergy = Double.MAX_VALUE;
                maxRate = Double.MIN_VALUE;
                minEnergyReq = null;
                maxRateReq = null;
                randomAddFlag = true;
                for(int j = 0;j < devices.size();j++){
                    Device device = devices.get(j);
                    Map map = device.getStateMappingToAffectedEntities();
                    if(device.getAffectedAttributeNames().contains(attribute)){
                        for(int k = 0;k < device.getStates().size();k++){
                            String state = device.getStates().get(k);
                            List<AffectedAttribute> monitoredEntities = (List<AffectedAttribute>) map.get(state);
                            for(int m = 0;m < monitoredEntities.size();m++){
                                AffectedAttribute monitoredEntity = monitoredEntities.get(m);
                                if(monitoredEntity.getAttributeName().equals(attribute) && monitoredEntity.getAdjustRate() < 0){
                                    List<String> triggers = new ArrayList<>();
                                    List<String> actions = new ArrayList<>();
                                    triggers.add(attribute + ">=" + value);
                                    actions.add(device.getDeviceName() + "." + state);
                                    if(randomAddFlag){
                                        random.add(new IfThenRequirement(triggers, actions, null, originalRequirement));
                                        randomAddFlag = false;
                                    }
                                    if(Math.abs(monitoredEntity.getAdjustRate()) > maxRate){
                                        maxRate = Math.abs(monitoredEntity.getAdjustRate());
                                        maxRateReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                    }
                                    if(Math.abs(monitoredEntity.getEnergy()) < minEnergy){
                                        minEnergy = Math.abs(monitoredEntity.getEnergy());
                                        minEnergyReq = new IfThenRequirement(triggers, actions, null, originalRequirement);
                                    }
                                }
                            }
                        }
                    }
                }
                bestPerformance.add(maxRateReq);
                System.out.println(minEnergyReq);
                saveEnergy.add(minEnergyReq);
            }
            //TriggerActionRequirement
            else if(requirement instanceof TriggerActionRequirement){
                TriggerActionRequirement triggerActionRequirement = (TriggerActionRequirement) requirement;
                String trigger = triggerActionRequirement.getTrigger();
                String action = triggerActionRequirement.getAction();
                String time = triggerActionRequirement.getTime();
                Double minEnergy = Double.MAX_VALUE;
                Double maxEnergy = Double.MIN_VALUE;
                String saveEnergyAction = null;
                String bestPerformanceAction = null;
                String randomAction = null;
                boolean randomAddFlag = true;
                boolean effectFlag = false;
                if (effectMap.containsKey(action)){
                    if(!effectMap.get(action).contains("//")) action = effectMap.get(action);
                    else {
                        effectFlag = true;
                        String[] actions = effectMap.get(action).split("//");
                        for(String tempAction : actions){
                            if(randomAddFlag){
                                randomAction = tempAction;
                                randomAddFlag = false;
                            }
                            if(tempAction.contains(",")){
                                Double totalEnergy = 0.0;
                                for(String temp : tempAction.split(",")){
                                    String deviceName = temp.split("\\.")[0];
                                    String state = temp.split("\\.")[1];
                                    Device device = null;
                                    for(Device tempDevice : devices){
                                        if(tempDevice.getDeviceName().equals(deviceName)){
                                            device = tempDevice;
                                            break;
                                        }
                                    }
                                    Map map = device.getStateMappingToAffectedEntities();
                                    AffectedAttribute monitoredEntitity = ((List<AffectedAttribute>) map.get(state)).get(0);
                                    Double energy = monitoredEntitity.getEnergy();
                                    totalEnergy += Math.abs(energy);
                                }
                                if(totalEnergy > maxEnergy){
                                    maxEnergy = totalEnergy;
                                    bestPerformanceAction = tempAction;
                                }
                                if(totalEnergy < minEnergy){
                                    minEnergy = totalEnergy;
                                    saveEnergyAction = tempAction;
                                }
                            }
                            else {
                                String deviceName = tempAction.split("\\.")[0];
                                String state = tempAction.split("\\.")[1];
                                Device device = null;
                                for(Device tempDevice : devices){
                                    if(tempDevice.getDeviceName().equals(deviceName)){
                                        device = tempDevice;
                                        break;
                                    }
                                }
                                Map map = device.getStateMappingToAffectedEntities();
                                AffectedAttribute monitoredEntitity = ((List<AffectedAttribute>) map.get(state)).get(0);
                                Double energy = monitoredEntitity.getEnergy();
                                if(Math.abs(energy) > maxEnergy){
                                    maxEnergy = Math.abs(energy);
                                    bestPerformanceAction = tempAction;
                                }
                                if(Math.abs(energy) < minEnergy){
                                    minEnergy = Math.abs(energy);
                                    saveEnergyAction = tempAction;
                                }
                            }
                        }
                    }
                }
                if(!effectFlag){
                    if (trigger.contains(" AND ")) {
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers = Arrays.asList(trigger.split(" AND "));
                        if (action.contains(",")) actions = Arrays.asList(action.split(","));
                        else actions.add(action);
                        random.add(new IfThenRequirement(triggers, actions, time, originalRequirement));
                        saveEnergy.add(new IfThenRequirement(triggers, actions, time, originalRequirement));
                        bestPerformance.add(new IfThenRequirement(triggers, actions, time, originalRequirement));
                    } else if (trigger.contains(" OR ")) {
                        for (int i = 0; i < trigger.split(" OR ").length; i++) {
                            List<String> triggers = new ArrayList<>();
                            List<String> actions = new ArrayList<>();
                            triggers.add(trigger.split(" OR ")[i]);
                            if (action.contains(",")) actions = Arrays.asList(action.split(","));
                            else actions.add(action);
                            random.add(new IfThenRequirement(triggers, actions, time,originalRequirement));
                            saveEnergy.add(new IfThenRequirement(triggers, actions, time,originalRequirement));
                            bestPerformance.add(new IfThenRequirement(triggers, actions, time,originalRequirement));
                        }
                    } else {
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger);
                        if (action.contains(",")) actions = Arrays.asList(action.split(","));
                        else actions.add(action);
                        random.add(new IfThenRequirement(triggers, actions, time, originalRequirement));
                        saveEnergy.add(new IfThenRequirement(triggers, actions, time, originalRequirement));
                        bestPerformance.add(new IfThenRequirement(triggers, actions, time, originalRequirement));
                    }
                }
                else {
                    if (trigger.contains(" AND ")) {
                        List<String> triggers = new ArrayList<>();
                        triggers = Arrays.asList(trigger.split(" AND "));
                        List<String> randomActions = new ArrayList<>();
                        List<String> saveEnergyActions = new ArrayList<>();
                        List<String> bestPerformanceActions = new ArrayList<>();
                        if (randomAction.contains(",")) randomActions = Arrays.asList(randomAction.split(","));
                        else randomActions.add(randomAction);
                        if (saveEnergyAction.contains(",")) saveEnergyActions = Arrays.asList(saveEnergyAction.split(","));
                        else saveEnergyActions.add(saveEnergyAction);
                        if (bestPerformanceAction.contains(",")) bestPerformanceActions = Arrays.asList(bestPerformanceAction.split(","));
                        else bestPerformanceActions.add(bestPerformanceAction);
                        random.add(new IfThenRequirement(triggers, randomActions, time, originalRequirement));
                        saveEnergy.add(new IfThenRequirement(triggers, saveEnergyActions, time, originalRequirement));
                        bestPerformance.add(new IfThenRequirement(triggers, bestPerformanceActions, time, originalRequirement));
                    } else if (trigger.contains(" OR ")) {
                        for (int i = 0; i < trigger.split(" OR ").length; i++) {
                            List<String> triggers = new ArrayList<>();
                            triggers.add(trigger.split(" OR ")[i]);
                            List<String> randomActions = new ArrayList<>();
                            List<String> saveEnergyActions = new ArrayList<>();
                            List<String> bestPerformanceActions = new ArrayList<>();
                            if (randomAction.contains(",")) randomActions = Arrays.asList(randomAction.split(","));
                            else randomActions.add(randomAction);
                            if (saveEnergyAction.contains(",")) saveEnergyActions = Arrays.asList(saveEnergyAction.split(","));
                            else saveEnergyActions.add(saveEnergyAction);
                            if (bestPerformanceAction.contains(",")) bestPerformanceActions = Arrays.asList(bestPerformanceAction.split(","));
                            else bestPerformanceActions.add(bestPerformanceAction);
                            random.add(new IfThenRequirement(triggers, randomActions, time, originalRequirement));
                            saveEnergy.add(new IfThenRequirement(triggers, saveEnergyActions, time, originalRequirement));
                            bestPerformance.add(new IfThenRequirement(triggers, bestPerformanceActions, time, originalRequirement));
                        }
                    } else {
                        List<String> triggers = new ArrayList<>();
                        triggers.add(trigger);
                        List<String> randomActions = new ArrayList<>();
                        List<String> saveEnergyActions = new ArrayList<>();
                        List<String> bestPerformanceActions = new ArrayList<>();
                        if (randomAction.contains(",")) randomActions = Arrays.asList(randomAction.split(","));
                        else randomActions.add(randomAction);
                        if (saveEnergyAction.contains(",")) saveEnergyActions = Arrays.asList(saveEnergyAction.split(","));
                        else saveEnergyActions.add(saveEnergyAction);
                        if (bestPerformanceAction.contains(",")) bestPerformanceActions = Arrays.asList(bestPerformanceAction.split(","));
                        else bestPerformanceActions.add(bestPerformanceAction);
                        random.add(new IfThenRequirement(triggers, randomActions, time, originalRequirement));
                        saveEnergy.add(new IfThenRequirement(triggers, saveEnergyActions, time, originalRequirement));
                        bestPerformance.add(new IfThenRequirement(triggers, bestPerformanceActions, time, originalRequirement));
                    }
                }
            }
        }
        results.add(random);
        results.add(saveEnergy);
        results.add(bestPerformance);
        return results;
    }

    public static List<String> computeReverseRequirements(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException, InterruptedException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> effectMap = computeEffectMap();
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);
        List<String> reverseRequirements = new ArrayList<>();
        Map<String, List<String>> triggerAndDeviceMappingToStates = new HashMap<>();
        for(IfThenRequirement requirement : ifThenRequirements){
            if(requirement.getTime() == null && requirement.getTriggerList().size() == 1){
                List<String> actions = requirement.getActionList();
                String trigger = requirement.getTriggerList().get(0);
                if(eo.getEvents().contains(trigger)) continue;
                for(String action : actions){
                    String attritbueOrTriggerDevice = "";
                    String relation = computeRelation(trigger);
                    if(relation.equals("")) attritbueOrTriggerDevice = trigger.split("\\.")[0];
                    else attritbueOrTriggerDevice = trigger.split(relation)[0];
                    String triggerAndDeviceName = attritbueOrTriggerDevice + "//" + action.split("\\.")[0];
                    String temp = action.split("\\.")[1];
                    String state = eo.getEvents().contains(temp) ? eo.getEventMappingToState().get(temp) : temp;
                    if(!triggerAndDeviceMappingToStates.containsKey(triggerAndDeviceName)) triggerAndDeviceMappingToStates.put(triggerAndDeviceName, new ArrayList<>());
                    if(!triggerAndDeviceMappingToStates.get(triggerAndDeviceName).contains(state)) triggerAndDeviceMappingToStates.get(triggerAndDeviceName).add(state);
                }
            }
        }
        List<String> devicesShouldBeRefined = new ArrayList<>();
        Iterator it = triggerAndDeviceMappingToStates.keySet().iterator();
        while (it.hasNext()){
            String triggerAndDeviceName = (String) it.next();
            String deviceName = triggerAndDeviceName.split("//")[1];
            if(!triggerAndDeviceMappingToStates.get(triggerAndDeviceName).contains(eo.getDeviceMappingToInitState().get(deviceName))) devicesShouldBeRefined.add(triggerAndDeviceName);
        }
        for(String triggerAndDeviceName : devicesShouldBeRefined){
            String triggerAttrubute = triggerAndDeviceName.split("//")[0];
            String deviceName = triggerAndDeviceName.split("//")[1];
            Map<String, List<String>> attributeMappingToValue = new HashMap<>();//[air.temperature->(>30,<10)]
            for(IfThenRequirement requirement : ifThenRequirements){
                if(requirement.getTime() == null){
                    List<String> actions = requirement.getActionList();
                    for(String action : actions){
                        if(action.split("\\.")[0].equals(deviceName) && requirement.getTriggerList().size() == 1){
                            String trigger = requirement.getTriggerList().get(0);
                            String relation = computeRelation(trigger);
                            String attribute = "";
                            if(relation.equals("")) break;
                            else attribute = trigger.split(relation)[0];
                            if(attribute.equals(triggerAttrubute)){
                                String value = relation + trigger.split(relation)[1];
                                if(!attributeMappingToValue.containsKey(attribute)) attributeMappingToValue.put(attribute, new ArrayList<>());
                                if(!attributeMappingToValue.get(attribute).contains(value)) attributeMappingToValue.get(attribute).add(value);
                                break;
                            }
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
            List<String> triggers = computeReverseRange(values);
            if(triggers.size() > 0 && attribute != null && !attribute.trim().equals("")){
                for(String trigger : triggers){
                    reverseRequirements.add("IF " + attribute + trigger + " THEN " + deviceName + "." + eo.getDeviceMappingToInitState().get(deviceName));
                }
            }
        }

        return reverseRequirements;
    }

    public static List<String> computeComplementedRequirements(String ontologyPath) throws IOException, DocumentException, InterruptedException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        List<String> complementedRequirements = new ArrayList<>();
        Iterator it = eo.getDeviceMappingToInitState().keySet().iterator();
        while (it.hasNext()){
            String deviceName = (String) it.next();
            String initState = eo.getDeviceMappingToInitState().get(deviceName);
            complementedRequirements.add("IF person.number=0 FOR 30m THEN " + deviceName + "." + initState);
        }
        return complementedRequirements;
    }

        public static JSONObject toFunctionalRequirements(String requirementTexts, String ontologyPath, int index, String complementedRequirements) throws IOException, DocumentException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        List<IfThenRequirement> ifThenRequirementList = new ArrayList<>();
        List<String> functionalRequirements = new ArrayList<>();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> effectMap = computeEffectMap();

        List<Requirement> requirements = initRequirements(Arrays.asList(requirementTexts.split("//")));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, effectMap, ontologyPath).get(index);

        //initial Evaluation.py
        Map<String, Map<String, Set<String>>> entityMappingToAttrubuteMappingToValue = new HashMap<>();
        for(IfThenRequirement requirement : ifThenRequirements){
            List<String> trggerList = requirement.getTriggerList();
            for(String trigger : trggerList){
                String relation = computeRelation(trigger);
                //air.temperature>30
                if(!relation.equals("")){
                    String entity = trigger.split(relation)[0].split("\\.")[0];
                    String attribute = trigger.split(relation)[0].split("\\.")[1];
                    String value = trigger.split(relation)[1];
                    if(!entityMappingToAttrubuteMappingToValue.containsKey(entity))
                        entityMappingToAttrubuteMappingToValue.put(entity, new HashMap<>());
                    Map<String, Set<String>> attributeMappingToValue = entityMappingToAttrubuteMappingToValue.get(entity);
                    if(relation.equals(">=") || relation.equals(">")){
                        if(!attributeMappingToValue.containsKey(attribute)) attributeMappingToValue.put(attribute, new HashSet<>());
                        attributeMappingToValue.get(attribute).add("over" + value);
                    }
                    else if(relation.equals("<=") || relation.equals("<")){
                        if(!attributeMappingToValue.containsKey(attribute)) attributeMappingToValue.put(attribute, new HashSet<>());
                        attributeMappingToValue.get(attribute).add("below" + value);
                    }
                    else if(relation.equals("=")){
                        if(!attributeMappingToValue.containsKey(attribute)) attributeMappingToValue.put(attribute, new HashSet<>());
                        attributeMappingToValue.get(attribute).add("equals" + value);
                    }
                    entityMappingToAttrubuteMappingToValue.put(entity, attributeMappingToValue);
                }
            }
        }
        List<String> adds = new ArrayList<>();
        Iterator it = entityMappingToAttrubuteMappingToValue.keySet().iterator();
        //air = {'temperature': 'set, [over20, below20]','humidity': 'set, [over70, below100,below70]'}
        while (it.hasNext()){
            String entity = (String) it.next();
            String add = entity + " = {";
            Map<String, Set<String>> attributeMappingToValue =  entityMappingToAttrubuteMappingToValue.get(entity);
            Iterator itt = attributeMappingToValue.keySet().iterator();
            while (itt.hasNext()){
                add = add + "\'";
                String attribute = (String) itt.next();
                add = add + attribute + "\': \'set, [";
                Set<String> values = attributeMappingToValue.get(attribute);
                for(String value : values){
                    add = add + value + ", ";
                }
                add = add.substring(0, add.length() - 2);
                add = add + "]\',";
            }
            add = add.substring(0, add.length() - 1);
            add = add + "}";
            adds.add(add);
        }
        BufferedReader br = new BufferedReader(new FileReader(TEMPLATEPATH + "evaluation_copy.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(TEMPLATEPATH + "Evaluation.py"));
        String line = null;
        while ((line = br.readLine()) != null){
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        for(String add : adds){
            bw.write(add);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();

        //synthisize using autotap
        List<String> functionalAndNonfunctionalRequirements = new ArrayList<>();
        boolean flag = false;
        for(Requirement requirement : requirements){
            if(requirement instanceof AlwaysNeverRequirement){
                String req = "";
                AlwaysNeverRequirement alwaysNeverRequirement = (AlwaysNeverRequirement) requirement;
                if(alwaysNeverRequirement.getAttribute() == null){
                    flag = true;
                    String deviceEventOrState = alwaysNeverRequirement.getDeviceEventOrState();
                    String device = deviceEventOrState.split("\\.")[0];
                    String eventOrState = deviceEventOrState.split("\\.")[1];
                    if (eo.getEvents().contains(eventOrState)) {
                        eventOrState = eo.getEventMappingToState().get(eventOrState);
                    }
                    String deviceState = device + "." + eventOrState;
                    req = req + getPythonFromJava(deviceState.trim().toLowerCase());
                    if(alwaysNeverRequirement.getAlwaysNever().equals("ALWAYS")){
                        req = req + " SHOULD ALWAYS BE ACTIVE";
                        functionalAndNonfunctionalRequirements.add(req);
                    }
                    else {
                        req = req + " SHOULD NEVER HAPPEN";
                        functionalAndNonfunctionalRequirements.add(req);
                    }
                }
            }
            else if(requirement instanceof OccurenceRequirement){
                flag = true;
                String req = "";
                OccurenceRequirement occurenceRequirement = (OccurenceRequirement) requirement;
                List<String> deviceStates = occurenceRequirement.getDeviceStates();
                for(int i = 0;i < deviceStates.size();i++){
                    req = req + getPythonFromJava(deviceStates.get(i).trim().toLowerCase());
                    if(i != deviceStates.size() - 1) req = req + ",";
                }
                req = req + " SHOULD NEVER OCCUR TOGETHER";
                functionalAndNonfunctionalRequirements.add(req);
            }
        }

        for(IfThenRequirement requirement : ifThenRequirements){
            String time = requirement.getTime();
            if(true){
                for(int i = 0;i < requirement.getActionList().size();i++){
                    String deviceAndState = "";
                    String action = requirement.getActionList().get(i);
                    String left = action.split("\\.")[0];
                    String right = action.split("\\.")[1];
                    if (eo.getEvents().contains(right)) {
                        right = eo.getEventMappingToState().get(right);
                    }
                    deviceAndState = left + "." +right;
                    String trigger = requirement.getTriggerList().get(0);
                    List<String> conditions = new ArrayList<>();
                    conditions.addAll(requirement.getTriggerList());
                    conditions.remove(0);
                    String autotapTrigger = getPythonFromJava(trigger);
                    String autotapAction = getPythonFromJava(deviceAndState);
                    if(conditions.size() == 0) functionalAndNonfunctionalRequirements.add("IF " + autotapTrigger + " THEN " + autotapAction);
                    else {
                        String autotapCondition = "";
                        for(int j = 0;j< conditions.size();j++){
                            autotapCondition = autotapCondition + conditions.get(j);
                            if(j != conditions.size() - 1) autotapCondition = autotapCondition + ",";
                        }
                        functionalAndNonfunctionalRequirements.add("IF " + autotapTrigger + " WHILE " + autotapCondition + " THEN " + autotapAction);
                    }
                }
            }
            else {
                /*
                time != null
                TODO
                 */
            }
        }
        bw = new BufferedWriter(new FileWriter("autotapInput.txt"));
        for(String requirement : functionalAndNonfunctionalRequirements){
            bw.write(requirement);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();

        if(flag){
            String cmd = "python3 " + PYTHONCMDPATH + " autotapInput.txt";
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(cmd);
                InputStream is = proc.getInputStream();
                InputStream es = proc.getErrorStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    String req = "";
                    String trigger = "";
                    String action = "";
                    if(line.contains(", ")){
                        //IF <ac.mode=Cold> WHILE [window.on=true], THEN <['ac.mode=Off', 'window.on=false']>.
                        if(line.contains("WHILE")){
                            trigger = line.split(" ")[1];
                            action = line.split(" ")[5];
                            trigger = trigger.substring(1, trigger.length() - 1);
                            trigger = getJavaFromPython(trigger);
                            if(action.contains(",")) action = action.substring(3,action.indexOf(",") - 1);
                            else action = action.substring(3, action.length() - 4);
                            action = getJavaFromPython(action);
                            String conditions = line.split(" ")[3];
                            conditions = conditions.substring(1, conditions.length() - 2);
                            if(conditions.contains(" AND ")){
                                trigger = trigger + " AND " + getJavaFromPython(conditions);
                            }
                            else {
                                for(int i = 0;i < conditions.split(" AND ").length;i++){
                                    String condition = conditions.split(" AND ")[i];
                                    condition = getJavaFromPython(condition);
                                    trigger = trigger + " AND " + condition;
                                }
                            }
                        }
                        //IF <projector.on=true>, THEN <['window.on=false']>.
                        else {
                            trigger = line.split(" ")[1];
                            action = line.split(" ")[3];
                            trigger = trigger.substring(1, trigger.length() - 2);
                            trigger = getJavaFromPython(trigger);
                            if(action.contains(",")) action = action.substring(3,action.indexOf(",") - 1);
                            else action = action.substring(3, action.length() - 4);
                            action = getJavaFromPython(action);
                        }
                    }

                    //IF person.distancefrommc=below2 THEN microphone.on=true
                    else {
                        trigger = line.split(" ")[1];
                        action = line.split(" ")[3];
                        trigger = getJavaFromPython(trigger);
                        action = getJavaFromPython(action);
                    }
                    req = "IF " + trigger + " THEN " + action;
                    functionalRequirements.add(req);
                    boolean matchFlag = false;
                    for(IfThenRequirement ifThenRequirement : ifThenRequirements){
                        String originalTrigger = ifThenRequirement.getTriggerList().get(0);
                        List<String> originalActions = ifThenRequirement.getActionList();
                        List<String> functionalTriggers = Arrays.asList(trigger.split(" AND "));
                        String functionalAction = action;
                        if(originalActions.contains(functionalAction)){
                            for(String functionalTrigger : functionalTriggers){
                                if(functionalTrigger.equals(originalTrigger))matchFlag = true;
                                else{
                                    String originalRelation = computeRelation(originalTrigger);
                                    String functionalRelation = computeRelation(functionalTrigger);
                                    if(!originalRelation.equals("") && !functionalRelation.equals("")){
                                        String originalEntityAndAttribute = originalTrigger.split(originalRelation)[0];
                                        String originalValue = originalTrigger.split(originalRelation)[1];
                                        String functionalEntiyAndAttribute = functionalTrigger.split(functionalRelation)[0];
                                        String functionalValue = functionalTrigger.split(functionalRelation)[1];
                                        if(originalEntityAndAttribute.equals(functionalEntiyAndAttribute) && originalValue.equals(functionalValue)){
                                            if((originalRelation.equals(">=") && functionalRelation.equals(">")) || (originalRelation.equals("<=") && functionalRelation.equals("<"))) matchFlag = true;
                                        }
                                    }
                                }
                            }
                        }
                        if(matchFlag){
                            List<String> functionalActions = new ArrayList<>();
                            functionalActions.add(functionalAction);
                            ifThenRequirementList.add(new IfThenRequirement(functionalTriggers, functionalActions ,null ,ifThenRequirement.getExpectation()));
                            break;
                        }
                    }
                    for(Requirement requirement : requirements){
                        boolean flag1 = false;
                        boolean flag2 = false;
                        List<String> functionalTriggers = Arrays.asList(trigger.split(" AND "));
                        String functionalAction = action;
                        List<String> functionalActions = new ArrayList<>();
                        functionalActions.add(functionalAction);
                        List<String> functionalTriggersAndActions = new ArrayList<>();
                        functionalTriggersAndActions.addAll(functionalTriggers);
                        functionalTriggersAndActions.add(functionalAction);
                        if(requirement instanceof OccurenceRequirement){
                            OccurenceRequirement occurenceRequirement = (OccurenceRequirement) requirement;
                            List<String> deviceStates = occurenceRequirement.getDeviceStates();
                            String temp = "";
                            for(String deviceState : deviceStates){
                                if(functionalTriggersAndActions.contains(deviceState)){
                                    flag1 = true;
                                    temp = deviceState;
                                    break;
                                }
                            }
                            for(String deviceState : deviceStates){
                                if(!deviceState.equals(temp)){
                                    for(String functionalTriggerAndAction : functionalTriggersAndActions){
                                        String tempDevice = temp.split("\\.")[0];
                                        String functionalDevice = functionalTriggerAndAction.contains(".") ? functionalTriggerAndAction.split("\\.")[0] : "";
                                        if(functionalDevice.startsWith("!")) functionalDevice = functionalDevice.substring(1);
                                        String device = deviceState.split("\\.")[0];
                                        if(device.equals(functionalDevice) && !device.equals(tempDevice)){
                                            flag2 = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if(flag1 & flag2){
                            IfThenRequirement temp = new IfThenRequirement(functionalTriggers, functionalActions ,null ,requirement.getRequirement());
                            if(!ifThenRequirementList.contains(temp))ifThenRequirementList.add(temp);
                            else{
                                int tempIndex = ifThenRequirementList.indexOf(temp);
                                ifThenRequirementList.get(tempIndex).addExpectation(temp.getExpectation());
                            }
                        }
                    }
                }
                br = new BufferedReader(new InputStreamReader(es, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
        else {
            for(IfThenRequirement ifThenRequirement : ifThenRequirements){
                ifThenRequirementList.add(ifThenRequirement);
                functionalRequirements.add(ifThenRequirement.getIfThenClause());
            }
        }
        List<Requirement> tempRequirements = initRequirements(Arrays.asList(complementedRequirements.split("//")));
        List<IfThenRequirement> tempIfThenRequirements = computeIfThenRequirements(tempRequirements, effectMap, ontologyPath).get(index);
        ifThenRequirementList.addAll(tempIfThenRequirements);
        jsonObject.put("functionalRequirements",functionalRequirements);
        jsonObject.put("ifThenRequirements", ifThenRequirementList);
        return jsonObject;
    }

    public static String toSystemBehaviours(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        StringBuilder sb = new StringBuilder("");
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> effectMap = computeEffectMap();

        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);

        for(IfThenRequirement requirement : ifThenRequirements){
            String triggers = "";
            String actions = "";
            String time = requirement.getTime();
            for(int i = 0;i < requirement.getActionList().size();i++){
                String action = requirement.getActionList().get(i);
                if(!action.startsWith("M.")){
                    String left = action.split("\\.")[0];
                    String right = action.split("\\.")[1];
                    if (eo.getEvents().contains(right)) {
                        right = eo.getEventMappingToState().get(right);
                    }
                    action =  "M." + eo.getStateMappingToAction().get(right);
                    actions = actions + action;
                    if(i != requirement.getActionList().size() - 1) actions = actions + ",";
                }
            }
            for(int i = 0;i < requirement.getTriggerList().size();i++){
                String trigger = requirement.getTriggerList().get(i);
                triggers = triggers + trigger;
                if(i != requirement.getTriggerList().size() - 1) triggers = triggers + " AND ";
            }
            if(time == null) sb.append("IF " + triggers + " THEN " + actions);
            else sb.append("IF " + triggers + " FOR " + time + " THEN " + actions);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public static JSONObject computeResources(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        Map<String, String> effectMap = computeEffectMap();
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);

        Map<String, Set<Resource>> deviecStateAndResourcesUsed = new HashMap<>();
        File deviceRegistrationTable = new File(DEVICEREGITRATIONTABLEPATH);
        BufferedReader br = new BufferedReader(new FileReader(deviceRegistrationTable));
        String line = "";
        while ((line = br.readLine()) != null){
            if(line.startsWith("r:")){
                line = line.substring(2);
                String deviceState = line.split("->")[0];
                if(!deviecStateAndResourcesUsed.containsKey(deviceState)) deviecStateAndResourcesUsed.put(deviceState, new HashSet<>());
                List<String> resources = Arrays.asList(line.split("->")[1].split("//"));
                for(String resource : resources){
                    String resourceName = resource.split("=")[0];
                    Double value = Double.parseDouble(resource.split("=")[1]);
                    Resource temp = new Resource(resourceName, value);
                    deviecStateAndResourcesUsed.get(deviceState).add(temp);
                }
            }
        }

        Map<String, Set<Resource>> deviceMappingToResources = new HashMap<>();
        Map<String, Double> resourceNameMappingToValue = new HashMap<>();
        for(IfThenRequirement ifThenRequirement : ifThenRequirements){
            List<String> actions = ifThenRequirement.getActionList();
            for(String deviceState : actions){
                if(deviecStateAndResourcesUsed.containsKey(deviceState)){
                    String deviceName = deviceState.split("\\.")[0];
                    if(!deviceMappingToResources.containsKey(deviceName)) deviceMappingToResources.put(deviceName, new HashSet<>());
                    Set<Resource> resources = deviecStateAndResourcesUsed.get(deviceState);
                    for(Resource resource : resources){
                        deviceMappingToResources.get(deviceName).add(resource);
                    }
                }
            }
        }
        Iterator it = deviceMappingToResources.keySet().iterator();
        while (it.hasNext()){
            String deviceName = (String) it.next();
            Set<Resource> resources = deviceMappingToResources.get(deviceName);
            Map<String, Double> resourceNameMappingToMaxValues = new HashMap<>();
            for(Resource resource : resources){
                if(!resourceNameMappingToMaxValues.containsKey(resource.getResourceName()))resourceNameMappingToMaxValues.put(resource.getResourceName(), resource.getValue());
                else {
                    if(resourceNameMappingToMaxValues.get(resource.getResourceName()) < resource.getValue()) resourceNameMappingToMaxValues.put(resource.getResourceName(), resource.getValue());
                }
            }
            for(String resourceName : resourceNameMappingToMaxValues.keySet()){
                Double value = resourceNameMappingToMaxValues.get(resourceName);
                if(!resourceNameMappingToValue.containsKey(resourceName)) resourceNameMappingToValue.put(resourceName, value);
                else resourceNameMappingToValue.put(resourceName, resourceNameMappingToValue.get(resourceName) + value);
            }
        }

        it = resourceNameMappingToValue.keySet().iterator();
        while (it.hasNext()){
            String resourceName = (String) it.next();
            Double value = resourceNameMappingToValue.get(resourceName);
            result.put(resourceName, value);
        }
        return  result;
    }


    public static String modifyDot(String dot){
        dot = dot.replaceAll("S:","");
        dot = dot.replaceAll("\\>","\\\\>");
        dot = dot.replaceAll("\\<","\\\\<");
        return dot;
    }

    public static boolean strListEquals(List<String> list1, List<String> list2){
        if(list1.size() != list2.size()) return false;
        for(String temp : list1){
            if (!list2.contains(temp)) return false;
        }
        for(String temp : list2){
            if (!list1.contains(temp)) return false;
        }
        return true;
    }

//    public static void main(String[] args) throws IOException {
//        BufferedReader br = new BufferedReader(new FileReader("onenet_map.xml"));
//        BufferedWriter bw = new BufferedWriter(new FileWriter("ontology.xml"));
//        String line = null;
//        while ((line = br.readLine()) != null){
//            bw.write(line.toLowerCase());
//            bw.newLine();
//            bw.flush();
//        }
//        br.close();
//        bw.close();
//    }

    public static void main(String[] args) throws IOException, DocumentException {
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        String requirementTexts = "IF air.temperature<30 THEN ac.coldon//IF air.temperature>10 THEN ac.hoton//IF light.brightness<35 THEN bulb.bon//IF air.humidity<70 THEN ah.ahon";
        System.out.println(computeResources(requirementTexts, ontologyPath,0));
    }
}
