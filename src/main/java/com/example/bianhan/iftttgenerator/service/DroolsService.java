package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("generateService")
public class DroolsService {
    public String toDrools(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        StringBuilder sb = new StringBuilder("");
        int ruleIndex = 1;
        int groupIndex = 1;
        int clockIndex = 1;
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);

        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);
        Map<String, List<String>> triggerMap = computeMap(PathConfiguration.DROOLSMAPPATH, "triggerMap", eo);
        Map<String, List<String>> actionMap = computeMap(PathConfiguration.DROOLSMAPPATH, "actionMap", eo);
        Map<String, String> paraTypeMap = computeMap(PathConfiguration.DROOLSMAPPATH, "paraTypeMap", eo);

        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath);

        for(IfThenRequirement requirement : ifThenRequirements){
            if(requirement.getTime()==null){
                int salience = 0;
                for(String action : requirement.getActionList()){
                    sb.append("rule R" + ruleIndex);
                    sb.append("\r\n");
                    sb.append("group G" + groupIndex);
                    sb.append("\r\n");
                    if(salience != 0){
                        sb.append("salience " + salience);
                        sb.append("\r\n");
                    }
                    sb.append("lock-on-active true");
                    sb.append("\r\n");
                    if(!action.startsWith("M.")){
                        String left = action.split("\\.")[0];
                        String right = action.split("\\.")[1];
                        if (eo.getEvents().contains(right)) {
                            right = eo.getEventMappingToState().get(right);
                        }
                        action =  "M." + eo.getStateMappingToAction().get(right);
                    }
                    List<String> when = new ArrayList<>();
                    List<String> then = new ArrayList<>();
                    for(String trigger : requirement.getTriggerList()){
                        String relation = computeRelation(trigger);
                        if(!relation.equals("")){
                            String entity = trigger.split(relation)[0];
                            if(triggerMap.containsKey(entity) && triggerMap.get(entity).contains("env")){
                                String envVar = triggerMap.get(entity).get(0);
                                when.add("Environ(" + trigger.replaceAll(entity, envVar) + ")");
                            }
                            else {
                                for(int i = 0;i < triggerMap.get(trigger).size();i++){
                                    when.add(triggerMap.get(trigger).get(i));
                                }
                            }
                        }
                        else {
                            String left = trigger.split("\\.")[0];
                            String right = trigger.split("\\.")[1];
                            if(eo.getEvents().contains(right)) right = eo.getEventMappingToState().get(right);
                            trigger = left + "." + right;
                            for(int j = 0;j < triggerMap.get(trigger).size();j++){
                                when.add(triggerMap.get(trigger).get(j));
                            }
                        }
                    }
                    List<String> actions = actionMap.get(action);
                    for(int i = 0;i < actions.size();i++){
                        then.add(actions.get(i) + ";");
                    }
                    for(int i = 0;i < then.size();i++){
                        String clause = then.get(i);
                        boolean enterFlag = false;//whether enter the judge loop
                        boolean flag = false;//whether when has the initialize statement
                        if(clause.startsWith("$") && clause.contains(".")){
                            enterFlag = true;
                            for(int j = 0;j < when.size();j++){
                                if(when.get(j).contains(clause.substring(0, clause.indexOf(".")) + ":")){
                                    flag = true;
                                }
                            }
                        }
                        if(!flag && enterFlag){
                            when.add(clause.substring(0, clause.indexOf(".")) + ":" + paraTypeMap.get(clause.substring(0, clause.indexOf("."))));
                        }
                    }
                    //write when and then
                    sb.append("  when");
                    sb.append("\r\n");
                    for(int i = 0;i < when.size();i++){
                        sb.append("    " + when.get(i));
                        sb.append("\r\n");
                    }

                    sb.append("  then");
                    sb.append("\r\n");
                    for(int i = 0;i < then.size();i++){
                        sb.append("    " + then.get(i));
                        sb.append("\r\n");
                    }
                    sb.append("end");
                    sb.append("\r\n");
                    sb.append("\r\n");
                    salience--;
                    ruleIndex++;
                }
                groupIndex++;
            }
            else {
                String time = requirement.getTime();
                sb.append("rule R" + ruleIndex + "Clock1");
                sb.append("\r\n");
                sb.append("group G" + groupIndex);
                sb.append("\r\n");
                sb.append("lock-on-active true");
                sb.append("\r\n");
                sb.append("  when");
                sb.append("\r\n");
                if(clockIndex == 1) sb.append("    $clocker:DurClocker(durFlag==false)");
                else sb.append("    $clocker:DurClocker" + clockIndex + "(durFlag==false)");
                sb.append("\r\n");
                for(String trigger : requirement.getTriggerList()){
                    String relation = computeRelation(trigger);
                    if(!relation.equals("")){
                        String entity = trigger.split(relation)[0];
                        if(triggerMap.containsKey(entity) && triggerMap.get(entity).contains("env")){
                            String envVar = triggerMap.get(entity).get(0);
                            sb.append("    Environ(" + trigger.replaceAll(entity, envVar) + ")");
                            sb.append("\r\n");
                        }
                        else {
                            for(int i = 0;i < triggerMap.get(trigger).size();i++){
                                sb.append("    " + triggerMap.get(trigger).get(i));
                                sb.append("\r\n");
                            }
                        }
                    }
                    else {
                        String left = trigger.split("\\.")[0];
                        String right = trigger.split("\\.")[1];
                        if(eo.getEvents().contains(right)) right = eo.getEventMappingToState().get(right);
                        trigger = left + "." + right;
                        for(int j = 0;j < triggerMap.get(trigger).size();j++){
                            sb.append("    " + triggerMap.get(trigger).get(j));
                            sb.append("\r\n");
                        }
                    }
                }
                sb.append("  then");
                sb.append("\r\n");
                if(time.contains("h")) sb.append("    $clocker.setDureTime(" + time.split("h")[0] + ",0,0);");
                else if(time.contains("m")) sb.append("    $clocker.setDureTime(0," + time.split("m")[0] + ",0);");
                else sb.append("    $clocker.setDureTime(0,0," + time.split("s")[0] + ");");
                sb.append("\r\n");
                sb.append("    update($clocker);");
                sb.append("\r\n");
                sb.append("end");
                sb.append("\r\n");
                sb.append("\r\n");

                sb.append("rule R" + ruleIndex + "Clock2");
                sb.append("\r\n");
                sb.append("group G" + groupIndex);
                sb.append("\r\n");
                sb.append("lock-on-active true");
                sb.append("\r\n");
                sb.append("  when");
                sb.append("\r\n");
                for(String trigger : requirement.getTriggerList()){
                    String relation = computeRelation(trigger);
                    String replaceRelation = computeReverseRelation(trigger);
                    trigger = trigger.replaceAll(relation, replaceRelation);
                    if(!relation.equals("")){
                        String entity = trigger.split(replaceRelation)[0];
                        if(triggerMap.containsKey(entity) && triggerMap.get(entity).contains("env")){
                            String envVar = triggerMap.get(entity).get(0);
                            sb.append("    Environ(" + trigger.replaceAll(entity, envVar) + ")");
                            sb.append("\r\n");
                        }
                        else {
                            for(int i = 0;i < triggerMap.get(trigger).size();i++){
                                sb.append("    " + triggerMap.get(trigger).get(i));
                                sb.append("\r\n");
                            }
                        }
                    }
                    if(clockIndex == 1) sb.append("    $clocker:DurClocker(durFlag==true)");
                    else sb.append("    $clocker:DurClocker" + clockIndex + "(durFlag==true)");
                    sb.append("\r\n");
                    sb.append("  then");
                    sb.append("\r\n");
                    sb.append("    $clocker.clearDureTime();");
                    sb.append("\r\n");
                    sb.append("    update($clocker);");
                    sb.append("\r\n");
                    sb.append("end");
                    sb.append("\r\n");
                    sb.append("\r\n");
                }
                sb.append("rule R" + ruleIndex + "Clock3");
                sb.append("\r\n");
                sb.append("group G" + groupIndex);
                sb.append("\r\n");
                sb.append("lock-on-active true");
                sb.append("\r\n");
                List<String> when = new ArrayList<>();
                List<String> then = new ArrayList<>();
                for(String action : requirement.getActionList()){
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
                        then.add(actions.get(i) + ";");
                    }
                    for(int i = 0;i < then.size();i++){
                        String clause = then.get(i);
                        boolean enterFlag = false;//whether enter the judge loop
                        boolean flag = false;//whether when has the initialize statement
                        if(clause.startsWith("$") && clause.contains(".")){
                            enterFlag = true;
                            for(int j = 0;j < when.size();j++){
                                if(when.get(j).contains(clause.substring(0, clause.indexOf(".")) + ":")){
                                    flag = true;
                                }
                            }
                        }
                        if(!flag && enterFlag){
                            when.add(clause.substring(0, clause.indexOf(".")) + ":" + paraTypeMap.get(clause.substring(0, clause.indexOf("."))));
                        }
                    }
                }
                //write when and then
                sb.append("  when");
                sb.append("\r\n");
                if(clockIndex == 1) sb.append("    clocker:DurClocker(hour==0,minute==0,seconds==0)");
                else sb.append("    $clocker:DurClocker" + clockIndex + "(hour==0,minute==0,seconds==0)");
                sb.append("\r\n");
                for(int i = 0;i < when.size();i++){
                    sb.append("    " + when.get(i));
                    sb.append("\r\n");
                }

                sb.append("  then");
                sb.append("\r\n");
                for(int i = 0;i < then.size();i++){
                    sb.append("    " + then.get(i));
                    sb.append("\r\n");
                }
                sb.append("end");
                sb.append("\r\n");
                sb.append("\r\n");
                ruleIndex++;
                clockIndex++;
            }
        }
        return sb.toString();
    }

    public JSONObject refineRequirements(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath);
        List<String> refinedRequirements = getRefinedRequirements(ifThenRequirements,ontologyPath);
        result.put("refined", refinedRequirements);
        return result;
    }

    private String transformAlwaysRequirements(String requirement, EnvironmentOntology eo, int ruleIndex, int groupIndex,
                                               Map<String, List<String>> actionMap, Map<String, String> paraTypeMap){
        StringBuilder sb  =  new StringBuilder();
        String state = requirement.split(" ")[0].split("\\.")[1];
        String action = "M." + eo.getStateMappingToAction().get(state);
        sb.append("rule R" + ruleIndex);
        sb.append("\r\n");
        sb.append("group G" + groupIndex);
        sb.append("\r\n");
        sb.append("salience 100");
        sb.append("\r\n");
        sb.append("lock-on-active true");
        sb.append("\r\n");
        List<String> when = new ArrayList<>();
        List<String> then = new ArrayList<>();
        List<String> actions = actionMap.get(action);
        for(int i = 0;i < actions.size();i++){
            then.add(actions.get(i) + ";");
        }
        for(int i = 0;i < then.size();i++){
            String clause = then.get(i);
            boolean enterFlag = false;//whether enter the judge loop
            boolean flag = false;//whether when has the initialize statement
            if(clause.startsWith("$") && clause.contains(".")){
                enterFlag = true;
                for(int j = 0;j < when.size();j++){
                    if(when.get(j).contains(clause.substring(0, clause.indexOf(".")) + ":")){
                        flag = true;
                    }
                }
            }
            if(!flag && enterFlag){
                when.add(clause.substring(0, clause.indexOf(".")) + ":" + paraTypeMap.get(clause.substring(0, clause.indexOf("."))));
            }
        }
        sb.append("  when");
        sb.append("\r\n");
        for(int i = 0;i < when.size();i++){
            sb.append("    " + when.get(i));
            sb.append("\r\n");
        }

        sb.append("  then");
        sb.append("\r\n");
        for(int i = 0;i < then.size();i++){
            sb.append("    " + then.get(i));
            sb.append("\r\n");
        }
        sb.append("end");
        sb.append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    private String transformNeverRequirements(String requirement, EnvironmentOntology eo, int ruleIndex, int groupIndex,
                                              Map<String, List<String>> actionMap, Map<String, String> paraTypeMap){
        StringBuilder sb = new StringBuilder();
        if(requirement.contains("HAPPEN")){
            String device = requirement.split(" ")[0].split("\\.")[0];
            String event = requirement.split(" ")[0].split("\\.")[1];
            String action = "M." + eo.getStateMappingToAction().get(eo.getReverseState(device,eo.getEventMappingToState().get(event)));
            sb.append("rule R" + ruleIndex);
            sb.append("\r\n");
            sb.append("group G" + groupIndex);
            sb.append("\r\n");
            sb.append("salience 100");
            sb.append("\r\n");
            sb.append("lock-on-active true");
            sb.append("\r\n");
            List<String> when = new ArrayList<>();
            List<String> then = new ArrayList<>();
            List<String> actions = actionMap.get(action);
            for(int i = 0;i < actions.size();i++){
                then.add(actions.get(i) + ";");
            }
            for(int i = 0;i < then.size();i++){
                String clause = then.get(i);
                boolean enterFlag = false;//whether enter the judge loop
                boolean flag = false;//whether when has the initialize statement
                if(clause.startsWith("$") && clause.contains(".")){
                    enterFlag = true;
                    for(int j = 0;j < when.size();j++){
                        if(when.get(j).contains(clause.substring(0, clause.indexOf(".")) + ":")){
                            flag = true;
                        }
                    }
                }
                if(!flag && enterFlag){
                    when.add(clause.substring(0, clause.indexOf(".")) + ":" + paraTypeMap.get(clause.substring(0, clause.indexOf("."))));
                }
            }
            sb.append("  when");
            sb.append("\r\n");
            for(int i = 0;i < when.size();i++){
                sb.append("    " + when.get(i));
                sb.append("\r\n");
            }

            sb.append("  then");
            sb.append("\r\n");
            for(int i = 0;i < then.size();i++){
                sb.append("    " + then.get(i));
                sb.append("\r\n");
            }
            sb.append("end");
            sb.append("\r\n");
            sb.append("\r\n");
        }
        else if(requirement.contains("ACTIVE")){
            String device = requirement.split(" ")[0].split("\\.")[0];
            String state = requirement.split(" ")[0].split("\\.")[1];
            String action = "M." + eo.getStateMappingToAction().get(eo.getReverseState(device, state));
            sb.append("rule R" + ruleIndex);
            sb.append("\r\n");
            sb.append("group G" + groupIndex);
            sb.append("\r\n");
            sb.append("salience 100");
            sb.append("\r\n");
            sb.append("lock-on-active true");
            sb.append("\r\n");
            List<String> when = new ArrayList<>();
            List<String> then = new ArrayList<>();
            List<String> actions = actionMap.get(action);
            for(int i = 0;i < actions.size();i++){
                then.add(actions.get(i) + ";");
            }
            for(int i = 0;i < then.size();i++){
                String clause = then.get(i);
                boolean enterFlag = false;//whether enter the judge loop
                boolean flag = false;//whether when has the initialize statement
                if(clause.startsWith("$") && clause.contains(".")){
                    enterFlag = true;
                    for(int j = 0;j < when.size();j++){
                        if(when.get(j).contains(clause.substring(0, clause.indexOf(".")) + ":")){
                            flag = true;
                        }
                    }
                }
                if(!flag && enterFlag){
                    when.add(clause.substring(0, clause.indexOf(".")) + ":" + paraTypeMap.get(clause.substring(0, clause.indexOf("."))));
                }
            }
            sb.append("  when");
            sb.append("\r\n");
            for(int i = 0;i < when.size();i++){
                sb.append("    " + when.get(i));
                sb.append("\r\n");
            }

            sb.append("  then");
            sb.append("\r\n");
            for(int i = 0;i < then.size();i++){
                sb.append("    " + then.get(i));
                sb.append("\r\n");
            }
            sb.append("end");
            sb.append("\r\n");
            sb.append("\r\n");
        }
        return sb.toString();
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
            if(triggers.size() > 0 && attribute != null && !attribute.trim().equals("")){
                for(String trigger : triggers){
                    refinedRequirements.add("IF " + attribute + trigger + " THEN " + deviceName + "." + eo.getDeviceMappingToInitState().get(deviceName));
                }
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
        String re = "IF air.temperature>30 FOR 10m THEN allow ventilating the room//IF person.distanceFromMc<=2 THEN allow using microphone";
        DroolsService droolsService = new DroolsService();
        System.out.println(droolsService.toDrools(re, "ontology_SmartConferenceRoom.xml"));
    }
}
