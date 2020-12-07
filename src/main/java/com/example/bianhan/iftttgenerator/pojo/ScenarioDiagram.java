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

    public void toDotFile_ServiceRequirement(EnvironmentOntology eo, String scFilePath) throws IOException {
        List<String> strs = new ArrayList<>();
        strs.add("digraph scenario{");
        strs.add("rankdir=RL;");

        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node = scenarioNodes.get(i);
            strs.add("N" + i + "[shape = Mrecord,color=pink,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
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

    public void toDotFile_DeviceRequirement(EnvironmentOntology eo, String scFilePath) throws IOException {
        List<String> strs = new ArrayList<>();
        strs.add("digraph scenario{");
        strs.add("rankdir=RL;");
        Map<Integer, Integer> positionX = new HashMap<>();
        int maxLayer = 0;
        int devicNumber = 0;
        int deviceX = 200;
        Set<ScenarioNode> deviceNodes = new HashSet<>();
        for(int i = 0;i < scenarioNodes.size();i++){
            maxLayer = scenarioNodes.get(i).getLayer() > maxLayer ? scenarioNodes.get(i).getLayer() : maxLayer;
            if(scenarioNodes.get(i).getType() == 6) devicNumber++;
            if(scenarioNodes.get(i).getLayer() == 2) deviceX += 200;
        }

        Set<Integer> nodeNumbers = new HashSet<>();
        int deviceIndex = 1;
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node = scenarioNodes.get(i);
            int deviceY = (maxLayer * 150) / devicNumber * deviceIndex;
            if(!positionX.containsKey(node.getLayer())) positionX.put(node.getLayer(), 200);
            else positionX.put(node.getLayer(), positionX.get(node.getLayer()) + 200);
            int y = node.getLayer() * 150;
            String pos = "\"" + y + "," + positionX.get(node.getLayer()) + "\"";
            String devicePos = "\"" + deviceY + "," + deviceX + "\"";
            if(isTitle(node)){
                strs.add("title" + "[shape = Mrecord,pos=\"500,0\"" + ",label = \"" + modifyDot(node.getContent())  + "\",shape=plaintext, fontsize=16]");
                nodeNumbers.add(i);
            }
            else if(isBehaviour(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=steelblue1,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=invis]");
                nodeNumbers.add(i);
            }
            else if(isSensor(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=limegreen,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=invis]");
                nodeNumbers.add(i);
            }
            else if(isExpect(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=orange,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                nodeNumbers.add(i);
            }
            else if(isExpectation(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=pink,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                nodeNumbers.add(i);
            }
            else if(isDevice(node)){
                if(!deviceNodes.contains(node)){
                    strs.add("N" + i + "[shape = doubleoctagon,pos=" + devicePos + ",color=grey,label = \"" + modifyDot(node.getContent())  + "\",style=filled]");
                    deviceIndex++;
                    deviceNodes.add(node);
                    nodeNumbers.add(i);
                }
            }
        }
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node1 = scenarioNodes.get(i);
            for(int j = i + 1;j < scenarioNodes.size();j++){
                ScenarioNode node2 = scenarioNodes.get(j);
                if(nodeNumbers.contains(i) && nodeNumbers.contains(j)){
                    if(node1.getType() == 6){
                        if(node2.getRelavantPD().equals(node1.getContent())){
                            if(isExpect(node2))strs.add("N" + j + "->" + "N" + i + "[style = dashed,color=grey,penwidth=5]");
                            else strs.add("N" + j + "->" + "N" + i + "[style = dashed,color=grey,style=invis,penwidth=5]");
                        }
                    }
                    else if(node2.getType() == 6){
                        if(node1.getRelavantPD().equals(node2.getContent())){
                            if(isExpect(node1))strs.add("N" + i + "->" + "N" + j + "[style = dashed,color=grey,penwidth=5]");
                            else strs.add("N" + i + "->" + "N" + j + "[style = dashed,color=grey,style=invis,penwidth=5]");
                        }
                    }
                    else if(isExpect(node1) && isExpect(node2) && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                            && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)
                            && (node1.getIfThenIndex() == node2.getIfThenIndex() || node1.getIfThenIndex() == -1 || node2.getIfThenIndex() == -1)){
                        if(node1.getLayer() > node2.getLayer()){
                            strs.add("N" + j + "->" + "N" + i + "[style = dashed,color=orange,penwidth=5]");

                        }
                        else{
                            strs.add("N" + i + "->" + "N" + j + "[style = dashed,color=orange,penwidth=5]");

                        }
                    }
                    else if(node1.getType() == 4 && node2.getType() == 5 && node1.getTargetExpectationNode()!=null && node1.getTargetExpectationNode().contains(node2.getContent())){
                        strs.add("N" + i + "->" + "N" + j + "[color=pink,penwidth=3]");
                    }
                    else if(node1.getType() == 5 && node2.getType() == 4 && node2.getTargetExpectationNode()!=null && node2.getTargetExpectationNode().contains(node1.getContent())){
                        strs.add("N" + j + "->" + "N" + i + "[color=pink,penwidth=3]");
                    }
                    else if(!isExpect(node1) && !isExpectation(node1) && !isExpect(node2) && !isExpectation(node2) && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                            && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)
                            && (node1.getIfThenIndex() == node2.getIfThenIndex() || node1.getIfThenIndex() == -1 || node2.getIfThenIndex() == -1)){
                        if(node1.getLayer() > node2.getLayer()){
                            strs.add("N" + j + "->" + "N" + i + "[color=steelblue1,style=invis,penwidth=3]");

                        }
                        else{
                            strs.add("N" + i + "->" + "N" + j + "[color=steelblue1,style=invis,penwidth=3]");

                        }
                    }
                    else if((isBehaviour(node1) || isSensor(node1)) && isExpect(node2)){
                        if(node1.getContent().equals(node2.getContent()) && node1.getRelavantPD().equals(node2.getRelavantPD()) && node1.getIfThenIndex() == node2.getIfThenIndex()){
                            strs.add("N" + i + "->" + "N" + j + "[dir=none,color=green3,style=invis,penwidth=3]");

                        }
                        else {
                            String eventOrState = node2.getContent();
                            String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                            if(node1.getIfThenIndex() == node2.getIfThenIndex() && eo.getStateMappingToAction().containsKey(state)&&eo.getStateMappingToAction().get(state).equals(node1.getContent())){
                                strs.add("N" + i + "->" + "N" + j + "[color=red,style=invis,penwidth=3]");

                            }
                        }
                    }
                    else if((isBehaviour(node2) || isSensor(node2)) && isExpect(node1)){
                        if(node2.getContent().equals(node1.getContent()) && node2.getRelavantPD().equals(node1.getRelavantPD()) && node1.getIfThenIndex() == node2.getIfThenIndex()){
                            strs.add("N" + j + "->" + "N" + i + "[dir=none,color=green3,style=invis,penwidth=3]");
                        }
                        else {
                            String eventOrState = node1.getContent();
                            String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                            if(node1.getIfThenIndex() == node2.getIfThenIndex() && eo.getStateMappingToAction().containsKey(state)&&eo.getStateMappingToAction().get(state).equals(node2.getContent())){
                                strs.add("N" + j + "->" + "N" + i + "[color=red,style=invis,penwidth=3]");
                            }
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

    public void toDotFile_SystemBehaviour(EnvironmentOntology eo, String scFilePath) throws IOException {
        List<String> strs = new ArrayList<>();
        strs.add("digraph scenario{");
        strs.add("rankdir=RL;");
        Map<Integer, Integer> positionX = new HashMap<>();
        int maxLayer = 0;
        int devicNumber = 0;
        int deviceX = 200;
        Set<ScenarioNode> deviceNodes = new HashSet<>();
        for(int i = 0;i < scenarioNodes.size();i++){
            maxLayer = scenarioNodes.get(i).getLayer() > maxLayer ? scenarioNodes.get(i).getLayer() : maxLayer;
            if(scenarioNodes.get(i).getType() == 6) devicNumber++;
            if(scenarioNodes.get(i).getLayer() == 2) deviceX += 200;
        }

        Set<Integer> nodeNumbers = new HashSet<>();
        int deviceIndex = 1;
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node = scenarioNodes.get(i);
            int deviceY = (maxLayer * 150) / devicNumber * deviceIndex;
            if(!positionX.containsKey(node.getLayer())) positionX.put(node.getLayer(), 200);
            else positionX.put(node.getLayer(), positionX.get(node.getLayer()) + 200);
            int y = node.getLayer() * 150;
            String pos = "\"" + y + "," + positionX.get(node.getLayer()) + "\"";
            String devicePos = "\"" + deviceY + "," + deviceX + "\"";
            if(isTitle(node)){
                strs.add("title" + "[shape = Mrecord,pos=\"500,0\"" + ",label = \"" + modifyDot(node.getContent())  + "\",shape=plaintext, fontsize=16]");
                nodeNumbers.add(i);
            }
            else if(isBehaviour(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=steelblue1,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                nodeNumbers.add(i);
            }
            else if(isSensor(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=limegreen,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                nodeNumbers.add(i);
            }
            else if(isExpect(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=orange,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                nodeNumbers.add(i);
            }
            else if(isExpectation(node)){
                strs.add("N" + i + "[shape = Mrecord,pos=" + pos + ",color=pink,label = \"" + node.getRelavantPD() + ":\\n" + modifyDot(node.getContent())  + "\",style=filled]");
                nodeNumbers.add(i);
            }
            else if(isDevice(node)){
                if(!deviceNodes.contains(node)){
                    strs.add("N" + i + "[shape = doubleoctagon,pos=" + devicePos + ",color=grey,label = \"" + modifyDot(node.getContent())  + "\",style=filled]");
                    deviceIndex++;
                    deviceNodes.add(node);
                    nodeNumbers.add(i);
                }
            }
        }
        for(int i = 0;i < scenarioNodes.size();i++){
            ScenarioNode node1 = scenarioNodes.get(i);
            for(int j = i + 1;j < scenarioNodes.size();j++){
                ScenarioNode node2 = scenarioNodes.get(j);
                if(nodeNumbers.contains(i) && nodeNumbers.contains(j)){
                    if(node1.getType() == 6){
                        if(node2.getRelavantPD().equals(node1.getContent())){
                            strs.add("N" + j + "->" + "N" + i + "[style = dashed,color=grey,penwidth=5]");
                        }
                    }
                    else if(node2.getType() == 6){
                        if(node1.getRelavantPD().equals(node2.getContent())){
                            strs.add("N" + i + "->" + "N" + j + "[style = dashed,color=grey,penwidth=5]");
                        }
                    }
                    else if(isExpect(node1) && isExpect(node2) && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                            && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)
                            && (node1.getIfThenIndex() == node2.getIfThenIndex() || node1.getIfThenIndex() == -1 || node2.getIfThenIndex() == -1)){
                        if(node1.getLayer() > node2.getLayer()){
                            strs.add("N" + j + "->" + "N" + i + "[style = dashed,color=orange,penwidth=5]");

                        }
                        else{
                            strs.add("N" + i + "->" + "N" + j + "[style = dashed,color=orange,penwidth=5]");

                        }
                    }
                    else if(node1.getType() == 4 && node2.getType() == 5 && node1.getTargetExpectationNode()!=null && node1.getTargetExpectationNode().contains(node2.getContent())){
                        strs.add("N" + i + "->" + "N" + j + "[color=pink,penwidth=3]");
                    }
                    else if(node1.getType() == 5 && node2.getType() == 4 && node2.getTargetExpectationNode()!=null && node2.getTargetExpectationNode().contains(node1.getContent())){
                        strs.add("N" + j + "->" + "N" + i + "[color=pink,penwidth=3]");
                    }
                    else if(!isExpect(node1) && !isExpectation(node1) && !isExpect(node2) && !isExpectation(node2) && Math.abs(node1.getLayer() - node2.getLayer()) == 1
                            && (node1.getChainIndex() == node2.getChainIndex() || node1.getChainIndex() == -1 || node2.getChainIndex() == -1)
                            && (node1.getIfThenIndex() == node2.getIfThenIndex() || node1.getIfThenIndex() == -1 || node2.getIfThenIndex() == -1)){
                        if(node1.getLayer() > node2.getLayer()){
                            strs.add("N" + j + "->" + "N" + i + "[color=steelblue1,penwidth=3]");

                        }
                        else{
                            strs.add("N" + i + "->" + "N" + j + "[color=steelblue1,penwidth=3]");

                        }
                    }
                    else if((isBehaviour(node1) || isSensor(node1)) && isExpect(node2)){
                        if(node1.getContent().equals(node2.getContent()) && node1.getRelavantPD().equals(node2.getRelavantPD()) && node1.getIfThenIndex() == node2.getIfThenIndex()){
                            strs.add("N" + i + "->" + "N" + j + "[dir=none,color=green3,penwidth=3]");

                        }
                        else {
                            String eventOrState = node2.getContent();
                            String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                            if(node1.getIfThenIndex() == node2.getIfThenIndex() && eo.getStateMappingToAction().containsKey(state)&&eo.getStateMappingToAction().get(state).equals(node1.getContent())){
                                strs.add("N" + i + "->" + "N" + j + "[color=red,penwidth=3]");

                            }
                        }
                    }
                    else if((isBehaviour(node2) || isSensor(node2)) && isExpect(node1)){
                        if(node2.getContent().equals(node1.getContent()) && node2.getRelavantPD().equals(node1.getRelavantPD()) && node1.getIfThenIndex() == node2.getIfThenIndex()){
                            strs.add("N" + j + "->" + "N" + i + "[dir=none,color=green3,penwidth=3]");
                        }
                        else {
                            String eventOrState = node1.getContent();
                            String state = eo.getEvents().contains(eventOrState) ? eo.getEventMappingToState().get(eventOrState) : eventOrState;
                            if(node1.getIfThenIndex() == node2.getIfThenIndex() && eo.getStateMappingToAction().containsKey(state)&&eo.getStateMappingToAction().get(state).equals(node2.getContent())){
                                strs.add("N" + j + "->" + "N" + i + "[color=red,penwidth=3]");
                            }
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

    private boolean isBehaviour(ScenarioNode node){
        return node.getType() == 0 || node.getType() == 2;
    }

    private boolean isExpect(ScenarioNode node){
        return node.getType() == 3 || node.getType() == 4;
    }

    private boolean isSensor(ScenarioNode node){
        return node.getType() == 1;
    }

    private boolean isExpectation(ScenarioNode node){
        return node.getType() == 5;
    }

    private boolean isTitle(ScenarioNode node){
        return node.getType() == -1;
    }

    private boolean isDevice(ScenarioNode node){
        return node.getType() == 6;
    }
}
