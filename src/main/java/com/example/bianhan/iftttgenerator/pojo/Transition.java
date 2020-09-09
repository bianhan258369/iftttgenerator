package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

@Data
public class Transition {
    private String from;
    private String to;
    private String event;

    public Transition(String from, String to, String event) {
        this.from = from;
        this.to = to;
        this.event = event;
    }

    @Override
    public String toString() {
        return from + "--" + event + "->" + to;
    }
}
