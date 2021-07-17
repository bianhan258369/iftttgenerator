package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.*;
import com.example.bianhan.iftttgenerator.util.ComputeUtil;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.ArrayStack;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.jni.Time;
import org.dom4j.DocumentException;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

import static com.example.bianhan.iftttgenerator.configuration.PathConfiguration.*;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.*;

@Service("haService")
public class HAService {
    public List<String> getEntityIds() throws InterruptedException, IOException {
        URI uri = URI.create("ws://192.168.31.238:8123/api/websocket");
        List<String> entityIds = new ArrayList<>();
        Set<String> device_domain_set = new HashSet<>();
        Set<String> binary_sensor_device_class_set = new HashSet<>();
        Set<String> numeric_sensor_device_class_set = new HashSet<>();

        BufferedReader br = new BufferedReader(new FileReader(DEVICEREGISTRYTABLE));
        String line = "";
        while ((line = br.readLine()) != null){
            if(line.startsWith("d:")) device_domain_set.add(line.substring(2).split("->")[0]);
            else if(line.startsWith("s:binary")) binary_sensor_device_class_set.add(line.split("->")[1]);
            else if(line.startsWith("s:numeric")) numeric_sensor_device_class_set.add(line.split("->")[1]);
        }

        WebSocketClient client = new WebSocketClient(uri){
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("open!");
            }

            @Override
            public void onMessage(String message) {
                JSONObject result = JSONObject.fromObject(message);
                if(result.getBoolean("success") && result.getInt("id") == 2){
                    for(int i = 0;i < result.getJSONArray("result").size();i++){
                        JSONObject json = (JSONObject) result.getJSONArray("result").get(i);
                        String entity_id = json.getString("entity_id");
                        String domain = entity_id.split("\\.")[0];
//                        String currentState = json.getString("state");
                        JSONObject attributes = json.getJSONObject("attributes");
                        String device_class = attributes.containsKey("device_class")? attributes.getString("device_class") : null;
                        if(device_domain_set.contains(domain)) entityIds.add(entity_id);
                        for(String device_domain : device_domain_set){
                            if(!device_domain.contains(".")){
                                if(device_domain.equals(domain) && !entityIds.contains(entity_id)) entityIds.add(entity_id);
                            }
                            else {
                                String left = device_domain.split("\\.")[0];
                                if(left.equals(domain) && !entityIds.contains(entity_id)) entityIds.add(entity_id);
                            }
                        }
                        if(domain.equals("binary_sensor") && binary_sensor_device_class_set.contains(device_class)) entityIds.add(entity_id);
                        else if(domain.equals("sensor") && numeric_sensor_device_class_set.contains(device_class)) entityIds.add(entity_id);
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("close!");
            }

            @Override
            public void onError(Exception ex) {

            }
        };

        client.connect();
        while(!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
            Thread.sleep(10);
        }
        JSONObject auth = new JSONObject();
        auth.put("type", "auth");
        auth.put("access_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI2MTIyNjExYmFlZmU0NzExOTcyZmIyNzA4MDIwMTc4NCIsImlhdCI6MTYyMjE2NzM2MywiZXhwIjoxOTM3NTI3MzYzfQ.UnqYoPOE1jY9672CcLkIoIhpdBnaLcaiMhhNYWTAsEs");
        client.send(auth.toString());

        JSONObject states = new JSONObject();
        states.put("id", 2);
        states.put("type", "get_states");
        client.send(states.toString());

        while (entityIds.size() == 0){
            Thread.sleep(10);
        }
        client.close();
        return entityIds;
    }

    public void writePersonalDeviceTable(JSONObject entityAreas) throws IOException, InterruptedException {
        {
            URI uri = URI.create("ws://192.168.31.238:8123/api/websocket");
            Set<String> device_domain_set = new HashSet<>();
            Set<String> binary_sensor_device_class_set = new HashSet<>();
            Set<String> numeric_sensor_device_class_set = new HashSet<>();
            Map<String, String> entity_id_mapping_to_device_id = new HashMap<>();
            Map<String, String> entity_id_mapping_to_domain_or_device_class = new HashMap<>();
            Map<String, String> device_id_mapping_to_area_id = new HashMap<>();

            BufferedReader br = new BufferedReader(new FileReader(DEVICEREGISTRYTABLE));
            String line = "";
            while ((line = br.readLine()) != null){
                if(line.startsWith("d:")) device_domain_set.add(line.substring(2).split("->")[0]);
                else if(line.startsWith("s:binary")) binary_sensor_device_class_set.add(line.split("->")[1]);
                else if(line.startsWith("s:numeric")) numeric_sensor_device_class_set.add(line.split("->")[1]);
            }


            WebSocketClient client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("open!");
                }

                @Override
                public void onMessage(String message) {
                    JSONObject result = JSONObject.fromObject(message);
                    if(result.getBoolean("success") && result.getInt("id") == 2){
                        for(int i = 0;i < result.getJSONArray("result").size();i++){
                            JSONObject json = (JSONObject) result.getJSONArray("result").get(i);
                            String entity_id = json.getString("entity_id");
                            String domain = entity_id.split("\\.")[0];
//                        String currentState = json.getString("state");
                            JSONObject attributes = json.getJSONObject("attributes");
                            String device_class = attributes.containsKey("device_class")? attributes.getString("device_class") : null;
                            for(String device_domain : device_domain_set){
                                if(!device_domain.contains(".")){
                                    if(device_domain.equals(domain) && !entity_id_mapping_to_domain_or_device_class.containsKey(entity_id)) entity_id_mapping_to_domain_or_device_class.put(entity_id, domain);
                                }
                                else {
                                    String left = device_domain.split("\\.")[0];
                                    String right = device_domain.split("\\.")[1];
                                    if(left.equals(domain) && entity_id.contains(right)  && !entity_id_mapping_to_domain_or_device_class.containsKey(entity_id)) entity_id_mapping_to_domain_or_device_class.put(entity_id, device_domain);
                                }
                            }
                            if(domain.equals("binary_sensor") && binary_sensor_device_class_set.contains(device_class)) entity_id_mapping_to_domain_or_device_class.put(entity_id, device_class);
                            else if(domain.equals("sensor") && numeric_sensor_device_class_set.contains(device_class)) entity_id_mapping_to_domain_or_device_class.put(entity_id, device_class);
                        }
                    }
                    else if(result.getBoolean("success") && result.getInt("id") == 3){
                        for(int i = 0;i < result.getJSONArray("result").size();i++){
                            JSONObject json = (JSONObject) result.getJSONArray("result").get(i);
                            String entity_id = json.getString("entity_id");
                            String device_id = json.containsKey("device_id") ? json.getString("device_id") : null;
                            String domain = entity_id.split("\\.")[0];
                            if(device_domain_set.contains(domain) || domain.equals("binary_sensor") || domain.equals("sensor"))
                                entity_id_mapping_to_device_id.put(entity_id, device_id);
                        }
                    }
                    else if (result.getBoolean("success") && result.getInt("id") == 4){
                        for(int i = 0;i < result.getJSONArray("result").size();i++){
                            JSONObject json = (JSONObject) result.getJSONArray("result").get(i);
                            String device_id = json.getString("id");
                            String area_id = json.getString("area_id");
                            device_id_mapping_to_area_id.put(device_id, area_id);
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("close!");
                }

                @Override
                public void onError(Exception ex) {

                }
            };

            client.connect();
            while(!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
                Thread.sleep(10);
            }
            JSONObject auth = new JSONObject();
            auth.put("type", "auth");
            auth.put("access_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI2MTIyNjExYmFlZmU0NzExOTcyZmIyNzA4MDIwMTc4NCIsImlhdCI6MTYyMjE2NzM2MywiZXhwIjoxOTM3NTI3MzYzfQ.UnqYoPOE1jY9672CcLkIoIhpdBnaLcaiMhhNYWTAsEs");
            client.send(auth.toString());

            JSONObject states = new JSONObject();
            states.put("id", 2);
            states.put("type", "get_states");
            client.send(states.toString());

            JSONObject entity = new JSONObject();
            entity.put("id", 3);
            entity.put("type", "config/entity_registry/list");
            client.send(entity.toString());

            JSONObject device = new JSONObject();
            device.put("id", 4);
            device.put("type", "config/device_registry/list");
            client.send(device.toString());

            while (entity_id_mapping_to_domain_or_device_class.size() == 0 || entity_id_mapping_to_device_id.size() == 0 || device_id_mapping_to_area_id.size() == 0) {
                Thread.sleep(10);
            }

            client.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(PERSONALDEVICETABLEPATH));
            for (String entity_id : entity_id_mapping_to_domain_or_device_class.keySet()){
                String domain_or_device_class = entity_id_mapping_to_domain_or_device_class.get(entity_id);
                System.out.println(domain_or_device_class);
                String device_id = entity_id_mapping_to_device_id.get(entity_id);
                String area_id = entityAreas.getString(entity_id);
                bw.write(domain_or_device_class + "->" + device_id + "->" + entity_id + "->" + area_id);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
    }

    public void writeAutomations(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<JSONObject> messages = new ArrayList<>();
        Map<String, String> effectMap = computeEffectMap();
        List<String> requirements = Arrays.asList(requirementTexts.split("//"));
        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(initRequirements(requirements), effectMap, ontologyPath).get(index);
        Map<String, NumericSensor> numericSensorMap = new HashMap<>();
        Map<String, BinarySensor> binarySensorMap = new HashMap<>();
        Map<String, DeviceRegistryItem> deviceRegistryItemMap = new HashMap<>();
        List <Entity> entities = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("device_registry.txt"));
        String line = "";
        while ((line = br.readLine()) != null){
            if(line.startsWith("s:") && line.contains("binary")){
                String[] splits = line.split("->");
                binarySensorMap.put(splits[2], new BinarySensor(line));
            }
            else if(line.startsWith("s:") && line.contains("numeric")){
                String[] splits = line.split("->");
                numericSensorMap.put(splits[2], new NumericSensor(line));
            }
            else if(line.startsWith("d:")){
                String[] splits = line.split("->");
                deviceRegistryItemMap.put(splits[1], new DeviceRegistryItem(line));
            }
        }
        br = new BufferedReader(new FileReader("personal_device.txt"));
        while ((line = br.readLine()) != null){
            Entity entity = new Entity(line);
            entities.add(entity);
        }
        br = new BufferedReader(new FileReader("automation_ids.txt"));
        while ((line = br.readLine()) != null){
            if(!line.trim().equals("")){
                HttpDelete httpDelete = new HttpDelete("http://192.168.31.238:8123/api/config/automation/config/" + line);
                RequestConfig requestConfig = RequestConfig.custom().
                        setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                        .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
                httpDelete.setConfig(requestConfig);
                Header[] headers = {new BasicHeader("Content-type", "application/json"), new BasicHeader("Authorization","Bearer " + HATOKEN)};
                httpDelete.setHeaders(headers);
                try {
                    HttpResponse response = httpClient.execute(httpDelete);
                    if (response != null && response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        System.out.println("result:" + result);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("automation_ids.txt"));

        Map<String, Set<String>> locationmap = new HashMap<>();
        br = new BufferedReader(new FileReader("locationmap.txt"));
        while ((line = br.readLine()) != null){
            if(!line.trim().equals("")){
                String left = line.split("->")[0];
                String right = line.split("->")[1];
                List<String> temp = Arrays.asList(right.split(","));
                Set<String> rooms = new HashSet<>();
                rooms.addAll(temp);
                locationmap.put(left, rooms);
            }
        }
        for(IfThenRequirement ifThenRequirement : ifThenRequirements){
            Set<String> rooms = locationmap.get(ifThenRequirement.getRoom());
            for(String room : rooms){
                JSONObject message = new JSONObject();
                JSONArray triggers = new JSONArray();
                JSONArray condition = new JSONArray();
                JSONArray actions = new JSONArray();
                String id = UUID.randomUUID().toString();
                message.put("alias","auto_" + id);
                message.put("description",ifThenRequirement.getIfThenClause());
                message.put("mode","single");
                String time = ifThenRequirement.getTime();
                //write trigger
                for(String trigger : ifThenRequirement.getTriggerList()){
                    JSONObject triggerJSON = new JSONObject();
                    String relation = computeRelation(trigger);
                    //device state trigger
                    if(relation.equals("")){
                        String domain = trigger.split("\\.")[0];
                        String state = trigger.split("\\.")[1];
                        DeviceRegistryItem deviceRegistryItem = deviceRegistryItemMap.get(state);
                        triggerJSON.put("platform","device");
                        triggerJSON.put("type",deviceRegistryItem.getType());

                        for(Entity entity : entities){
                            if(entity.getDomainOrDeviceClass().equals(deviceRegistryItem.getDomain()) && entity.getArea().equals(room)){
                                triggerJSON.put("device_id", entity.getDevice_id());
                                triggerJSON.put("entity_id", entity.getEntity_id());
                                triggerJSON.put("domain", entity.getDomainOrDeviceClass());
                                break;
                            }
                        }
                    }
                    //attribute value trigger
                    else {
                        String monitor = trigger.split(relation)[0];
                        String value = trigger.split(relation)[1];
                        //binary sensor trigger
                        if(binarySensorMap.containsKey(monitor)){
                            BinarySensor binarySensor = binarySensorMap.get(monitor);
                            if(relation.equals("=")){
                                if(value.equals("0")) triggerJSON.put("type", binarySensor.getFalseValue());
                                else triggerJSON.put("type", binarySensor.getTrueValue());
                            }
                            else if(relation.equals("!=")){
                                if(value.equals("0")) triggerJSON.put("type", binarySensor.getTrueValue());
                                else triggerJSON.put("type", binarySensor.getFalseValue());
                            }
                            triggerJSON.put("platform","device");
                            for (Entity entity : entities){
                                if(entity.getDomainOrDeviceClass().equals(binarySensor.getType()) && entity.getArea().equals(room)){
                                    triggerJSON.put("device_id", entity.getDevice_id());
                                    triggerJSON.put("entity_id", entity.getEntity_id());
                                    break;
                                }
                            }
                            triggerJSON.put("domain", "binary_sensor");
                        }
                        //numeric sensor trigger
                        else if(numericSensorMap.containsKey(monitor)){
                            NumericSensor numericSensor = numericSensorMap.get(monitor);
                            triggerJSON.put("type", numericSensor.getType());
                            triggerJSON.put("platform","device");
                            for (Entity entity : entities){
                                if(entity.getDomainOrDeviceClass().equals(numericSensor.getType()) && entity.getArea().equals(room)){
                                    triggerJSON.put("device_id",entity.getDevice_id());
                                    triggerJSON.put("entity_id",entity.getEntity_id());
                                    break;
                                }
                            }
                            triggerJSON.put("domain","sensor");
                            if(relation.equals(">") || relation.equals(">=")) triggerJSON.put("above",value);
                            else triggerJSON.put("below",value);
                        }
                    }
                    if(time != null){
                        JSONObject timeJSON = new JSONObject();
                        //hour
                        if(time.contains("h")) timeJSON.put("hours", Integer.parseInt(time.substring(0, time.length() - 1)));
                        else timeJSON.put("hours", 0);
                        //minute
                        if (time.contains("m")) timeJSON.put("minutes", Integer.parseInt(time.substring(0, time.length() - 1)));
                        else timeJSON.put("minutes", 0);
                        //second
                        if (time.contains("s")) timeJSON.put("seconds", Integer.parseInt(time.substring(0, time.length() - 1)));
                        else timeJSON.put("seconds", 0);
                        //millionseconds
                        timeJSON.put("milliseconds", 0);
                        triggerJSON.put("for",timeJSON);
                    }
                    triggers.add(triggerJSON);
                }
                for(String action : ifThenRequirement.getActionList()){
                    JSONObject actionJSON = new JSONObject();
                    String state = action.split("\\.")[1];
                    DeviceRegistryItem deviceRegistryItem = deviceRegistryItemMap.get(state);
                    String domain = deviceRegistryItem.getDomain();
                    domain = domain.contains(".") ? domain.split("\\.")[0] : domain;
                    actionJSON.put("service", domain + "." + deviceRegistryItem.getService());
                    JSONObject targetJSON = new JSONObject();
                    for(Entity entity : entities){
                        if(entity.getDomainOrDeviceClass().equals(deviceRegistryItem.getDomain()) && entity.getArea().equals(room)){
                            targetJSON.put("entity_id", entity.getEntity_id());
                            break;
                        }
                    }
                    actionJSON.put("target", targetJSON);
                    actions.add(actionJSON);
                }
                message.put("trigger", triggers);
                message.put("condition", condition);
                message.put("action", actions);
                messages.add(message);
            }
        }
        for(JSONObject message : messages){
            System.out.println(message);
            String id = message.getString("alias").substring(5);
            HttpPost httpPost = new HttpPost("http://192.168.31.238:8123/api/config/automation/config/" + id);
            RequestConfig requestConfig = RequestConfig.custom().
                    setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                    .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
            httpPost.setConfig(requestConfig);
            Header[] headers = {new BasicHeader("Content-type", "application/json"), new BasicHeader("Authorization","Bearer " + HATOKEN)};
            httpPost.setHeaders(headers);
            try {
                httpPost.setEntity(new StringEntity(message.toString(), ContentType.create("application/json", "utf-8")));
                HttpResponse response = httpClient.execute(httpPost);
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    if(result.contains("ok")){
                        System.out.println("result:" + result);
                        bw.write(id + "\n");
                        bw.flush();
                    }
                }
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != httpClient) {
            try {
                bw.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public void writeAutomationsYAML(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
//        ComputeUtil.writeAutomationsYAML(requirementTexts, ontologyPath, index);
//    }

    public static void main(String[] args) {
        String id = UUID.randomUUID().toString();
        JSONObject message = new JSONObject();
        JSONArray triggers = new JSONArray();
        JSONArray condition = new JSONArray();
        JSONArray actions = new JSONArray();
        JSONObject action = new JSONObject();
        JSONObject trigger = new JSONObject();
        message.put("alias","aaa");
        message.put("description","aaa");
        message.put("mode","single");
        trigger.put("type","illuminance");
        trigger.put("platform","device");
        trigger.put("device_id","223eedf22b004368e7b9950dc7710c1e");
        trigger.put("entity_id","sensor.light_sensor_2773_light_level");
        trigger.put("domain","sensor");
        trigger.put("above",500);
        JSONObject time = new JSONObject();
        time.put("hours",0);
        time.put("minutes",3);
        time.put("seconds",0);
        time.put("milliseconds",0);
        trigger.put("for",time);
        triggers.add(trigger);
        action.put("service","light.turn_on");
        JSONObject target = new JSONObject();
        target.put("entity_id","light.mibedsidelamp2_6b92");
        action.put("target",target);
        actions.add(action);
        message.put("trigger",triggers);
        message.put("condition",condition);
        message.put("action",actions);
        System.out.println(message.toString());
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonObject = null;



        HttpPost httpPost = new HttpPost("http://192.168.31.238:8123/api/config/automation/config/" + id);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);
        Header[] headers = {new BasicHeader("Content-type", "application/json"), new BasicHeader("Authorization","Bearer " + HATOKEN)};
        httpPost.setHeaders(headers);
        try {
            httpPost.setEntity(new StringEntity(message.toString(), ContentType.create("application/json", "utf-8")));
            System.out.println("request parameters" + EntityUtils.toString(httpPost.getEntity()));
            System.out.println("httpPost:" + httpPost);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println("result:" + result);
                jsonObject = JSONObject.fromObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
