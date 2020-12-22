package com.example.bianhan.iftttgenerator.service;

import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeResources;

@Service("resourceService")
public class ResourceService {
    public JSONObject getResourcesUsed(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        return computeResources(requirementTexts, ontologyPath, index);
    }
}
