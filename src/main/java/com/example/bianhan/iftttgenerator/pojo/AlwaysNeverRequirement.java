package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class AlwaysNeverRequirement extends Requirement {
    private String alwaysNever;//"ALWAYS" or "NEVER"
    private String eventOrState;
    private String attribute;
    private String relation;//"BELOW" or "ABOVE"
    private double value;

    public AlwaysNeverRequirement(String requirement, String alwaysNever, String attribute, String relation, double value) {
        super(requirement);
        this.alwaysNever = alwaysNever;
        this.eventOrState = null;
        this.attribute = attribute;
        this.relation = relation;
        this.value = value;
    }

    public AlwaysNeverRequirement(String requirement, String alwaysNever, String eventOrState) {
        super(requirement);
        this.alwaysNever = alwaysNever;
        this.eventOrState = eventOrState;
        this.attribute = null;
        this.relation = null;
    }
}
