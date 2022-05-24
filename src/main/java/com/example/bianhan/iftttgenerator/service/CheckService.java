package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.SMTPATH;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("checkService")
public class CheckService {

    public JSONObject check(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> effectMap = computeEffectMap();
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);

        JSONObject result = new JSONObject();
        Map<String, List<Conflict>> groupMappingToSolvableErrors = new HashMap<>();
        List<Conflict> unsolvableErrors = new ArrayList<>();
        //trigger conflict
        //if t>10 AND t<10 then a
        for(int i = 0;i < ifThenRequirements.size();i++){
            IfThenRequirement ifThenRequirement = ifThenRequirements.get(i);
            List<String> triggerList = ifThenRequirement.getTriggerList();
            if(selfConflict(triggerList)){
                Map<Integer, String> lineMappingToClause = new HashMap<>();
                lineMappingToClause.put(i + 1, ifThenRequirement.getIfThenClause());
                Conflict conflict = new Conflict(lineMappingToClause,"unresolvable");
                unsolvableErrors.add(conflict);
            }
        }

        //if t>10 THEN a, if t<15 THEN !a
        for(int i = 0;i < ifThenRequirements.size() - 1;i++){
            if(ifThenRequirements.get(i).getTime() != null) continue;
            for(int j = i + 1; j < ifThenRequirements.size();j++){
                if(ifThenRequirements.get(j).getTime() != null) continue;
                List<String> triggerList1 = ifThenRequirements.get(i).getTriggerList();
                List<String> triggerList2 = ifThenRequirements.get(j).getTriggerList();
                label:
                for(String action1 : ifThenRequirements.get(i).getActionList()){
                    for(String action2 : ifThenRequirements.get(j).getActionList()){
                        if(action1.split("\\.")[0].equals(action2.split("\\.")[0]) && !action1.split("\\.")[1].equals(action2.split("\\.")[1])){
                            if(!hasConflict(triggerList1, triggerList2)){
                                Map<Integer, String> lineMappingToClause = new HashMap<>();
                                lineMappingToClause.put(i + 1, ifThenRequirements.get(i).getIfThenClause());
                                lineMappingToClause.put(j + 1, ifThenRequirements.get(j).getIfThenClause());
                                Conflict conflict = new Conflict(lineMappingToClause,"unresolvable");
                                String trigger1 = triggerList1.get(0);
                                String trigger2 = triggerList2.get(0);
                                String relation1 = computeRelation(trigger1);
                                String relation2 = computeRelation(trigger2);
                                if(triggerList1.size() == 1 && triggerList2.size() == 1 && !relation1.equals("") && !relation2.equals("") && trigger1.split(relation1)[0].equals(trigger2.split(relation2)[0])){
                                    int value1 = Integer.parseInt(trigger1.split(relation1)[1]);
                                    int value2 = Integer.parseInt(trigger2.split(relation2)[1]);
                                    if(((relation1.equals(">") || relation1.equals(">=")) && (relation2.equals("<") || relation2.equals("<=")) && value1 < value2) ||
                                            ((relation1.equals("<") || relation1.equals("<=")) && (relation2.equals(">") || relation2.equals(">=")) && value1 > value2)){
                                        String group = trigger1.split(relation1)[0] + "//" + action1.split("\\.")[0];
                                        if(!groupMappingToSolvableErrors.containsKey(group)) groupMappingToSolvableErrors.put(group, new ArrayList<>());
                                        conflict.setErrorType("resolvable");
                                        groupMappingToSolvableErrors.get(group).add(conflict);
                                    }
                                }
                                else{
                                    unsolvableErrors.add(conflict);
                                }
                                break label;
                            }
                        }
                    }
                }
            }
        }

        //if a then b, if b then !a, if c then a
        for(int i = 0;i < ifThenRequirements.size() - 1;i++){
            if(ifThenRequirements.get(i).getTime() != null) continue;
            for(int j = i + 1; j < ifThenRequirements.size();j++){
                if(ifThenRequirements.get(j).getTime() != null) continue;
                boolean flag1 = false;//a and !a
                boolean flag2 = true;//b and b
                boolean flag3 = false;//exist if c then a
                List<String> triggerList1 = ifThenRequirements.get(i).getTriggerList();
                List<String> triggerList2 = ifThenRequirements.get(j).getTriggerList();
                List<String> actionList1 = ifThenRequirements.get(i).getActionList();
                List<String> actionList2 = ifThenRequirements.get(j).getActionList();
                String trigger1 = triggerList1.get(0);
                //judge whether the first trigger and the second action has conflict
                if(actionList2.size() == 1 && triggerList1.size() == 1 && computeRelation(trigger1).equals("")){
                    String action2 = actionList2.get(0);
                    if(!trigger1.contains("!") && action2.split("\\.")[0].equals(trigger1.split("\\.")[0])
                    && !action2.split("\\.")[1].equals(trigger1.split("\\.")[1])) flag1 = true;
                    if(trigger1.startsWith("!") && trigger1.equals(action2)) flag1 = true;

                    for(String trigger2 : actionList1){
                        if(!triggerList2.contains(trigger2)) flag2 = false;
                    }
                }
                Map<Integer, String> lineMappingToClause = new HashMap<>();
                lineMappingToClause.put(i + 1, ifThenRequirements.get(i).getIfThenClause());
                lineMappingToClause.put(j + 1, ifThenRequirements.get(j).getIfThenClause());
                for(int k = 0;k < ifThenRequirements.size();k++){
                    IfThenRequirement ifThenRequirement = ifThenRequirements.get(k);
                    List<String> actionList = ifThenRequirement.getActionList();
                    if(actionList.contains(trigger1)){
                        flag3 = true;
                        lineMappingToClause.put(k + 1, ifThenRequirements.get(k).getIfThenClause());
                        break;
                    }
                }
                if(flag1 & flag2 & flag3){
                    Conflict conflict = new Conflict(lineMappingToClause,"chain");
                    unsolvableErrors.add(conflict);
                }
            }
        }
        List<String> resolvable = new ArrayList<>();
        List<String> unresolvable = new ArrayList<>();
        Iterator it = groupMappingToSolvableErrors.keySet().iterator();
        int group = 1;
        while (it.hasNext()){
            StringBuilder sb = new StringBuilder("Group" + group + ":");
            sb.append("\r\n");
            List<Conflict> conflicts = groupMappingToSolvableErrors.get(it.next());
            for(Conflict conflict : conflicts){
                sb.append(conflict.toString());
                sb.append("\r\n");
            }
            resolvable.add(sb.toString());
        }
//        for(int i = 0;i < solvableErrors.size();i++){
//            Conflict conflict = solvableErrors.get(i);
//
//        }
        for(int i = 0;i < unsolvableErrors.size();i++){
            Conflict conflict = unsolvableErrors.get(i);
            unresolvable.add("GROUP " + (i + 1) + " :\n " + conflict.toString());
        }
        if(resolvable.size() == 0) resolvable.add("No Solvable Errors");
        if(unresolvable.size() == 0) unresolvable.add("No Unsolvable Errors");
        result.put("solvableErrors",resolvable);
        result.put("unsolvableErrors",unresolvable);
        return result;
    }

    public JSONObject solve(List<IfThenRequirement> ifThenRequirements) throws IOException {
        JSONObject result = new JSONObject();
        List<String> solved = new ArrayList<>();
        for(IfThenRequirement ifThenRequirement : ifThenRequirements) solved.add(ifThenRequirement.getIfThenClause());
        boolean flag = true;
        do {
            label:
            for(int i = 0;i < ifThenRequirements.size() - 1;i++){
                if(ifThenRequirements.get(i).getTime() != null) continue;
                for(int j = i + 1; j < ifThenRequirements.size();j++){
                    if(ifThenRequirements.get(j).getTime() != null) continue;
                    List<String> triggerList1 = ifThenRequirements.get(i).getTriggerList();
                    List<String> triggerList2 = ifThenRequirements.get(j).getTriggerList();
                    for(String action1 : ifThenRequirements.get(i).getActionList()){
                        for(String action2 : ifThenRequirements.get(j).getActionList()){
                            if(action1.split("\\.")[0].equals(action2.split("\\.")[0]) && !action1.split("\\.")[1].equals(action2.split("\\.")[1])){
                                if(!hasConflict(triggerList1, triggerList2)){
                                    if(triggerList1.size() == 1 && triggerList2.size() == 1){
                                        String trigger1 = triggerList1.get(0);
                                        String trigger2 = triggerList2.get(0);
                                        String relation1 = computeRelation(trigger1);
                                        String relation2 = computeRelation(trigger2);
                                        if(!relation1.equals("") && !relation2.equals("") && trigger1.split(relation1)[0].equals(trigger2.split(relation2)[0])){
                                            if(relation1.equals(relation2)){
                                                return null;
                                            }
                                            String value = trigger1.split(relation1)[1];
                                            String replaceAttributeRange = trigger2.split(relation2)[0] + relation2 + value;
                                            IfThenRequirement replaceRequirement = ifThenRequirements.get(j);
                                            List<String> replaceTriggerList = new ArrayList<>();
                                            replaceTriggerList.add(replaceAttributeRange);
                                            replaceRequirement.setTriggerList(replaceTriggerList);
                                            solved.set(j,replaceRequirement.getIfThenClause());
                                            ifThenRequirements.get(j).setTriggerList(replaceTriggerList);
                                            for(String expectation : ifThenRequirements.get(j).getExpectations()){
                                                if(expectation.contains("IF ") && expectation.contains(" THEN ")){
                                                    String trigger = expectation.substring(3, expectation.indexOf(" THEN "));
                                                    String action = expectation.substring(expectation.indexOf(" THEN ") + 6);
                                                    if(trigger.equals(trigger2)){
                                                        ifThenRequirements.get(j).getExpectations().remove(expectation);
                                                        String newExpectation = "IF " + replaceAttributeRange + " THEN " + action;
                                                        ifThenRequirements.get(j).addExpectation(newExpectation);
                                                    }
                                                }
                                            }
                                            flag = true;
                                            break label;
                                        }
                                    }
                                }
                                else flag = false;
                            }
                        }
                    }
                }
            }
        }while (flag);
        result.put("solved",solved);
        result.put("ifThenRequirements",ifThenRequirements);
        return result;
    }

    private boolean selfConflict(List<String> triggerList) throws IOException {
        Set<String> declares = new HashSet<>();
        Set<String> expressions = new HashSet<>();
        Map<String, Integer> deviceStateMappingToInterger = new HashMap<>();
        deviceStateMappingToInterger.put("MAX",-1);
        for(String trigger : triggerList){
            String relation = computeRelation(trigger);
            if(!relation.equals("")){
                String attribute = trigger.split(relation)[0];
                String value = trigger.split(relation)[1];
                //(declare-const a Int)
                String declare = "(declare-const " + attribute + " Real)";
                //(assert (> a 10))
                String expression = "(assert (" + relation + " " + attribute + " " + value + "))";
                declares.add(declare);
                expressions.add(expression);
            }
            else {
                boolean notFlag = false;
                if(trigger.startsWith("!")){
                    notFlag = true;
                    trigger = trigger.substring(1);
                }
                //ac.coldOn,!ac.coldOn
                String device = trigger.split("\\.")[0];
                String state = trigger.split("\\.")[1];
                String declare = "(declare-const " + device + " Int)";
                int stateValue = deviceStateMappingToInterger.containsKey(trigger) ? deviceStateMappingToInterger.get(trigger) : deviceStateMappingToInterger.get("MAX") + 1;
                String expression = "";
                if(notFlag) expression = "(assert (not(= " + device + " " + stateValue + ")))";
                else expression = "(assert (= " + device + " " + stateValue + "))";
                declares.add(declare);
                expressions.add(expression);
            }
        }
        String smtFilePath = SMTPATH + UUID.randomUUID().toString() + ".smt2";
        createSmtFile(declares,expressions,smtFilePath);
        return !z3Sat(smtFilePath);
    }

//    private boolean hasConflict(List<String> triggerList1, List<String> triggerList2) throws IOException {
//        Set<String> declares = new HashSet<>();
//        Set<String> expressions = new HashSet<>();
//        Map<String, Integer> deviceStateMappingToInterger = new HashMap<>();
//        deviceStateMappingToInterger.put("MAX",-1);
//        List<String> triggerList = new ArrayList<>();
//        triggerList.addAll(triggerList1);
//        triggerList.addAll(triggerList2);
//        for(String trigger : triggerList){
//            String relation = computeRelation(trigger);
//            if(!relation.equals("")){
//                String attribute = trigger.split(relation)[0];
//                String value = trigger.split(relation)[1];
//                //(declare-const a Int)
//                String declare = "(declare-const " + attribute + " Real)";
//                //(assert (> a 10))
//                String expression = "(assert (" + relation + " " + attribute + " " + value + "))";
//                declares.add(declare);
//                expressions.add(expression);
//            }
//            else {
//                boolean notFlag = false;
//                if(trigger.startsWith("!")){
//                    notFlag = true;
//                    trigger = trigger.substring(1);
//                }
//                //ac.coldOn,!ac.coldOn
//                String device = trigger.split("\\.")[0];
//                String state = trigger.split("\\.")[1];
//                String declare = "(declare-const " + device + " Int)";
//                int stateValue = 0;
//                if(deviceStateMappingToInterger.containsKey(trigger)) stateValue = deviceStateMappingToInterger.get(trigger);
//                else{
//                    stateValue = deviceStateMappingToInterger.get("MAX") + 1;
//                    deviceStateMappingToInterger.put(trigger, stateValue);
//                    deviceStateMappingToInterger.put("MAX",stateValue);
//                }
//                String expression = "";
//                if(notFlag) expression = "(assert (not(= " + device + " " + stateValue + ")))";
//                else expression = "(assert (= " + device + " " + stateValue + "))";
//                declares.add(declare);
//                expressions.add(expression);
//            }
//        }
//        String smtFilePath = SMTPATH + UUID.randomUUID().toString() + ".smt2";
//        createSmtFile(declares,expressions,smtFilePath);
//        return !z3Sat(smtFilePath);
//    }

    private Set<String> transformTriggerToZ3Form_Declares(List<String> triggerList){
        Set<String> declares = new HashSet<>();
        if((triggerList.size() == 1)) {
            String trigger = triggerList.get(0);
            String relation = computeRelation(trigger);
            if (!relation.equals("")) {
                String attribute = trigger.split(relation)[0];
                //(declare-const a Int)
                String declare = "(declare-const " + attribute + " Real)";
                declares.add(declare);
            } else {
                String device = trigger.split("\\.")[0];
                String declare = "(declare-const " + device + " Int)";
                declares.add(declare);
            }
        }
        else {
            String trigger1 = triggerList.get(0);
            String trigger2 = triggerList.get(1);
            String relation1 = computeRelation(trigger1);
            String relation2 = computeRelation(trigger2);
            if(!relation1.equals("")){
                String attribute1 = trigger1.split(relation1)[0];
                String declare1 = "(declare-const " + attribute1 + " Real)";
                declares.add(declare1);
            }
            else {
                String device = trigger1.split("\\.")[0];
                String declare = "(declare-const " + device + " Int)";
                declares.add(declare);
            }
            if(!relation2.equals("")){
                String attribute2 = trigger2.split(relation2)[0];
                String declare2 = "(declare-const " + attribute2 + " Real)";
                declares.add(declare2);
            }
            else {
                String device = trigger2.split("\\.")[0];
                String declare = "(declare-const " + device + " Int)";
                declares.add(declare);
            }
        }
        return declares;
    }

    private Set<String> transformTriggerToZ3Form_Expressions(List<String> triggerList, Map<String, Integer> deviceStateMappingToInterger){
        Set<String> expressions = new HashSet<>();
        if((triggerList.size() == 1)){
            String trigger = triggerList.get(0);
            String relation = computeRelation(trigger);
            if(!relation.equals("")){
                String attribute = trigger.split(relation)[0];
                String value = trigger.split(relation)[1];
                //(assert (> a 10))
                String expression = "";
                if(relation.equals("!=")) expression = "(assert (not(" + relation + " " + attribute + " " + value + ")))";
                else expression = "(assert (" + relation + " " + attribute + " " + value + "))";
                expressions.add(expression);
            }
            else {
                boolean notFlag = false;
                if(trigger.startsWith("!")){
                    notFlag = true;
                    trigger = trigger.substring(1);
                }
                //ac.coldOn,!ac.coldOn
                String device = trigger.split("\\.")[0];
                int stateValue = 0;
                if(deviceStateMappingToInterger.containsKey(trigger)) stateValue = deviceStateMappingToInterger.get(trigger);
                else{
                    stateValue = deviceStateMappingToInterger.get("MAX") + 1;
                    deviceStateMappingToInterger.put(trigger, stateValue);
                    deviceStateMappingToInterger.put("MAX",stateValue);
                }
                String expression = "";
                if(notFlag) expression = "(assert (not(= " + device + " " + stateValue + ")))";
                else expression = "(assert (= " + device + " " + stateValue + "))";
                expressions.add(expression);
            }
        }
        else {
            String trigger1 = triggerList.get(0);
            String trigger2 = triggerList.get(1);
            String relation1 = computeRelation(trigger1);
            String relation2 = computeRelation(trigger2);
            String expression1 = "";
            String expression2 = "";
            if(!relation1.equals("")){
                String attribute1 = trigger1.split(relation1)[0];
                String value1 = trigger1.split(relation1)[1];
                if(relation1.equals("!=")) expression1 = "(not(= " +  attribute1 + " " + value1 + "))";
                else expression1 = "(" + relation1 + " " + attribute1 + " " + value1 + ")";
            }
            else {
                boolean notFlag = false;
                if(trigger1.startsWith("!")){
                    notFlag = true;
                    trigger1 = trigger1.substring(1);
                }
                //ac.coldOn,!ac.coldOn
                String device = trigger1.split("\\.")[0];
                int stateValue = 0;
                if(deviceStateMappingToInterger.containsKey(trigger1)) stateValue = deviceStateMappingToInterger.get(trigger1);
                else{
                    stateValue = deviceStateMappingToInterger.get("MAX") + 1;
                    deviceStateMappingToInterger.put(trigger1, stateValue);
                    deviceStateMappingToInterger.put("MAX",stateValue);
                }
                if(notFlag) expression1 = "(not(= " + device + " " + stateValue + "))";
                else expression1 = "(= " + device + " " + stateValue + ")";
            }
            if(!relation2.equals("")){
                String attribute2 = trigger2.split(relation2)[0];
                String value2 = trigger2.split(relation2)[1];
                if(relation2.equals("!=")) expression2 = "(not(= " +  attribute2 + " " + value2 + "))";
                else expression2 = "(" + relation2 + " " + attribute2 + " " + value2 + ")";
            }
            else {
                boolean notFlag = false;
                if(trigger2.startsWith("!")){
                    notFlag = true;
                    trigger2 = trigger2.substring(1);
                }
                //ac.coldOn,!ac.coldOn
                String device = trigger2.split("\\.")[0];
                int stateValue = 0;
                if(deviceStateMappingToInterger.containsKey(trigger2)) stateValue = deviceStateMappingToInterger.get(trigger2);
                else{
                    stateValue = deviceStateMappingToInterger.get("MAX") + 1;
                    deviceStateMappingToInterger.put(trigger2, stateValue);
                    deviceStateMappingToInterger.put("MAX",stateValue);
                }
                if(notFlag) expression2 = "(not(= " + device + " " + stateValue + "))";
                else expression2 = "(= " + device + " " + stateValue + ")";
            }

            String expression = "(assert (and " + expression1 + expression2 + "))";
            expressions.add(expression);
        }
        return expressions;
    }

    private boolean hasConflict(List<String> triggerList1, List<String> triggerList2) throws IOException {
        Set<String> declares = new HashSet<>();
        Set<String> expressions = new HashSet<>();
        Map<String, Integer> deviceStateMappingToInterger = new HashMap<>();
        deviceStateMappingToInterger.put("MAX",-1);
        declares.addAll(transformTriggerToZ3Form_Declares(triggerList1));
        declares.addAll(transformTriggerToZ3Form_Declares(triggerList2));
        expressions.addAll(transformTriggerToZ3Form_Expressions(triggerList1, deviceStateMappingToInterger));
        expressions.addAll(transformTriggerToZ3Form_Expressions(triggerList2, deviceStateMappingToInterger));
        String smtFilePath = SMTPATH + UUID.randomUUID().toString() + ".smt2";
        createSmtFile(declares,expressions,smtFilePath);
        return !z3Sat(smtFilePath);
    }

    private void createSmtFile(Set<String> declares, Set<String> expressions, String smtFilePath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(smtFilePath));
        for(String declare : declares){
            bw.write(declare);
            bw.newLine();
            bw.flush();
        }
        for(String expression : expressions){
            bw.write(expression);
            bw.newLine();
            bw.flush();
        }
        bw.write("(check-sat)");
        bw.flush();
        bw.close();
    }

    private boolean z3Sat(String smtFilePath) throws IOException {
        String command = "/usr/local/bin/z3 " + smtFilePath;
        String temp = "";
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedInputStream bis = new BufferedInputStream(
                    process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) {
                temp = temp + line + " ";
            }
            process.waitFor();
            if (process.exitValue() != 0) {
                return true;
            }
            process.destroy();
            bis.close();
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(temp.contains("unsat")) return false;
        else return true;
    }

//    public List<String> consistencyCheck2(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
//        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
//        Map<String, String> effectMap = computeEffectMap();
//        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
//        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);
//        List<String> errors = new ArrayList<>();
//        Map<String, List<List<String>>> entityMappingToTriggers = new HashMap<>();
//        for(int i = 0;i < ifThenRequirements.size();i++){
//            IfThenRequirement requirement = ifThenRequirements.get(i);
//            List<String> actions = requirement.getActionList();
//            for(int j = 0;j < actions.size();j++){
//                String action = actions.get(j);
//                String left = action.split("\\.")[0];
//                String right = action.split("\\.")[1];
//                if(eo.getEvents().contains(right)) actions.set(j, left + "." + eo.getEventMappingToState().get(right));
//                if(eo.getActionMappingToState().containsKey(right)) actions.set(j, left + "." + eo.getStateMappingToAction().get(right));
//            }
//            requirement.setActionList(actions);
//            ifThenRequirements.set(i, requirement);
//        }
//
//        for(IfThenRequirement requirement : ifThenRequirements){
//            for(String state : requirement.getActionList()){
//                String entity = state.split("\\.")[0];
//                List<List<String>> triggerLists = entityMappingToTriggers.containsKey(entity) ? entityMappingToTriggers.get(entity) : new ArrayList<>();
//                triggerLists.add(requirement.getTriggerList());
//                entityMappingToTriggers.put(entity, triggerLists);
//            }
//        }
//
//        Iterator it = entityMappingToTriggers.keySet().iterator();
//        while (it.hasNext()){
//            boolean flag = false;
//            String key = (String) it.next();
//            List<List<String>> triggerLists = entityMappingToTriggers.get(key);
//            if(triggerLists.size() > 1){
//                for(int i = 0;i < triggerLists.size() - 1;i++){
//                    List<String> triggerList1 = triggerLists.get(i);
//                    List<String> triggerList2 = triggerLists.get(i + 1);
//                    for(String trigger1 : triggerList1){
//                        for(String trigger2 : triggerList2){
//                            if(isNotTriggerConflict(trigger1, trigger2))flag = true;
//                        }
//                    }
//                }
//                if(!flag) errors.add("triggers on " + key + " have conflicts");
//            }
//        }
//        return errors;
//    }

//    public JSONObject z3Check(String smtFilePath , String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
//        JSONObject result = new JSONObject();
//        String temp = "";
//        List<String> formulas = getZ3Fomulas(requirementTexts, ontologyPath, index);
//        BufferedWriter bw = new BufferedWriter(new FileWriter(smtFilePath));
//        for(String formula : formulas){
//            bw.write(formula);
//            bw.newLine();
//            bw.flush();
//        }
//        bw.flush();
//        bw.close();
//        String command = "z3 " + smtFilePath;
//        try {
//            Process process = Runtime.getRuntime().exec(command);
//            BufferedInputStream bis = new BufferedInputStream(
//                    process.getInputStream());
//            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
//            String line;
//            while ((line = br.readLine()) != null) {
//                temp = temp + line + " ";
//            }
//            process.waitFor();
//            if (process.exitValue() != 0) {
//                result.put("result","failure");
//            }
//            process.destroy();
//            bis.close();
//            br.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        result.put("result","success");
//        if(temp.contains("unsat")) result.put("sat", "unsat");
//        else result.put("sat", "sat");
//        return result;
//    }

//    private boolean isNotTriggerConflict(String trigger1, String trigger2){
//        String relation1 = computeRelation(trigger1);
//        String relation2 = computeRelation(trigger2);
//        if(!relation1.equals("") && !relation2.equals("")){
//            String attribute1 = trigger1.split(relation1)[0];
//            String attribute2 = trigger2.split(relation2)[0];
//            String value1 = trigger1.split(relation1)[1];
//            String value2 = trigger2.split(relation2)[1];
//            if(attribute1.equals(attribute2)){
//                if(isNotRangeOverlapping(relation1, relation2, value1, value2)) return true;
//            }
//        }
//        return false;
//    }

//    private boolean isNotRangeOverlapping(String relation1, String relation2, String value1, String value2){
//        relation1 = simplifyRelation(relation1);
//        relation2 = simplifyRelation(relation2);
//        if(relation1.equals(">") && relation2.equals(">")) return false;
//        else if(relation1.equals("<") && relation2.equals("<")) return false;
//        else if(relation1.equals(">") && relation2.equals("<")){
//            return (Double.parseDouble(value1) >= Double.parseDouble(value2));
//        }
//        else if(relation1.equals("<") && relation2.equals(">")) return isNotRangeOverlapping(relation2, relation1, value2, value1);
//        else if(relation1.equals(">") && relation2.equals("=")){
//            return (Double.parseDouble(value1) > Double.parseDouble(value2));
//        }
//        else if(relation1.equals("<") && relation2.equals("=")){
//            return (Double.parseDouble(value1) < Double.parseDouble(value2));
//        }
//        else if(relation1.equals("=") && (relation2.equals("<") || relation2.equals(">"))) return isNotRangeOverlapping(relation2, relation1, value2, value1);
//        else if((relation1.equals("=") && relation2.equals("!=")) || (relation1.equals("!=") && relation2.equals("="))) return !value1.equals(value2);
//        return false;
//    }

//    private String simplifyRelation(String relation){
//        if(relation.equals(">=")) return ">";
//        else if(relation.equals("<=")) return "<";
//        else return relation;
//    }

//    private List<String> getZ3Fomulas(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
//        List<String> result = new ArrayList<>();
//        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
//        Map<String, String> effectMap = computeEffectMap();
//
//        List<Requirement> requirements = initRequirements(Arrays.asList(requirementTexts.split("//")));
//
//        Map<String, Integer> stateMappingToInteger = new HashMap<>();
//        Map<String, Z3Fomula> deviceMappingToFormula = new HashMap<>();
//        Iterator it = eo.getDeviceMappingToStates().keySet().iterator();
//        while (it.hasNext()){
//            String device = (String) it.next();
//            List<String> deviceStates = eo.getDeviceMappingToStates().get(device);
//            for(int i = 0;i < deviceStates.size();i++) stateMappingToInteger.put(device + "." + deviceStates.get(i),i);
//        }
//
//        for(Requirement requirement : requirements){
//             if(requirement instanceof AlwaysNeverRequirement){
//                AlwaysNeverRequirement alwaysNeverRequirement = (AlwaysNeverRequirement) requirement;
//                if(alwaysNeverRequirement.getAttribute() == null){
//                    String relation = alwaysNeverRequirement.getRelation();
//                    String deviceEventOrState = alwaysNeverRequirement.getDeviceEventOrState();
//                    String device = deviceEventOrState.split("\\.")[0];
//                    String eventOrState = deviceEventOrState.split("\\.")[1];
//                    String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
//                    int stateIndex = stateMappingToInteger.get(device + "." + state);
//                    if(!deviceMappingToFormula.containsKey(device)) deviceMappingToFormula.put(device, new Z3Fomula(device));
//                    Z3Fomula z3Fomula = deviceMappingToFormula.get(device);
//                    z3Fomula.getAttributes().add(device);
//                    if(relation.equals("ALWAYS")){
//                        String expressions = "(= " + device + " " + stateIndex + ")";
//                        z3Fomula.getExpressions().add(expressions);
//                    }
//                    else {
//                        String expressions = "(not (= " + device + " " + stateIndex + "))";
//                        z3Fomula.getExpressions().add(expressions);
//                    }
//                }
//            }
//        }
//
//        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, effectMap, ontologyPath).get(index);
//        for(IfThenRequirement ifThenRequirement : ifThenRequirements){
//            if(ifThenRequirement.getTime() == null){
//                for(String action : ifThenRequirement.getActionList()){
//                    String actionDevice = action.split("\\.")[0];
//                    String eventOrState = action.split("\\.")[1];
//                    String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
//                    if(!deviceMappingToFormula.containsKey(actionDevice)) deviceMappingToFormula.put(actionDevice, new Z3Fomula(actionDevice));
//                    Z3Fomula z3Fomula = deviceMappingToFormula.get(actionDevice);
//                    int stateIndex = stateMappingToInteger.get(actionDevice + "." +state);
//                    //(=> (< Air.humidity 30) (= Window 0))
//                    //(=> (= Blind 0) (= Window 0))
//                    if(ifThenRequirement.getTriggerList().size() == 1){
//                        String trigger = ifThenRequirement.getTriggerList().get(0);
//                        String relation = computeRelation(trigger);
//                        if(!relation.equals("")){
//                            String attribute = trigger.split(relation)[0];
//                            String value = trigger.split(relation)[1];
//                            String expression = "";
//                            if(relation.equals("!=")) expression = "(=> (not( = " + attribute + " " + value + ")) " + "(= " + actionDevice + " " + stateIndex + "))";
//                            else expression = "(=> (" + relation + " " + attribute + " " + value + ") " + "(= " + actionDevice + " " + stateIndex + "))";
//                            z3Fomula.getAttributes().add(attribute);
//                            z3Fomula.getExpressions().add(expression);
//                        }
//                        else {
//                            String triggerDevice = trigger.split("\\.")[0];
//                            int triggerDeviceStateIndex = stateMappingToInteger.get(trigger);
//                            String expressions = "(=> (= " + triggerDevice + " " + triggerDeviceStateIndex + ") " + "(= " + actionDevice + " " + stateIndex + "))";
//                            z3Fomula.getAttributes().add(triggerDevice);
//                            z3Fomula.getExpressions().add(expressions);
//                        }
//                    }
//                    //(=> (and (< Air.humidity 30) (> Air.humidity 40)) (= Window 0))
//                    //(=> (and (< Air.humidity 30) (= Blind 0)) (= Window 0))
//                    else {
//                        StringBuilder expressions = new StringBuilder("(=> (and ");
//                        for(String trigger : ifThenRequirement.getTriggerList()){
//                            String relation = computeRelation(trigger);
//                            if(!relation.equals("")){
//                                String attribute = trigger.split(relation)[0];
//                                String value = trigger.split(relation)[1];
//                                String temp = "";
//                                if(relation.equals("!=")) temp = "(not ( =" + attribute + " " + value + "))";
//                                else temp = "(" + relation + " " + attribute + " " + value + ")";
//                                expressions.append(temp);
//                                z3Fomula.getAttributes().add(attribute);
//                            }
//                            else {
//                                String triggerDevice = trigger.split("\\.")[0];
//                                int triggerDeviceStateIndex = stateMappingToInteger.get(trigger);
//                                String temp = "(= " + triggerDevice + " " + triggerDeviceStateIndex + ")";
//                                expressions.append(temp);
//                                z3Fomula.getAttributes().add(triggerDevice);
//                            }
//                        }
//                        expressions.append(") " + "(= " + actionDevice + " " + stateIndex + "))");
//                        z3Fomula.getExpressions().add(expressions.toString());
//                    }
//                }
//            }
//        }
//        it = deviceMappingToFormula.keySet().iterator();
//        while (it.hasNext()){
//            String device = (String) it.next();
//            Z3Fomula z3Fomula = deviceMappingToFormula.get(device);
//            result.add(z3Fomula.toString());
//        }
//        result.add("(check-sat)");
//        return result;
//    }



    public static void main(String[] args) throws IOException, DocumentException {
        CheckService checkService = new CheckService();
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> effectMap = computeEffectMap();
//        String re = "IF window.wopen THEN projector.pon//IF projector.pon THEN window.wclosed//IF ac.aoff THEN window.wopen";
//        String re = "IF light.brightness<35 THEN bulb.bon//IF light.brightness>30 THEN bulb.boff//IF air.temperature>20 THEN ac.coldon//IF air.temperature<25 THEN ac.hoton//IF air.temperature<10 THEN ac.aoff";
        String re = "IF light.brightness<35 THEN bulb.bon//IF light.brightness>30 THEN bulb.boff//IF air.temperature<25 THEN ac.hoton//IF air.temperature>20 THEN ac.coldon";
        System.out.println(checkService.check(re, ontologyPath, 0));
    }

}
