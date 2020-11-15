package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IfThenRequirement{

    private List<String> triggerList;
    private List<String> actionList;
    private String time;
    private String expectation;


    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time, String expectation) {
        this.triggerList = triggerList;
        this.actionList = actionList;
        this.time = time;
        this.expectation = expectation;
    }

    @Override
    public String toString(){
        String trigger = "";
        String action = "";
        for(int i = 0;i < triggerList.size();i++){
            trigger = trigger + triggerList.get(i);
            if(i != triggerList.size() - 1) trigger = trigger + " AND ";
        }
        for(int i = 0;i < actionList.size();i++){
            action = action + actionList.get(i);
            if(i != actionList.size() - 1) action = action + ",";
        }
        return "IF " + trigger + " THEN " + action;
//        else return expectation + " : IF " + trigger + " FOR " + time + " THEN " + action;
    }
}
