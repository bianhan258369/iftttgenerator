package com.example.bianhan.iftttgenerator.pojo.problemdiagram;

import lombok.Data;

@Data
public class Phenomenon {
    private String name;
    private String state;
    private Rect from;
    private Rect to;
    private boolean constraining = false;
    private Oval requirement;
    private int biaohao;

    public Phenomenon(String name, String state, Rect from, Rect to, int biaohao) {
        this.name = name;
        this.state = state;
        this.from = from;
        this.to = to;
        this.biaohao =biaohao;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Phenomenon){
            Phenomenon temp = (Phenomenon)obj;
            return this.getBiaohao() == temp.getBiaohao() && this.getState().equals(temp.getState()) && this.getName().equals(temp.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31;
    }

    @Override
    public String toString() {
        return "Phenomenon{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", constraining=" + constraining +
                ", requirement=" + requirement +
                ", biaohao=" + biaohao +
                '}';
    }
}
