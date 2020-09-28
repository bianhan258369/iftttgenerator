package com.example.bianhan.iftttgenerator.controller;

import com.example.bianhan.iftttgenerator.service.CheckService;
import com.example.bianhan.iftttgenerator.service.DroolsService;
import com.example.bianhan.iftttgenerator.service.ProblemFrameService;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.bianhan.iftttgenerator.util.Configuration.SCDPATH;
import static com.example.bianhan.iftttgenerator.util.Configuration.ontologyRootPath;

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

    @CrossOrigin
    @RequestMapping("/upload")
    @ResponseBody
    public JSONObject uploadOntology(@RequestParam("uploadedFile") MultipartFile file) throws IOException {
        JSONObject result = new JSONObject();
        File folder = new File(ontologyRootPath);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        String oldName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf(".")).replaceAll("-","_");
        file.transferTo(new File(ontologyRootPath,newName));
        result.put("filePath", (ontologyRootPath + newName));
        result.put("result", "success");
        return result;
    }

    @CrossOrigin
    @RequestMapping("/transform")
    @ResponseBody
    public List<String> transformToDrools(@RequestParam String requirementTexts, @RequestParam String ontologyPath) throws IOException, DocumentException {
        List<String> drools = Arrays.asList(droolsService.toDrools(requirementTexts, ontologyPath).split("\n"));
        return drools;
    }

    @CrossOrigin
    @RequestMapping("/refine")
    @ResponseBody
    public JSONObject refineRequirements(@RequestParam String requirementTexts, @RequestParam String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        StringBuilder sb = new StringBuilder("");
        List<String> refinedRequirements = (List<String>) droolsService.refineRequirements(requirementTexts, ontologyPath).get("refined");
        for(int i = 0;i < refinedRequirements.size();i++){
            String requirement = refinedRequirements.get(i);
            sb.append(requirement);
            if(i != refinedRequirements.size() - 1) sb.append("\n");
        }
        result.put("result", "success");
        result.put("refinedRequirements", sb.toString());
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getPD")
    @ResponseBody
    public JSONObject getPD(@RequestParam String requirementTexts, @RequestParam String ontologyPath) throws IOException, DocumentException {
        JSONObject result = pfService.getElementsOfPD(requirementTexts, ontologyPath);
        result.put("result","success");
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getSCD")
    @ResponseBody
    public JSONObject getSCD(@RequestParam String requirementTexts, @RequestParam String ontologyPath) throws IOException, DocumentException, InterruptedException {
        JSONObject result = new JSONObject();
        String folderPath = SCDPATH + UUID.randomUUID().toString() + "/";
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        result = pfService.getSdPng(requirementTexts, ontologyPath, folderPath);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/check")
    @ResponseBody
    public List<String> check(@RequestParam String requirementTexts, @RequestParam String ontologyPath) throws IOException, DocumentException {
        JSONObject result = new JSONObject();
        List<String> errors = checkService.consistencyCheck(requirementTexts, ontologyPath);
        if(errors.size() == 0) errors.add("No Errors!");
        return errors;
    }
}
