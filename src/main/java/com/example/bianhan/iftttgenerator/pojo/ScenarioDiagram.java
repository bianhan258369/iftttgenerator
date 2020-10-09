package com.example.bianhan.iftttgenerator.pojo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.modifyDot;

public class ScenarioDiagram {
    private List<ScenarioNode> scenarioNodes;

    public ScenarioDiagram(List<ScenarioNode> scenarioNodes) {
        this.scenarioNodes = scenarioNodes;
    }

    public List<ScenarioNode> getScenarioNodes() {
        return scenarioNodes;
    }

    public void setScenarioNodes(List<ScenarioNode> scenarioNodes) {
        this.scenarioNodes = scenarioNodes;
    }

    public void toDotFile(EnvironmentOntology eo, String scFilePath) throws IOException {
        List<String> strs = new ArrayList<>();
        Set<String> problemDomains = new HashSet<>();
        strs.add("digraph scenario{");
        
        Map<Integer, Integer> positionX = new HashMap<>();
        int maxLayer = 0;
        for(int i = 0;i < scenarioNodes.size();i++){
            maxLayer = scenarioNodes.get(i).getLayer() > maxLayer ? scenarioNodes.get(i).getLayer() : maxLayer;
        }
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node = scenarioNodes.get(i);
            if(!positionX.containsKey(node.getLayer())) positionX.put(node.getLayer(), 200);
            else positionX.put(node.getLayer(), positionX.get(node.getLayer()) + 200);
            int y = (maxLayer - node.getLayer() + 1) * 150;
            String pos = "\"" + positionX.get(node.getLayer()) + "," + y + "\"";
            if(node.getType() == 0){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=skyblue,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                
            }
            else if(node.getType() == 1){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=cadetblue,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                
            }
            else if(node.getType() == 2){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=orange,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                
            }
            else {
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=pink,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
            }
        }
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node1 = scenarioNodes.get(i);
            for(int j = i + 1;j < scenarioNodes.size();j++){
                ScenarioNode node2 = scenarioNodes.get(j);
                if(node1.getType() == 2 && node2.getType() == 2 && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                        && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)
                        && (node1.getIfThenIndex() == node2.getIfThenIndex() || node1.getIfThenIndex() == -1 || node2.getIfThenIndex() == -1)){
                    if(node1.getLayer() > node2.getLayer()){
                        strs.add("N" + j + "->" + "N" + i + "[style = dashed,color=orange]");
                        
                    }
                    else{
                        strs.add("N" + i + "->" + "N" + j + "[style = dashed,color=orange]");
                        
                    }
                }
                else if(node1.getType() == 2 && node2.getType() == 3 && node2.getLayer() - node1.getLayer() == 1){
                    strs.add("N" + i + "->" + "N" + j + "[color=pink]");
                }
                else if(node1.getType() == 3 && node2.getType() == 2 && node1.getLayer() - node2.getLayer() == 1){
                    strs.add("N" + j + "->" + "N" + i + "[color=pink]");
                }
                else if(node1.getType() != 2 && node1.getType() != 3 && node2.getType() != 2 && node2.getType() != 3 && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                        && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)
                        && (node1.getIfThenIndex() == node2.getIfThenIndex() || node1.getIfThenIndex() == -1 || node2.getIfThenIndex() == -1)){
                    if(node1.getLayer() > node2.getLayer()){
                        strs.add("N" + j + "->" + "N" + i + "[color=skyblue]");
                        
                    }
                    else{
                        strs.add("N" + i + "->" + "N" + j + "[color=skyblue]");
                        
                    }
                }
                else if((node1.getType() == 0 || node1.getType() == 1) && node2.getType() == 2){
                    if(node1.getContent().equals(node2.getContent())){
                        strs.add("N" + i + "->" + "N" + j + "[dir=none,color=green]");
                        
                    }
                    else {
                        String eventOrState = node2.getContent();
                        String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                        if(eo.getStateMappingToAction().containsKey(state)&&eo.getStateMappingToAction().get(state).equals(node1.getContent())){
                            strs.add("N" + i + "->" + "N" + j + "[color=red]");
                            
                        }
                    }
                }
                else if((node1.getType() == 0 || node1.getType() == 1) && node1.getType() == 2){
                    if(node2.getContent().equals(node1.getContent())){
                        strs.add("N" + i + "->" + "N" + j + "[dir=none,color=green]");
                        
                    }
                    else {
                        String eventOrState = node1.getContent();
                        String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                        if(eo.getStateMappingToAction().get(state).equals(node2.getContent())){
                            strs.add("N" + i + "->" + "N" + j + "[color=red]");
                            
                        }
                    }
                }
            }
        }
        strs.add("}");
        BufferedWriter bw = new BufferedWriter(new FileWriter(scFilePath));
        for(String str : strs){
            bw.write(str);
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }
}
