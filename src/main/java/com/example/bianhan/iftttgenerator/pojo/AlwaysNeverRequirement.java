package com.example.bianhan.iftttgenerator.pojo;

public class AlwaysNeverRequirement {
    private String subject;
    private String alwaysNever;

    public AlwaysNeverRequirement(String subject, String alwaysNever) {
        this.subject = subject;
        this.alwaysNever = alwaysNever;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAlwaysNever() {
        return alwaysNever;
    }

    public void setAlwaysNever(String alwaysNever) {
        this.alwaysNever = alwaysNever;
    }
}
