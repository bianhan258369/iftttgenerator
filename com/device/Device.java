package com.device;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import cmcc.iot.onenet.javasdk.api.datapoints.AddDatapointsApi;
import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.model.Datapoints;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;

public class Device {
	protected String devId="";
	protected String devName="";
	protected String devApi_key="";
	
	public Device(String devId, String devName,String devApi_key){
		this.devId=devId;
		this.devName=devName;
		this.devApi_key=devApi_key;
	}
	
	//获取数据
	public String getData(String datastreamId) {
		String start=null;
		String end=null;
		Integer duration=null;
		Integer limit=null;
		String cursor=null;
		Integer interval=null;
		String metd=null;
		Integer first=null;
		String sort=null;
		
		GetDatapointsListApi getDatapoints= new GetDatapointsListApi(datastreamId, start, end, this.devId, duration,limit,cursor,interval,metd,first,sort,this.devApi_key);
		BasicResponse<DatapointsList> response=getDatapoints.executeApi();
		System.out.println("errno:"+response.errno+" error:"+response.error);
		
		System.out.println(response.getJson());
		//字符串转json格式
		JSONObject jsonObj = new JSONObject(response.getJson());
		String data=jsonObj.get("data").toString();
		JSONObject jsonData = new JSONObject(data);
		JSONArray jsonDSArray = jsonData.getJSONArray("datastreams");
		
		JSONObject jsonDataStr = jsonDSArray.getJSONObject(0);
		
		
		JSONArray jsonDPArray = jsonDataStr.getJSONArray("datapoints");
		JSONObject jsonDataP = jsonDPArray.getJSONObject(0);
		String value=jsonDataP.get("value").toString();
		
		
		System.out.println("获取"+datastreamId+": "+value);
		return value;
		
	}
	
	//发送数据流值
	public void addData(Integer value,String datastreamId) {
		Map<String,List<Datapoints>> map=null;
		Integer type=null;
		
		String data=null;
		String valStr=value.toString();
		data="{\"datastreams\": [{\"id\": \""+datastreamId+"\",\"datapoints\": [{\"value\": "+valStr+"}]}]}";
		
		AddDatapointsApi addDp=new AddDatapointsApi(map,data,type,this.devId,this.devApi_key);
		BasicResponse<Void> response=addDp.executeApi();
		System.out.println("发送"+datastreamId+": "+valStr);
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
	}
	
	public void addData(Double value,String datastreamId) {
		Map<String,List<Datapoints>> map=null;
		Integer type=null;
		
		String data=null;
		String valStr=value.toString();
		data="{\"datastreams\": [{\"id\": \""+datastreamId+"\",\"datapoints\": [{\"value\": "+valStr+"}]}]}";
		
		AddDatapointsApi addDp=new AddDatapointsApi(map,data,type,this.devId,this.devApi_key);
		BasicResponse<Void> response=addDp.executeApi();
		System.out.println("发送"+datastreamId+": "+valStr);
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
	}
}
