package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class ScenarioNode {
    private String content;
    private String relavantPD;
    private int layer;
    private int type; //0:behaviour-trigger 1:remote domain 2:behaviour-action 3:expect-trigger 4:expect-state 5:intend -1:requirement content
    private int chainIndex;
    private int ifThenIndex;
    private List<Integer> ifThenIndexes;

    public ScenarioNode(String content, int layer, int type, String relavantPD, int chainIndex, int ifThenIndexIndex, List<Integer> ifThenIndexes) {
        this.content = content;
        this.relavantPD = relavantPD;
        this.layer = layer;
        this.type = type;
        this.chainIndex = chainIndex;
        this.ifThenIndex = ifThenIndex;
        this.ifThenIndexes = ifThenIndexes;
    }

    public ScenarioNode(String content, int layer, int type, String relavantPD, int chainIndex, int ifThenIndexIndex) {
        this.content = content;
        this.layer = layer;
        this.type = type;
        this.relavantPD = relavantPD;
        this.chainIndex = chainIndex;
        this.ifThenIndex = ifThenIndexIndex;
        this.ifThenIndexes = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScenarioNode that = (ScenarioNode) o;
        return layer == that.layer &&
                type == that.type &&
                Objects.equals(content, that.content) &&
                Objects.equals(relavantPD, that.relavantPD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, relavantPD, layer, type);
    }
}
