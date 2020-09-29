package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("iftttService")
public class IFTTTService {
    public String toIFTTT(String requirementTexts, String ontologyPath) throws IOException, DocumentException {
        StringBuilder sb = new StringBuilder("");
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);

        Map<String, String> intendMap = computeMap(PathConfiguration.IFTTTMAPPATH, "intendMap", eo);
        Map<String, List<String>> triggerMap = computeMap(PathConfiguration.IFTTTMAPPATH, "triggerMap", eo);
        Map<String, List<String>> actionMap = computeMap(PathConfiguration.IFTTTMAPPATH, "actionMap", eo);
        Map<String, String> paraTypeMap = computeMap(PathConfiguration.IFTTTMAPPATH, "paraTypeMap", eo);

        List<String> requirements = computeRequirements(requirementTexts, ontologyPath);
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath);

        for (IfThenRequirement requirement : ifThenRequirements) {
            if (requirement.getTime() == null) {
                String triggers = "";
                for (int i = 0;i < requirement.getTriggerList().size();i++) {
                    triggers = triggers + requirement.getTriggerList().get(i);
                    if(i != requirement.getTriggerList().size() - 1) triggers = triggers + "&&";
                }
                String action = requirement.getActionList().get(0);
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
        System.out.println(iftttService.toIFTTT(requirementTexts, ontologyPath));
    }
}
