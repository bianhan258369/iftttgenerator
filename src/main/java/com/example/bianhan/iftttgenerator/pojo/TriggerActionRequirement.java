package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class TriggerActionRequirement extends Requirement {
    String trigger;
    String action;
    String time;

    public TriggerActionRequirement(String requirement, String trigger, String action, String time) {
        super(requirement);
        this.trigger = trigger;
        this.action = action;
        this.time = time;
    }

    public TriggerActionRequirement(String requirement, String room, String trigger, String action, String time) {
        super(requirement, room);
        this.trigger = trigger;
        this.action = action;
        this.time = time;
    }
}
