package com.example.bianhan.iftttgenerator.configuration;

public class PathConfiguration {
    public static final String DROOLSMAPPATH = "drools_map.txt";
    public static final String ONENETMAPPATH = "onenet_map.txt";
    public static final String IFTTTMAPPATH = "ifttt_map.txt";
    public static final String DEVICEREGITRATIONTABLEPATH = "DeviceRegitrationTable.txt";
    static String os = System.getProperty("os.name").toLowerCase();

    public static final String SCDPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/images/" : "/home/pi/iftttgenerator/images/";
    public static final String ONTOLOGYROOTPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontologyFiles/" : "/home/pi/iftttgenerator/ontologyFiles/";
    public static final String SMTPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/smts/" : "/home/pi/iftttgenerator/smts/";
    public static final String SMARTCONFERENCEROOMONTOLOGYPATH =os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontology_SmartConferenceRoom.xml" : "/home/pi/iftttgenerator/ontology_SmartConferenceRoom.xml";
    public static final String SMARTHOMEONTOLOGYPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontology_home.xml" : "/home/pi/iftttgenerator/ontology_home.xml";
    public static final String PYTHONCMDPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/autotap/iot-autotap/autotapmc/cmd/cmd.py" : "/home/pi/autotap/iot-autotap/autotapmc/cmd/cmd.py";
    public static final String TEMPLATEPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/autotap/iot-autotap/autotapmc/channels/template/" : "/home/pi/autotap/iot-autotap/autotapmc/channels/template/";
    public static final String DEVICEREGISTRYTABLE = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/device_registry.txt" : "/home/pi/iftttgenerator/device_registry.txt";
    public static final String PERSONALDEVICETABLEPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/personal_device.txt" : "/home/pi/iftttgenerator/personal_device.txt";
//    public static final String HATOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI2MTIyNjExYmFlZmU0NzExOTcyZmIyNzA4MDIwMTc4NCIsImlhdCI6MTYyMjE2NzM2MywiZXhwIjoxOTM3NTI3MzYzfQ.UnqYoPOE1jY9672CcLkIoIhpdBnaLcaiMhhNYWTAsEs";
    public static final String HATOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIzOTk0MzNlMmZiYjQ0M2ZmYTE0YmMzNzM2OTc4MWM5ZCIsImlhdCI6MTYzMTU5OTQ1NywiZXhwIjoxOTQ2OTU5NDU3fQ.MZGvokybbRPTzV3NLOG60SCUA6Dg5kL_3lRNnmTJnS8";
}
