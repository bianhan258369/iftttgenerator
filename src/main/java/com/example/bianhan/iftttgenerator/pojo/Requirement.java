package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Requirement {
    private String description;
    private List<String>  meanings;
}
