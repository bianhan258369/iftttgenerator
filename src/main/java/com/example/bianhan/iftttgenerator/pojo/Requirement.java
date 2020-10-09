package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Requirement {
    private String requirement;

    public Requirement(){
        this.requirement = null;
    }

    public Requirement(String requirement) {
        this.requirement = requirement;
    }
}
