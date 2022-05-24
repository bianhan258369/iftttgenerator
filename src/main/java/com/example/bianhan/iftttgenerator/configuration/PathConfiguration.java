package com.example.bianhan.iftttgenerator.configuration;

public class PathConfiguration {
    static String os = System.getProperty("os.name").toLowerCase();

    public static final String DROOLSMAPPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/drools_map.txt" : "/root/dsigs/drools_map.txt";
    public static final String ONENETMAPPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/onenet_map.txt" : "/root/dsigs/onenet_map.txt";
    public static final String IFTTTMAPPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ifttt_map.txt" : "/root/dsigs/ifttt_map.txt";
    public static final String DEVICEREGITRATIONTABLEPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/DeviceRegitrationTable.txt" : "/root/dsigs/DeviceRegitrationTable.txt";

    public static final String SCDPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/images/" : "/root/dsigs/images/";
    public static final String ONTOLOGYROOTPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontologyFiles/" : "/root/dsigs/ontologyFiles/";
    public static final String SMTPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/smts/" : "/root/dsigs/smts/";
    public static final String SMARTCONFERENCEROOMONTOLOGYPATH =os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontology_SmartConferenceRoom.xml" : "/root/dsigs/ontology_SmartConferenceRoom.xml";
    public static final String SMARTHOMEONTOLOGYPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/ontology_home.xml" : "/root/dsigs/ontology_home.xml";
    public static final String PYTHONCMDPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/autotap/iot-autotap/autotapmc/cmd/cmd.py" : "/root/autotap/iot-autotap/autotapmc/cmd/cmd.py";
    public static final String TEMPLATEPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/autotap/iot-autotap/autotapmc/channels/template/" : "/root/autotap/iot-autotap/autotapmc/channels/template/";
    public static final String DEVICEREGISTRYTABLE = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/device_registry.txt" : "/root/dsigs/device_registry.txt";
    public static final String PERSONALDEVICETABLEPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/personal_device.txt" : "/root/dsigs/personal_device.txt";
    public static final String AUTOMATIONIDS = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/automation_ids.txt" : "/root/dsigs/automation_ids.txt";
    public static final String LOCATIONMAP = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/locationmap.txt" : "/root/dsigs/locationmap.txt";
    public static final String JAVAMAPPINGTOPYTHONPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/JavaMappingToPython.txt" : "/root/dsigs/JavaMappingToPython.txt";
    public static final String AUTOTAPINPUTPATH = os.startsWith("mac") ? "/Users/bianhan/Desktop/project/iftttgenerator/autotapInput.txt" : "/root/dsigs/autotapInput.txt";


    //    public static final String HATOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI2MTIyNjExYmFlZmU0NzExOTcyZmIyNzA4MDIwMTc4NCIsImlhdCI6MTYyMjE2NzM2MywiZXhwIjoxOTM3NTI3MzYzfQ.UnqYoPOE1jY9672CcLkIoIhpdBnaLcaiMhhNYWTAsEs";
    public static final String HATOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIzOTk0MzNlMmZiYjQ0M2ZmYTE0YmMzNzM2OTc4MWM5ZCIsImlhdCI6MTYzMTU5OTQ1NywiZXhwIjoxOTQ2OTU5NDU3fQ.MZGvokybbRPTzV3NLOG60SCUA6Dg5kL_3lRNnmTJnS8";
    public static final String haIP = "192.168.31.81:8123";
}
