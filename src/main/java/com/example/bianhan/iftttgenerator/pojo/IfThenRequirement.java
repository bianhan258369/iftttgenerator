package com.example.bianhan.iftttgenerator.pojo;

import java.util.List;

public class IfThenRequirement{
    private List<String> triggerList;
    private List<String> actionList;
    private String time;

    public IfThenRequirement(List<String> triggerList, List<String> actionList, String time) {
        this.triggerList = triggerList;
        this.actionList = actionList;
        this.time = time;
    }

    public List<String> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<String> triggerList) {
        this.triggerList = triggerList;
    }

    public List<String> getActionList() {
        return actionList;
    }

    public void setActionList(List<String> actionList) {
        this.actionList = actionList;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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
