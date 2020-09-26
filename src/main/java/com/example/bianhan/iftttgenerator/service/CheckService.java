package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.util.Configuration;
import org.dom4j.DocumentException;
import org.omg.CORBA.MARSHAL;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("checkService")
public class CheckService {
    public List<String> consistencyCheck(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(Configuration.DROOLSMAPPATH, "intendMap", eo);
        List<String> requirements = computeRequirements(requirementTexts, ontologyPath);
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath);
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

    public static void main(String[] args) throws IOException, DocumentException {
        CheckService checkService = new CheckService();
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap("onenet_map.txt", "intendMap", eo);
        String re = "IF air.temperature>30 AND air.humidity<40 THEN window.wopen//IF air.temperature<40 THEN window.wclosed";
        System.out.println(checkService.consistencyCheck(re, ontologyPath));
    }

}
