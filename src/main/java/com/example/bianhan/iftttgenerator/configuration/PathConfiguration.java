package com.example.bianhan.iftttgenerator.configuration;

public class PathConfiguration {
    public static final String DROOLSMAPPATH = "drools_map.txt";
    public static final String ONENETMAPPATH = "onenet_map.txt";
    public static final String IFTTTMAPPATH = "ifttt_map.txt";
    public static final String DEVICEREGITRATIONTABLEPATH = "DeviceRegitrationTable.txt";
    private String scdPath = "";
    private String ontologyRootPath = "";
    private String smtPath = "";
    private String smartConferenceRoomOntologyPath = "";
    private String smartHomeOntologyPath = "";

    public static final String SCDPATH = System.getProperty("os.name").toLowerCase().startsWith("win") ? "E:/JavaProject/iftttgenerator/images/" :
            (System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/images/" : "/root/dsigs/images/") ;
    public static final String ONTOLOGYROOTPATH = System.getProperty("os.name").toLowerCase().startsWith("win") ? "E:/JavaProject/iftttgenerator/ontologyFiles/" :
            (System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontologyFiles/" : "/root/dsigs/ontologyFiles/") ;
    public static final String SMTPATH = System.getProperty("os.name").toLowerCase().startsWith("win") ? "E:/JavaProject/iftttgenerator/smts/" :
            (System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/smts/" : "/root/dsigs/smts/") ;
    public static final String SMARTCONFERENCEROOMONTOLOGYPATH = System.getProperty("os.name").toLowerCase().startsWith("win") ? "E:/JavaProject/iftttgenerator/ontology_SmartConferenceRoom.xml" :
            (System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontology_SmartConferenceRoom.xml" : "/root/dsigs/ontology_SmartConferenceRoom.xml");
    public static final String SMARTHOMEONTOLOGYPATH = System.getProperty("os.name").toLowerCase().startsWith("win") ? "E:/JavaProject/iftttgenerator/ontology_SmartConferenceRoom.xml" :
            (System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontology_SmartConferenceRoom.xml" : "/root/dsigs/ontology_SmartConferenceRoom.xml");
    public static final String PYTHONCMDPATH = System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/autotap/iot-autotap/autotapmc/cmd/cmd.py" : "/root/autotap/iot-autotap/autotapmc/cmd/cmd.py";
    public static final String TEMPLATEPATH = System.getProperty("os.name").toLowerCase().startsWith("mac") ? "/Users/bianhan/Desktop/project/autotap/iot-autotap/autotapmc/channels/template/" : "/root/autotap/iot-autotap/autotapmc/channels/template/";

}
