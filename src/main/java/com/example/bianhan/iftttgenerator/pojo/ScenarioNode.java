package com.example.bianhan.iftttgenerator.pojo;

import java.util.Objects;

public class ScenarioNode {
    private String content;
    private String relavantPD;
    private int layer;
    private int type;//0:behaviour 1:sensor 2:expect
    private int chainIndex;

    public ScenarioNode(String content, int layer, int type, String relavantPD, int chainIndex) {
        this.content = content;
        this.layer = layer;
        this.type = type;
        this.relavantPD = relavantPD;
        this.chainIndex = chainIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRelavantPD() {
        return relavantPD;
    }

    public void setRelavantPD(String relavantPD) {
        this.relavantPD = relavantPD;
    }

    public int getChainIndex() {
        return chainIndex;
    }

    public void setChainIndex(int chainIndex) {
        this.chainIndex = chainIndex;
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
