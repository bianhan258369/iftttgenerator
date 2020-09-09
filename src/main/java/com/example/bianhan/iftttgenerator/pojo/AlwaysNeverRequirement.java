package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class AlwaysNeverRequirement {
    private String subject;
    private String alwaysNever;

    public AlwaysNeverRequirement(String subject, String alwaysNever) {
        this.subject = subject;
        this.alwaysNever = alwaysNever;
    }
}
