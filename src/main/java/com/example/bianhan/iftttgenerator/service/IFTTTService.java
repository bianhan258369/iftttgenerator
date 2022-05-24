package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("iftttService")
public class IFTTTService {
    public String toIFTTT(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
//        StringBuilder sb = new StringBuilder("\r\n");
        StringBuilder sb = new StringBuilder("");
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);

        Map<String, String> effectMap = computeEffectMap();
        Map<String, List<String>> actionMap = computeMap(PathConfiguration.IFTTTMAPPATH, "actionMap", eo);
        Map<String, List<String>> triggerMap = computeMap(PathConfiguration.IFTTTMAPPATH, "triggerMap", eo);

        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);
//        List<IfThenRequirement> temp = new ArrayList<>();
//        temp.add(ifThenRequirements.get(0));
//        temp.add(ifThenRequirements.get(5));
//        temp.add(ifThenRequirements.get(1));
//        temp.add(ifThenRequirements.get(2));
//        temp.add(ifThenRequirements.get(3));
//        temp.add(ifThenRequirements.get(4));
//        ifThenRequirements = temp;
        for (IfThenRequirement requirement : ifThenRequirements) {
            String triggers = "";
            for (int i = 0;i < requirement.getTriggerList().size();i++) {
                String tempTrigger = "";
                String trigger = requirement.getTriggerList().get(i);
                String relation = computeRelation(trigger);
                if(relation.equals("")){
                    tempTrigger = triggerMap.get(trigger).get(0);
                }
                else{
                    String attribute = trigger.split(relation)[0];
                    String value = trigger.split(relation)[1];
                    tempTrigger = triggerMap.get(attribute).get(0) + relation + value;
                }
                triggers = triggers + tempTrigger;
                if(i != requirement.getTriggerList().size() - 1) triggers = triggers + "&&";
            }
            for(String action : requirement.getActionList()){
                String left = action.split("\\.")[0];
                String right = action.split("\\.")[1];
                if (eo.getEvents().contains(right)) {
                    right = eo.getEventMappingToState().get(right);
                }
                action = "M." + eo.getStateMappingToAction().get(right);
                action = actionMap.get(action).get(0);
                sb.append("IF " + triggers + " THEN " + action);
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        IFTTTService iftttService = new IFTTTService();
        String requirementTexts = "IF air.temperature<30&&air.temperature>20 THEN ac.coldOn//IF air.temperature>10 THEN ac.hotOn";
        System.out.println(iftttService.toIFTTT(requirementTexts, ontologyPath,0));
    }
}
