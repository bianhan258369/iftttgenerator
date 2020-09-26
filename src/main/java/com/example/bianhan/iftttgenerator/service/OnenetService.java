package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.util.Configuration;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("onnetService")
public class OnenetService {
    public String toOnenet(String requirementTexts, String ontologyPath) throws IOException, DocumentException{
        StringBuilder sb = new StringBuilder("");
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);

        Map<String, String> intendMap = computeMap(Configuration.ONENETMAPPATH, "intendMap",eo);
        Map<String, List<String>> triggerMap = computeMap(Configuration.ONENETMAPPATH, "triggerMap",eo);
        Map<String, List<String>> actionMap = computeMap(Configuration.ONENETMAPPATH, "actionMap",eo);

        List<String> requirements = computeRequirements(requirementTexts, ontologyPath);
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath);
        System.out.println(ifThenRequirements);

        for(IfThenRequirement requirement : ifThenRequirements) {
            if (requirement.getTime() == null) {
                sb.append("if (");
                for (String trigger : requirement.getTriggerList()){
                    String relation = computeRelation(trigger);
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
                    if(!action.startsWith("M.")){
                        String left = action.split("\\.")[0];
                        String right = action.split("\\.")[1];
                        if (eo.getEvents().contains(right)) {
                            right = eo.getEventMappingToState().get(right);
                        }
                        action =  "M." + eo.getStateMappingToAction().get(right);
                    }
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
                            String trigger = requirement.getTriggerList().get(0);
                            String relation = computeRelation(trigger);
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
            List<String> triggers = computeReverseRange(values);
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


    public static void main(String[] args) throws IOException, DocumentException {
        String re = "IF air.temperature>30 THEN allow ventilating the room//IF person.distanceFromMc<0.5 THEN mc.mon";
        OnenetService onenetService = new OnenetService();
        System.out.println(onenetService.toOnenet(re, "ontology_SmartConferenceRoom.xml"));
    }
}
