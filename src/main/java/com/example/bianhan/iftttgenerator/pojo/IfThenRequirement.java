package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IfThenRequirement{

    private List<String> triggerList;
    private List<String> actionList;
    private String time;
    private String intend;
    private String originalRequirement;

//    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time) {
//        this.triggerList = triggerList;
//        this.actionList = actionList;
//        this.time = time;
//    }

//    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time, String intend) {
//        this.triggerList = triggerList;
//        this.actionList = actionList;
//        this.time = time;
//        this.intend = intend;
//    }


    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time, String intend, String originalRequirement) {
        this.triggerList = triggerList;
        this.actionList = actionList;
        this.time = time;
        this.intend = intend;
        this.originalRequirement = originalRequirement;
    }

    @Override
    public String toString() {
        return intend + " : IF " + triggerList + " FOR " + time + " THEN " + actionList;
    }
}
