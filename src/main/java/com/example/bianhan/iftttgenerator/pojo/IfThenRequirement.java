package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class IfThenRequirement{
    private List<String> triggerList;
    private List<String> actionList;
    private String time;

    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time) {
        this.triggerList = triggerList;
        this.actionList = actionList;
        this.time = time;
    }

    @Override
    public String toString() {
        return "IfThenRequirement{" +
                "triggerList=" + triggerList +
                ", actionList=" + actionList +
                ", time='" + time + '\'' +
                '}';
    }
}
