package com.example.bianhan.iftttgenerator.controller;

import com.example.bianhan.iftttgenerator.pojo.IfThenRequirement;
import com.example.bianhan.iftttgenerator.service.*;
import com.example.bianhan.iftttgenerator.util.FileUtil;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.*;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class GenerateController {
    @Autowired
    private DroolsService droolsService;
    @Autowired
    private ProblemFrameService pfService;
    @Autowired
    private CheckService checkService;
    @Autowired
    private OnenetService onenetService;
    @Autowired
    private IFTTTService iftttService;

    @CrossOrigin
    @RequestMapping("/upload")
    @ResponseBody
    public JSONObject uploadOntology(@RequestParam("uploadedFile") MultipartFile file) throws IOException {
        JSONObject result = new JSONObject();
        File folder = new File(ONTOLOGYROOTPATH);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        String oldName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf(".")).replaceAll("-","_");
        file.transferTo(new File(ONTOLOGYROOTPATH,newName));
        result.put("filePath", (ONTOLOGYROOTPATH + newName));
        result.put("result", "success");
        return result;
    }

    @CrossOrigin
    @RequestMapping("/chooseScenario")
    @ResponseBody
    public JSONObject chooseScenario(@RequestParam String scenario) throws IOException {
        JSONObject result = new JSONObject();
        String path = "";
        if(scenario.equals("SmartHome")) path = SMARTHOMEONTOLOGYPATH;
        else if(scenario.equals("SmartConferenceRoom")) path = SMARTCONFERENCEROOMONTOLOGYPATH;
        result.put("path",path);
        result.put("result", "success");
        return result;
    }

    @CrossOrigin
    @RequestMapping("/transform2FunctionalRequirements")
    @ResponseBody
    public JSONObject transformToFunctionalRequirements(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException, InterruptedException {
        JSONObject result = toFunctionalRequirements(requirementTexts, ontologyPath, index);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/transform2SystemBehaviour")
    @ResponseBody
    public List<String> transformToSystemBehaviour(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
        List<String> systemBehaviours = Arrays.asList(toSystemBehaviours(requirementTexts, ontologyPath, index).split("\n"));
        return systemBehaviours;
    }

    @CrossOrigin
    @RequestMapping("/transform2Drools")
    @ResponseBody
    public List<String> transformToDrools(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
        List<String> drools = Arrays.asList(droolsService.toDrools(requirementTexts, ontologyPath, index).split("\n"));
        return drools;
    }

    @CrossOrigin
    @RequestMapping("/transform2Onenet")
    @ResponseBody
    public List<String> transformToOnenet(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
        List<String> onenet = Arrays.asList(onenetService.toOnenet(requirementTexts, ontologyPath, index).split("\n"));
        return onenet;
    }

    @CrossOrigin
    @RequestMapping("/transform2IFTTT")
    @ResponseBody
    public List<String> transformToIFTTT(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
        List<String> ifttt = Arrays.asList(iftttService.toIFTTT(requirementTexts, ontologyPath, index).split("\n"));
        return ifttt;
    }


    @CrossOrigin
    @RequestMapping("/complement")
    @ResponseBody
    public JSONObject complementRequirements(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException, InterruptedException {
        JSONObject result = new JSONObject();
        StringBuilder sb = new StringBuilder("");
        List<String> complementedRequirements = (List<String>) computeComplementedRequirements(requirementTexts, ontologyPath, index);
        for(int i = 0;i < complementedRequirements.size();i++){
            String requirement = complementedRequirements.get(i);
            sb.append(requirement);
            if(i != complementedRequirements.size() - 1) sb.append("\n");
        }
        result.put("result", "success");
        result.put("complementedRequirements", sb.toString());
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getPD")
    @ResponseBody
    public JSONObject getPD(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
        JSONObject result = pfService.getElementsOfPD(requirementTexts, ontologyPath, index);
        result.put("result","success");
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getSrSCD")
    @ResponseBody
    public JSONObject getSrAndSbSCD(@RequestParam String requirementTexts, @RequestParam String ontologyPath) throws IOException, DocumentException, InterruptedException {
        String folderPath = SCDPATH + UUID.randomUUID().toString() + "/";
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        JSONObject result = pfService.getSrPng(folderPath, requirementTexts, ontologyPath);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getDrSCD")
    @ResponseBody
    public JSONObject getDrSCD(@RequestBody JSONObject json) throws IOException, DocumentException, InterruptedException {
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        List<List<String>> triggerLists = (List<List<String>>) json.get("triggerLists");
        List<List<String>> actionLists = (List<List<String>>) json.get("actionLists");
        List<String> times = (List<String>) json.get("times");
        List<String> expectations = (List<String>) json.get("expectations");
        String ontologyPath = (String) json.get("ontologyPath");
        int index = (int) json.get("index");
        for(int i = 0;i < triggerLists.size();i++){
            List<String> triggerList = triggerLists.get(i);
            List<String> actionList = actionLists.get(i);
            String time = times.get(i);
            String expectation = expectations.get(i);
            ifThenRequirements.add(new IfThenRequirement(triggerList, actionList, time, expectation));
        }
        String folderPath = SCDPATH + UUID.randomUUID().toString() + "/";
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        JSONObject result = pfService.getDrPng(ifThenRequirements, ontologyPath, folderPath, index);
        return result;
    }


    @CrossOrigin
    @RequestMapping("/getSbSCD")
    @ResponseBody
    public JSONObject getSbSCD(@RequestBody JSONObject json) throws IOException, DocumentException, InterruptedException {
        List<IfThenRequirement> ifThenRequirements = new ArrayList<>();
        List<List<String>> triggerLists = (List<List<String>>) json.get("triggerLists");
        List<List<String>> actionLists = (List<List<String>>) json.get("actionLists");
        List<String> times = (List<String>) json.get("times");
        List<String> expectations = (List<String>) json.get("expectations");
        String ontologyPath = (String) json.get("ontologyPath");
        int index = (int) json.get("index");
        for(int i = 0;i < triggerLists.size();i++){
            List<String> triggerList = triggerLists.get(i);
            List<String> actionList = actionLists.get(i);
            String time = times.get(i);
            String expectation = expectations.get(i);
            ifThenRequirements.add(new IfThenRequirement(triggerList, actionList, time, expectation));
        }
        String folderPath = SCDPATH + UUID.randomUUID().toString() + "/";
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        JSONObject result = pfService.getSbPng(ifThenRequirements, ontologyPath, folderPath, index);
        return result;
    }

    //图片显示
    @RequestMapping(value="/display",method = RequestMethod.GET)
    @ResponseBody
    public void diaplay(String fileName, HttpServletResponse response) {
        pfService.ToPng(fileName, response);
    }

    @CrossOrigin
    @RequestMapping("/check")
    @ResponseBody
    public List<String> check(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        List<String> errors = checkService.consistencyCheck(requirementTexts, ontologyPath, index);
        if(errors.size() == 0) errors.add("No Rule Errors!");
        return errors;
    }

//    @CrossOrigin
//    @RequestMapping("/z3Check")
//    @ResponseBody
//    public JSONObject z3Check(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
//        File folder = new File(SMTPATH);
//        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
//        String filePath = SMTPATH + UUID.randomUUID().toString() + ".smt2";
//        return checkService.z3Check(filePath, requirementTexts, ontologyPath, index);
//
//    }

    @CrossOrigin
    @RequestMapping("/onenetSimulation")
    @ResponseBody
    public void onenetSimulation(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException, InterruptedException {
        onenetService.runSimulation(onenetService.toOnenet(requirementTexts, ontologyPath, index));
    }

    @CrossOrigin
    @RequestMapping("/downloadOntology")
    public void downloadOntology() throws IOException {
        String filePath = "ontology_SmartConferenceRoom.xml";
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        // 设置信息给客户端不解析
        String type = new MimetypesFileTypeMap().getContentType(filePath);
        // 设置contenttype，即告诉客户端所发送的数据属于什么类型
        response.setHeader("Content-type",type);
        // 设置编码
        String hehe = new String(filePath.getBytes("utf-8"), "iso-8859-1");
        // 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
        response.setHeader("Content-Disposition", "attachment;filename=" + hehe);
        FileUtil.download(filePath, response);
    }

}
