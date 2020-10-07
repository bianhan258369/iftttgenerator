package com.test;

import org.json.JSONArray;
import org.json.JSONObject;

import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;

public class GetDataTest {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		String datastreamIds="heatButton";
		String start=null;
		String end=null;
		String devId="622232654";
		Integer duration=null;
		Integer limit=null;
		String cursor=null;
		Integer interval=null;
		String metd=null;
		Integer first=null;
		String sort=null;
		String key="JtponmGC7L3crDSAJaQgdHm=Idc=";
		
		GetDatapointsListApi getDatapoints= new GetDatapointsListApi(datastreamIds, start, end, devId, duration,limit,cursor,interval,metd,first,sort,key);
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
		Integer val=Integer.valueOf(value);
		System.out.println(val);
		
		System.out.println(value);
		
		Thread.sleep(2000);

	}

}
