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

@Service("generateService")
public class GenerateService {
    final String MAPPATH = "map.txt";
    public String toDrools(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        StringBuilder sb = new StringBuilder("");
        int ruleIndex = 1;
        int groupIndex = 1;
        int clockIndex = 1;
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
        Map<String, String> paraTypeMap = new HashMap<>();
        String line = "";
        while ((line = br.readLine()) != null){
            List<String> temp = new ArrayList<>();
            String left = line.split("->")[0];
            String right = line.split("->")[1];
            for(int i = 0;i < right.split("//").length;i++) temp.add(right.split("//")[i]);
            if(left.startsWith("M.")) actionMap.put(left, temp);
            else if(left.startsWith("$")) paraTypeMap.put(left, right);
            else triggerMap.put(left,temp);
        }

        for(String requirement : requirements){
            //state SHOULD ALWAYS BE ACTIVE
            //event SHOULD NEVER HAPPEN
            //state SHOULD NEVER BE ACTIVE
            if(requirement.contains("ALWAYS")){
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
                ruleIndex++;
                groupIndex++;
            }
            else if(requirement.contains("NEVER")){
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
                    ruleIndex++;
                    groupIndex++;
                }
                else {
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
                    ruleIndex++;
                    groupIndex++;
                }
            }
            else if(requirement.contains("IF") && requirement.contains("THEN") && !requirement.contains("SHOULD")){
                requirement = requirement.substring(3);
                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
                String action = requirement.split(" THEN ")[1];
                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                if(trigger.contains(" && ")){
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" && "));
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
                else if(trigger.contains(" || ")){
                    for(int i = 0;i < trigger.split(" || ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger.split(" || ")[i]);
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
                if(trigger.contains(" && ")){
                    List<String> triggers = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" && "));
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
                else if(trigger.contains(" || ")){
                    for(int i = 0;i < trigger.split(" || ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        triggers.add(trigger.split(" || ")[i]);
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
                    String left = action.split("\\.")[0];
                    String right = action.split("\\.")[1];
                    if(eo.getEvents().contains(right)){
                        right = eo.getEventMappingToState().get(right);
                    }
                    action =  "M." + eo.getStateMappingToAction().get(right);
                    List<String> when = new ArrayList<>();
                    List<String> then = new ArrayList<>();
                    for(String trigger : requirement.getTriggerList()){
                        String relation = "";
                        if(trigger.contains(">=")) relation = ">=";
                        else if(trigger.contains("<=")) relation = "<=";
                        else if(trigger.contains("<")) relation = "<";
                        else if(trigger.contains(">")) relation = ">";
                        else if(trigger.contains("!=")) relation = "!=";
                        else if(trigger.contains("=")) relation = "=";
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
                            left = trigger.split("\\.")[0];
                            right = trigger.split("\\.")[1];
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
                    String relation = "";
                    if(trigger.contains(">=")) relation = ">=";
                    else if(trigger.contains("<=")) relation = "<=";
                    else if(trigger.contains("<")) relation = "<";
                    else if(trigger.contains(">")) relation = ">";
                    else if(trigger.contains("!=")) relation = "!=";
                    else if(trigger.contains("=")) relation = "=";
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
                    String relation = "";
                    String replaceRelation = "";
                    if(trigger.contains(">=")){
                        relation = ">=";
                        replaceRelation = "<";
                    }
                    else if(trigger.contains("<=")){
                        relation = "<=";
                        replaceRelation = ">";
                    }
                    else if(trigger.contains("<")){
                        relation = "<";
                        replaceRelation = ">=";
                    }
                    else if(trigger.contains(">")){
                        relation = ">";
                        replaceRelation = "<=";
                    }
                    else if(trigger.contains("!=")){
                        relation = "!=";
                        replaceRelation = "=";
                    }
                    else if(trigger.contains("=")){
                        relation = "=";
                        replaceRelation = "!=";
                    }
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
                    String left = action.split("\\.")[0];
                    String right = action.split("\\.")[1];
                    if(eo.getEvents().contains(right)){
                        right = eo.getEventMappingToState().get(right);
                    }
                    action =  "M." + eo.getStateMappingToAction().get(right);
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

    public List<String> refineRequirements(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        List<String> tempRequirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        List<String> refinedRequirements = new ArrayList<>();
        refinedRequirements.addAll(tempRequirements);
        refinedRequirements.add("");
        refinedRequirements.add("Below Are The Refined Requirements, Change Them Carefully:");
        for(String requirement : refinedRequirements){
            if(requirement.contains("IF") && requirement.contains("THEN") && !requirement.contains("SHOULD")){
                requirement = requirement.substring(3);
                String trigger = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[0] : requirement.split(" THEN ")[0];
                String action = requirement.split(" THEN ")[1];
                String time = requirement.contains(" FOR ") ? requirement.split(" THEN ")[0].split(" FOR ")[1] : null;
                if(trigger.contains(" && ")){
                    List<String> triggers = new ArrayList<>();
                    List<String> actions = new ArrayList<>();
                    triggers = Arrays.asList(trigger.split(" && "));
                    if(action.contains(",")) actions = Arrays.asList(action.split(","));
                    else actions.add(action);
                    ifThenRequirements.add(new IfThenRequirement(triggers, actions, time));
                }
                else if(trigger.contains(" || ")){
                    for(int i = 0;i < trigger.split(" || ").length;i++){
                        List<String> triggers = new ArrayList<>();
                        List<String> actions = new ArrayList<>();
                        triggers.add(trigger.split(" || ")[i]);
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
        refinedRequirements.addAll(getRefinedRequirements(ifThenRequirements,ontologyPath));
        return refinedRequirements;
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
        String reqTexts = "IF air.temperature>30 THEN air.temperature SHOULD DECREASE";
        GenerateService generateService = new GenerateService();
        System.out.println(generateService.toDrools(reqTexts, "ontology_SmartConferenceRoom.xml"));

//        List<String> values = new ArrayList<>();
//        values.add(">30");
//        values.add("<0");
//        GenerateService generateService = new GenerateService();
//        System.out.println(generateService.computeReverse(values));
    }
}
