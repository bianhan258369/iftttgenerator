package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class AlwaysNeverRequirement extends Requirement {
    private String alwaysNever;//"ALWAYS" or "NEVER"
    private String deviceEventOrState;
    private String attribute;
    private String relation;//"BELOW" or "ABOVE"
    private int value;

    public AlwaysNeverRequirement(String requirement, String alwaysNever, String attribute, String relation, int value) {
        super(requirement);
        this.alwaysNever = alwaysNever;
        this.deviceEventOrState = null;
        this.attribute = attribute;
        this.relation = relation;
        this.value = value;
    }

    public AlwaysNeverRequirement(String requirement, String alwaysNever, String deviceEventOrState) {
        super(requirement);
        this.alwaysNever = alwaysNever;
        this.deviceEventOrState = deviceEventOrState;
        this.attribute = null;
        this.relation = null;
    }
}
