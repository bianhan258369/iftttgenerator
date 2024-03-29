package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class ScenarioNode {
    private String content;
    private String relavantPD;
    private int layer;
    private int type; //0:behaviour-trigger 1:remote domain 2:behaviour-action 3:expect-trigger 4:expect-state 5:intend -1:requirement content 6:controlled entity
    private int chainIndex;
    private int ifThenIndex;
    private List<Integer> ifThenIndexes;
    private List<String> targetExpectationNode;

    public ScenarioNode(String content, int layer, int type, String relavantPD, int chainIndex, int ifThenIndex, List<Integer> ifThenIndexes, List<String> targetExpectationNode) {
        this.content = content;
        this.relavantPD = relavantPD;
        this.layer = layer;
        this.type = type;
        this.chainIndex = chainIndex;
        this.ifThenIndex = ifThenIndex;
        this.ifThenIndexes = ifThenIndexes;
        this.targetExpectationNode = targetExpectationNode;
    }

    public ScenarioNode(String content, int layer, int type, String relavantPD, int chainIndex, int ifThenIndex, List<Integer> ifThenIndexes) {
        this.content = content;
        this.relavantPD = relavantPD;
        this.layer = layer;
        this.type = type;
        this.chainIndex = chainIndex;
        this.ifThenIndex = ifThenIndex;
        this.ifThenIndexes = ifThenIndexes;
    }

    public ScenarioNode(String content, int layer, int type, String relavantPD, int chainIndex, int ifThenIndex) {
        this.content = content;
        this.layer = layer;
        this.type = type;
        this.relavantPD = relavantPD;
        this.chainIndex = chainIndex;
        this.ifThenIndex = ifThenIndex;
        this.ifThenIndexes = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScenarioNode that = (ScenarioNode) o;
        return Objects.equals(content, that.content) &&
                Objects.equals(relavantPD, that.relavantPD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, relavantPD);
    }

    @Override
    public String toString() {
        return "ScenarioNode{" +
                "content='" + content + '\'' +
                ", relavantPD='" + relavantPD + '\'' +
                ", layer=" + layer +
                ", type=" + type +
                '}';
    }

    public static void main(String[] args) {
        ScenarioNode scenarioNode = new ScenarioNode("aaa",1,1,"bbb",2,3);
        System.out.println(scenarioNode.getIfThenIndexes());
    }
}
