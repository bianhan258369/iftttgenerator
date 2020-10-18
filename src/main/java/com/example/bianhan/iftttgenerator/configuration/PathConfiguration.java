package com.example.bianhan.iftttgenerator.configuration;

public class PathConfiguration {
    public static final String DROOLSMAPPATH = "drools_map.txt";
    public static final String ONENETMAPPATH = "onenet_map.txt";
    public static final String IFTTTMAPPATH = "ifttt_map.txt";
    public static final String SCDPATH = System.getProperty("os.name").toLowerCase().startsWith("win") ?
            "E:/JavaProject/iftttgenerator/images/" :"/Users/bianhan/Desktop/project/iftttgenerator/images/";
    public static final String ontologyRootPath = System.getProperty("os.name").toLowerCase().startsWith("win") ?
            "E:/JavaProject/iftttgenerator/ontologyFiles/" :"/Users/bianhan/Desktop/project/iftttgenerator/ontologyFiles/";
    public static final String SMTPath = System.getProperty("os.name").toLowerCase().startsWith("win") ?
            "E:/JavaProject/iftttgenerator/smts/" :"/Users/bianhan/Desktop/project/iftttgenerator/smts/";
//    public static final String ontologyRootPath = "/root/ifttt/ontologyFiles/";
}
