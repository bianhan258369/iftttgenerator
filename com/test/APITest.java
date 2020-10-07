package com.test;

import java.util.List;
import java.util.Map;

import cmcc.iot.onenet.javasdk.api.datapoints.AddDatapointsApi;
import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
//import cmcc.iot.onenet.javasdk.api.datapoints.AddDatapointsApi;
import cmcc.iot.onenet.javasdk.api.device.AddDevicesApi;
import cmcc.iot.onenet.javasdk.model.Datapoints;
import cmcc.iot.onenet.javasdk.model.Location;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;
import cmcc.iot.onenet.javasdk.response.device.NewDeviceResponse;

@SuppressWarnings("unused")
public class APITest {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
//		String master_key="8TS2j9ZFa6WNhZTkwyrplUnt300=";
//		String device_name="test";
//		String protocol="EDP";
//		Object auth_info="testdevice";
//		Boolean isPrivate=true;
//		String desc = null;
//		List<String> tags = null;
//		Location location = null;
//		Map<String, Object> other = null;
//		Integer interval = null;
		
		String devId="620624213";
		String devBtnId="620637676";
		Integer type=null;
		Map<String,List<Datapoints>> map=null;
		
		String data="{\"datastreams\": [{\"id\": \"temperature\",\"datapoints\": [{\"value\": 32}]}]}";   //完整json格式
		String dataBtn=null;
		String api_key="tnFGfPSayCRN2R0rTu7=pa1K5=g=";
		String apiBtn_key="8KcCha06iw26JJDJqQWP9oi6=4U=";

		String datastreamIds="temperature";
		String datastreamBtnIds="button";
		String start=null;
		String end=null;
		Integer duration=null;
		Integer limit=null;
		String cursor=null;
		Integer interval=null;
		String metd=null;
		Integer first=null;
		String sort=null;
		Double value=10.0;
		Integer btnValue=0;
		
		
		GetDatapointsListApi getDatapoints=new GetDatapointsListApi(datastreamIds,start,end,devId,duration,limit,cursor,interval,metd,first,sort,api_key);
		BasicResponse<DatapointsList> response=getDatapoints.executeApi();
//		AddDatapointsApi addDatapoints=new AddDatapointsApi(map,data,type,devId,api_key);
//		BasicResponse<Void> response=addDatapoints.executeApi();
		//AddDevicesApi addDevice=new AddDevicesApi(device_name,protocol,desc,tags,location,isPrivate,auth_info,other,interval,master_key);
		//BasicResponse<NewDeviceResponse> response = addDevice.executeApi();
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
		Thread.sleep(2000);

		while(true) {
			
			String val=value.toString();
			data="{\"datastreams\": [{\"id\": \"temperature\",\"datapoints\": [{\"value\": "+val+"}]}]}";
			AddDatapointsApi addDatapoints1=new AddDatapointsApi(map,data,type,devId,api_key);
			BasicResponse<Void> response1=addDatapoints1.executeApi();
			System.out.println("errno:"+response1.errno+" error:"+response1.error);
			System.out.println(response1.getJson());
			Thread.sleep(2000);
			
			if(value<=15&&btnValue==0) {
				btnValue=1;
				dataBtn="{\"datastreams\": [{\"id\": \"button\",\"datapoints\": [{\"value\": 1}]}]}";   //开启暖气
				AddDatapointsApi addBtnDatapoints=new AddDatapointsApi(map,dataBtn,type,devBtnId,apiBtn_key);
				BasicResponse<Void> responseBtn=addBtnDatapoints.executeApi();
				
				System.out.println("errno:"+responseBtn.errno+" error:"+responseBtn.error);
				System.out.println(responseBtn.getJson());
				Thread.sleep(2000);
				
				
				
			}
			if(btnValue==1&&value<26) {
				value=value+0.5;
				Thread.sleep(2000);
			}
			if(value>=26) {
				Thread.sleep(2000);
			}
		}
	}

}
