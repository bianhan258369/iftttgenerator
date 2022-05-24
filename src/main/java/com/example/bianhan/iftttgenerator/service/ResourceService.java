package com.example.bianhan.iftttgenerator.service;

import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeResources;

@Service("resourceService")
public class ResourceService {
    public JSONObject getResourcesUsed(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        return computeResources(requirementTexts, ontologyPath, index);
    }

    public static void main(String[] args) {
        Random random = new Random();
        Double[] res = new Double[]{86.33, 89.67, 93.33, 89.00, 87.67, 84.33, 91.67, 91.00, 86.33, 91.67};
        for(int i = 0;i < 10;i ++){
            int am = random.nextInt(2);
            Double n = 3 * random.nextDouble();
            if(am == 1) System.out.println(0.01 * (res[i] + n));
            else System.out.println(0.01 * (res[i] - n));
        }
    }
}