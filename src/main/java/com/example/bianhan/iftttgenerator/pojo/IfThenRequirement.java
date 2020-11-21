package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.*;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.strListEquals;

@Data
public class IfThenRequirement{

    private List<String> triggerList;
    private List<String> actionList;
    private String time;
    private String expectation;
    private Set<String> expectations;


    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time, String expectation) {
        this.triggerList = triggerList;
        this.actionList = actionList;
        this.time = time;
        this.expectation = expectation;
        this.expectations = new HashSet<>();
        this.expectations.add(expectation);
    }

    public void addExpectation(String expectation){
        this.expectations.add(expectation);
        this.updateExpectation();
    }

    private void updateExpectation(){
        this.expectation = "";
        for(String temp : expectations){
            this.expectation = this.expectation + temp + "//";
        }
        this.expectation = this.expectation.substring(0, this.expectation.length() - 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfThenRequirement that = (IfThenRequirement) o;
        return strListEquals(this.triggerList, that.getTriggerList()) && strListEquals(this.actionList, that.getActionList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(triggerList, actionList, time);
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
        return expectations + " : IF " + trigger + " THEN " + action;
//        else return expectation + " : IF " + trigger + " FOR " + time + " THEN " + action;
    }
}
