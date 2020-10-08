package com.example.bianhan.iftttgenerator.controller;

import com.example.bianhan.iftttgenerator.service.CheckService;
import com.example.bianhan.iftttgenerator.service.DroolsService;
import com.example.bianhan.iftttgenerator.service.OnenetService;
import com.example.bianhan.iftttgenerator.service.ProblemFrameService;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.SCDPATH;
import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.ontologyRootPath;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeComplementedRequirements;

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
    @RequestMapping("/complement")
    @ResponseBody
    public JSONObject complementRequirements(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException {
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
    @RequestMapping("/getSCD")
    @ResponseBody
    public JSONObject getSCD(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException, InterruptedException {
        JSONObject result = new JSONObject();
        String folderPath = SCDPATH + UUID.randomUUID().toString() + "/";
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        System.out.println(folder.getAbsoluteFile());
        result = pfService.getSdPng(requirementTexts, ontologyPath, folderPath, index);
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

    @CrossOrigin
    @RequestMapping("/onenetSimulation")
    @ResponseBody
    public void onenetSimulation(@RequestParam String requirementTexts, @RequestParam String ontologyPath, @RequestParam int index) throws IOException, DocumentException, InterruptedException {
        onenetService.runSimulation(onenetService.toOnenet(requirementTexts, ontologyPath, index));
    }


}
