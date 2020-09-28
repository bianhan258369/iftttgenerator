package com.example.bianhan.iftttgenerator.pojo;

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

    public String toDot(EnvironmentOntology eo){
        StringBuilder sb = new StringBuilder("");
        Set<String> problemDomains = new HashSet<>();
        sb.append("digraph scenario{");
        sb.append("\r\n");
        Map<Integer, Integer> positionX = new HashMap<>();
        int maxLayer = 0;
        for(int i = 0;i < scenarioNodes.size();i++){
            maxLayer = scenarioNodes.get(i).getLayer() > maxLayer ? scenarioNodes.get(i).getLayer() : maxLayer;
        }
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node = scenarioNodes.get(i);
            if(!positionX.containsKey(node.getLayer())) positionX.put(node.getLayer(), 200);
            else positionX.put(node.getLayer(), positionX.get(node.getLayer()) + 200);
            int y = (maxLayer - node.getLayer() + 1) * 200;
            String pos = "\"" + positionX.get(node.getLayer()) + "," + y + "\"";
            if(node.getType() == 0){
                sb.append("N" + i + "[shape = Mrecord,pos=" + pos + ",color=blue,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\"]");
                sb.append("\r\n");
            }
            else if(node.getType() == 1){
                sb.append("N" + i + "[shape = Mrecord,pos=" + pos + ",color=green,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\"]");
                sb.append("\r\n");
            }
            else{
                sb.append("N" + i + "[shape = Mrecord,pos=" + pos + ",color=orange,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=dashed]");
                sb.append("\r\n");
            }
//            if(!problemDomains.contains(node.getRelavantPD())){
//                sb.append("PD" + (problemDomains.size() + 1) + "[shape = record, label = \"" + node.getRelavantPD() + "\"]");
//                sb.append("\r\n");
//                sb.append("N" + i + "->" + "PD" + (problemDomains.size() + 1));
//                sb.append("\r\n");
//            }
//            problemDomains.add(node.getRelavantPD());

        }
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node1 = scenarioNodes.get(i);
            for(int j = i + 1;j < scenarioNodes.size();j++){
                ScenarioNode node2 = scenarioNodes.get(j);
                if(node1.getType() == 2 && node2.getType() == 2 && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                        && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)){
                    if(node1.getLayer() > node2.getLayer()){
                        sb.append("N" + j + "->" + "N" + i + "[style = dashed,color=orange]");
                        sb.append("\r\n");
                    }
                    else{
                        sb.append("N" + i + "->" + "N" + j + "[style = dashed,color=orange]");
                        sb.append("\r\n");
                    }
                }
                else if(node1.getType() != 2 && node2.getType() != 2 && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                        && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)){
                    if(node1.getLayer() > node2.getLayer()){
                        sb.append("N" + j + "->" + "N" + i + "[color=blue]");
                        sb.append("\r\n");
                    }
                    else{
                        sb.append("N" + i + "->" + "N" + j + "[color=blue]");
                        sb.append("\r\n");
                    }
                }
                else if((node1.getType() == 0 || node1.getType() == 1) && node2.getType() == 2){
                    if(node1.getContent().equals(node2.getContent())){
                        sb.append("N" + i + "->" + "N" + j + "[dir=none,color=green]");
                        sb.append("\r\n");
                    }
                    else {
                        String eventOrState = node2.getContent();
                        String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                        if(eo.getStateMappingToAction().containsKey(state)&&eo.getStateMappingToAction().get(state).equals(node1.getContent())){
                            sb.append("N" + i + "->" + "N" + j + "[color=red]");
                            sb.append("\r\n");
                        }
                    }
                }
                else if((node1.getType() == 0 || node1.getType() == 1) && node1.getType() == 2){
                    if(node2.getContent().equals(node1.getContent())){
                        sb.append("N" + i + "->" + "N" + j + "[dir=none,color=green]");
                        sb.append("\r\n");
                    }
                    else {
                        String eventOrState = node1.getContent();
                        String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                        if(eo.getStateMappingToAction().get(state).equals(node2.getContent())){
                            sb.append("N" + i + "->" + "N" + j + "[color=red]");
                            sb.append("\r\n");
                        }
                    }
                }
            }
        }
        sb.append("}");
        sb.append("\r\n");
        return sb.toString();
    }
}
