package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("onnetService")
public class OnenetService {
    public String toOnenet(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException{
        StringBuilder sb = new StringBuilder("");
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);

        Map<String, String> intendMap = computeMap(PathConfiguration.ONENETMAPPATH, "intendMap",eo);
        Map<String, List<String>> triggerMap = computeMap(PathConfiguration.ONENETMAPPATH, "triggerMap",eo);
        Map<String, List<String>> actionMap = computeMap(PathConfiguration.ONENETMAPPATH, "actionMap",eo);

        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), intendMap, ontologyPath).get(index);

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


    public void runSimulation(String onenetRules) throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new FileReader("com/test/temp.java"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("com/test/SmartConferRoom.java"));
        boolean flag = false;
        String line = "";
        while ((line = br.readLine()) != null){
            bw.write(line);
            bw.newLine();
            bw.flush();
            if(line.trim().equals("//-----------") && !flag){
                flag = true;
                List<String> rules = Arrays.asList(onenetRules.split("\r\n"));
                for(String rule : rules){
                    bw.write(rule);
                    bw.newLine();
                    bw.flush();
                }
            }
        }
        bw.close();
        String cmd1 = System.getProperty("os.name").toLowerCase().startsWith("win") ?
                "javac -encoding utf-8 -cp .;* com\\test\\SmartConferRoom.java" :
                "javac -encoding utf-8 -cp .:* com/test/SmartConferRoom.java";
        String cmd2 = System.getProperty("os.name").toLowerCase().startsWith("win") ?
                "java -cp .;* com\\test\\SmartConferRoom" :
                "java -cp .:* com/test/SmartConferRoom";
        Process p1 = Runtime.getRuntime().exec(cmd1);
        p1.waitFor();
        p1.destroy();
        System.out.println(cmd1);
        Process p2 = Runtime.getRuntime().exec(cmd2);
        // Any error message?
        Thread errorGobbler
                = new Thread(new StreamGobbler(p2.getErrorStream(), System.err));

        // Any output?
        Thread outputGobbler
                = new Thread(new StreamGobbler(p2.getInputStream(), System.out));

        errorGobbler.start();
        outputGobbler.start();

        // Any error?
        int exitVal = p2.waitFor();
        errorGobbler.join();   // Handle condition where the
        outputGobbler.join();  // process ends before the threads finish
    }

    class StreamGobbler implements Runnable {
        private final InputStream is;
        private final PrintStream os;

        StreamGobbler(InputStream is, PrintStream os) {
            this.is = is;
            this.os = os;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    os.print((char) c);
            } catch (IOException x) {
                // Handle error
            }
        }
    }

    public static void main(String[] args) throws IOException, DocumentException, InterruptedException {
        String re = "IF air.temperature>30 THEN allow ventilating the room//IF person.distanceFromMc<0.5 THEN mc.mon";
        OnenetService onenetService = new OnenetService();
        onenetService.runSimulation(onenetService.toOnenet(re, "ontology_SmartConferenceRoom.xml",0));
    }
}
