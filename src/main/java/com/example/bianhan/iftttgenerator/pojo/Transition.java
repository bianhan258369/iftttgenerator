package com.example.bianhan.iftttgenerator.pojo;

public class Transition {
    private String from;
    private String to;
    private String event;

    public Transition(String from, String to, String event) {
        this.from = from;
        this.to = to;
        this.event = event;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return from + "--" + event + "->" + to;
    }
}
