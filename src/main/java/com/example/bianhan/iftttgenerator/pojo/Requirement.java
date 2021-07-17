package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Requirement {
    private String requirement;
    private String room;

    public Requirement(){
        this.requirement = null;
        this.room = null;
    }

    public Requirement(String requirement) {
        this.requirement = requirement;
        this.room = "home";
    }

    public Requirement(String requirement, String room) {
        this.requirement = requirement;
        this.room = room;
    }
}
