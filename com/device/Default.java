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

public class Default {
	

	private String heatButtonId="622232654";
	private String heatBtnApi_key="JtponmGC7L3crDSAJaQgdHm=Idc=";
	private String coldButtonId="622232561";
	private String coldBtnApi_key="VrCmMpfp0WNbN8PJXUebag6qYNU=";
	private String windowBtnId="622234837";
	private String windowBtnApi_key="2YHAfkm4CcXUyq3WeY3YS79ZXRY=";
	private String microBtnId="622234453";
	private String microBtnApi_key="O71brFucisysadsfXgavnpwhRKY=";
	private String bulbBtnId="622235640";
	private String bulbBtnApi_key="P6OnEtP=rJMD6iSVKmPG1Mubx4Y=";
	private String perNumSenId="626026544";
	private String perNumSenApi_key="xyuZ65VffFUn1nfaISQq33qClAM=";
	private String proBtnId="622233552";
	private String proBtnApi_key="u1YCw=B7Y9qfhoncjlx7LDaOm=A=";
	private String proBulbBtnId="625997439";
	private String proBulbBtnApi_key="mA1N=oHbNqKbaVOq178kTKSZHi4=";
	private String curtainBtnId="622234892";
	private String curtainBtnApi_key="4=sa126ONLdTqF0uXo7=oiITdt4=";
	

	private String hBtnDatastrId="heatButton";
	private String cBtnDatastrId="coldButton";
	private String windowBtnDatastrId="windowBtn";
	private String microBtnDatastrId="microButton";
	private String bulbBtnDataId="bulbButton";
	private String perNumDataId="personNum";
	private String proBtnDatastrId="proButton";
	private String proBulbBtnDatastrId="proBulbBtn";
	private String curtainBtnDatastrId="curtainButton";
	
	private Integer type=null;
	private Map<String,List<Datapoints>> map=null;

	private String hBtnData=null;
	private String cBtnData=null;
	private String windowBtnData=null;
	private String microBtnData=null;
	private String bulbBtnData=null;
	private String perNumData=null;
	private String proBtnData=null;
	private String proBulbBtnData=null;
	private String curtainBtnData=null;
	

	private Integer hBtnVal=1;
	private Integer cBtnVal=0;
	private Integer windowBtnVal=1;
	private Integer microBtnVal=1;
	private Integer bulbBtnVal=1;
	private Integer perNumVal;
	private Integer proBtnVal=1;
	private Integer proBulbBtnVal=1;
	private Integer curtainBtnVal=1;
	
	private String start=null;
	private String end=null;
	private Integer duration=null;
	private Integer limit=null;
	private String cursor=null;
	private Integer interval=null;
	private String metd=null;
	private Integer first=null;
	private String sort=null;
	
	
	
	public void def() throws InterruptedException {
		perNumVal=0;
		String perNumStr=perNumVal.toString();
		String hBtnStr;
		String cBtnStr;
		String windowBtnStr;
		String microBtnStr;
		String bulbBtnStr;
		String proBtnStr;
		String proBulbBtnStr;
		String curtainBtnStr;
		hBtnStr=Default.getData(hBtnDatastrId, start, end, heatButtonId, duration, 
				limit, cursor, interval, metd, first, sort, heatBtnApi_key);
		cBtnStr=Default.getData(cBtnDatastrId, start, end, coldButtonId, duration, 
				limit, cursor, interval, metd, first, sort, coldBtnApi_key);
		windowBtnStr=Default.getData(windowBtnDatastrId, start, end, windowBtnId, duration, 
				limit, cursor, interval, metd, first, sort, windowBtnApi_key);
		microBtnStr=Default.getData(microBtnDatastrId, start, end, microBtnId, duration, 
				limit, cursor, interval, metd, first, sort, microBtnApi_key);
		bulbBtnStr=Default.getData(bulbBtnDataId, start, end, bulbBtnId, duration, 
				limit, cursor, interval, metd, first, sort, bulbBtnApi_key);
		proBtnStr=Default.getData(proBtnDatastrId, start, end, proBtnId, duration, 
				limit, cursor, interval, metd, first, sort, proBtnApi_key);
		proBulbBtnStr=Default.getData(proBulbBtnDatastrId, start, end, proBulbBtnId, duration, 
				limit, cursor, interval, metd, first, sort, proBulbBtnApi_key);
		curtainBtnStr=Default.getData(curtainBtnDatastrId, start, end, curtainBtnId, duration, 
				limit, cursor, interval, metd, first, sort, curtainBtnApi_key);
		
		hBtnVal=Integer.valueOf(hBtnStr);
		cBtnVal=Integer.valueOf(cBtnStr);
		windowBtnVal=Integer.valueOf(windowBtnStr);
		microBtnVal=Integer.valueOf(microBtnStr);
		bulbBtnVal=Integer.valueOf(bulbBtnStr);
		proBtnVal=Integer.valueOf(proBtnStr);
		proBulbBtnVal=Integer.valueOf(proBulbBtnStr);
		curtainBtnVal=Integer.valueOf(curtainBtnStr);
		
		
		
		for(int i=0;i<30;i++) {
			perNumData="{\"datastreams\": [{\"id\": \""+perNumDataId+"\",\"datapoints\": [{\"value\": "+perNumStr+"}]}]}";
			AddDatapointsApi addPerNumDp=new AddDatapointsApi(map,perNumData,type,perNumSenId,perNumSenApi_key);
			BasicResponse<Void> perNumR=addPerNumDp.executeApi();
			System.out.println("errno:"+perNumR.errno+" error:"+perNumR.error);
			System.out.println(perNumR.getJson());
			Thread.sleep(2000);
		}
		
		
		if(perNumVal==0) {
			if(hBtnVal==1) {
				hBtnVal=0;
				hBtnStr=hBtnVal.toString();
				hBtnData="{\"datastreams\": [{\"id\": \""+hBtnDatastrId+"\",\"datapoints\": [{\"value\": "+hBtnStr+"}]}]}";
				AddDatapointsApi addhBtnDp=new AddDatapointsApi(map,hBtnData,type,heatButtonId,heatBtnApi_key);
				BasicResponse<Void> hBtnR=addhBtnDp.executeApi();
				
				System.out.println("errno:"+hBtnR.errno+" error:"+hBtnR.error);
				System.out.println(hBtnR.getJson());
				
				Thread.sleep(2000);
			}
			if(cBtnVal==1) {
				cBtnVal=0;
				cBtnStr=cBtnVal.toString();
				cBtnData="{\"datastreams\": [{\"id\": \""+cBtnDatastrId+"\",\"datapoints\": [{\"value\": "+cBtnStr+"}]}]}";
				AddDatapointsApi addcBtnDp=new AddDatapointsApi(map,cBtnData,type,coldButtonId,coldBtnApi_key);
				BasicResponse<Void> cBtnR=addcBtnDp.executeApi();
				
				System.out.println("errno:"+cBtnR.errno+" error:"+cBtnR.error);
				System.out.println(cBtnR.getJson());
				
				Thread.sleep(2000);
				
			}
			if(windowBtnVal==1) {
				windowBtnVal=0;
				windowBtnStr=windowBtnVal.toString();
				windowBtnData="{\"datastreams\": [{\"id\": \""+windowBtnDatastrId+"\",\"datapoints\": [{\"value\": "+windowBtnStr+"}]}]}";
				AddDatapointsApi addWBtnDp=new AddDatapointsApi(map,windowBtnData,type,windowBtnId,windowBtnApi_key);
				BasicResponse<Void> wBtnR=addWBtnDp.executeApi();
				System.out.println("errno:"+wBtnR.errno+" error:"+wBtnR.error);
				System.out.println(wBtnR.getJson());
				Thread.sleep(2000);
			}
			if(microBtnVal==1) {
				microBtnVal=0;
				microBtnStr=microBtnVal.toString();
				microBtnData="{\"datastreams\": [{\"id\": \""+microBtnDatastrId+"\",\"datapoints\": [{\"value\": "+microBtnStr+"}]}]}";
				AddDatapointsApi addBtnDp=new AddDatapointsApi(map,microBtnData,type,microBtnId,microBtnApi_key);
				BasicResponse<Void> btnR=addBtnDp.executeApi();
				
				System.out.println("errno:"+btnR.errno+" error:"+btnR.error);
				System.out.println(btnR.getJson());
				Thread.sleep(2000);
			}
			if(bulbBtnVal==1) {
				bulbBtnVal=0;
				bulbBtnStr=bulbBtnVal.toString();
				bulbBtnData="{\"datastreams\": [{\"id\": \""+bulbBtnDataId+"\",\"datapoints\": [{\"value\": "+bulbBtnStr+"}]}]}";
				AddDatapointsApi addBulbDp=new AddDatapointsApi(map,bulbBtnData,type,bulbBtnId,bulbBtnApi_key);
				BasicResponse<Void> bulbR=addBulbDp.executeApi();
				System.out.println("errno:"+bulbR.errno+" error:"+bulbR.error);
				System.out.println(bulbR.getJson());
			}
			if(proBtnVal==1) {
				proBtnVal=0;
				proBtnStr=proBtnVal.toString();
				proBtnData="{\"datastreams\": [{\"id\": \""+proBtnDatastrId+"\",\"datapoints\": [{\"value\": "+proBtnStr+"}]}]}";
				AddDatapointsApi addBtnDp=new AddDatapointsApi(map,proBtnData,type,proBtnId,proBtnApi_key);
				BasicResponse<Void> btnR=addBtnDp.executeApi();
				
				System.out.println("errno:"+btnR.errno+" error:"+btnR.error);
				System.out.println(btnR.getJson());
				Thread.sleep(2000);
			}
			if(proBulbBtnVal==1) {
				proBulbBtnVal=0;
				proBulbBtnStr=proBulbBtnVal.toString();
				proBulbBtnData="{\"datastreams\": [{\"id\": \""+proBulbBtnDatastrId+"\",\"datapoints\": [{\"value\": "+proBulbBtnStr+"}]}]}";
				AddDatapointsApi addBbBtnDp=new AddDatapointsApi(map,proBulbBtnData,type,proBulbBtnId,proBulbBtnApi_key);
				BasicResponse<Void> bulbBtnR=addBbBtnDp.executeApi();
				
				System.out.println("errno:"+bulbBtnR.errno+" error:"+bulbBtnR.error);
				System.out.println(bulbBtnR.getJson());
				Thread.sleep(2000);
			}
			if(curtainBtnVal==1) {
				curtainBtnVal=0;
				curtainBtnStr=curtainBtnVal.toString();
				curtainBtnData="{\"datastreams\": [{\"id\": \""+curtainBtnDatastrId+"\",\"datapoints\": [{\"value\": "+curtainBtnStr+"}]}]}";
				AddDatapointsApi addCtBtnDp=new AddDatapointsApi(map,curtainBtnData,type,curtainBtnId,curtainBtnApi_key);
				BasicResponse<Void> ctBtnR=addCtBtnDp.executeApi();
				
				System.out.println("errno:"+ctBtnR.errno+" error:"+ctBtnR.error);
				System.out.println(ctBtnR.getJson());
				Thread.sleep(2000);
			}
		}
		
		perNumVal=0;
	}
	
	public static String getData(String datastreamIds, String start, String end, String devId, Integer duration,
			Integer limit, String cursor, @Deprecated Integer interval, String metd, Integer first, String sort,String key) {
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
		
		
		System.out.println(datastreamIds+value);
		return value;
	}

	public Integer gethBtnVal() {
		return hBtnVal;
	}

	public void sethBtnVal(Integer hBtnVal) {
		this.hBtnVal = hBtnVal;
	}

	public Integer getcBtnVal() {
		return cBtnVal;
	}

	public void setcBtnVal(Integer cBtnVal) {
		this.cBtnVal = cBtnVal;
	}

	public Integer getWindowBtnVal() {
		return windowBtnVal;
	}

	public void setWindowBtnVal(Integer windowBtnVal) {
		this.windowBtnVal = windowBtnVal;
	}

	public Integer getMicroBtnVal() {
		return microBtnVal;
	}

	public void setMicroBtnVal(Integer microBtnVal) {
		this.microBtnVal = microBtnVal;
	}

	public Integer getBulbBtnVal() {
		return bulbBtnVal;
	}

	public void setBulbBtnVal(Integer bulbBtnVal) {
		this.bulbBtnVal = bulbBtnVal;
	}

	public Integer getProBtnVal() {
		return proBtnVal;
	}

	public void setProBtnVal(Integer proBtnVal) {
		this.proBtnVal = proBtnVal;
	}

	public Integer getProBulbBtnVal() {
		return proBulbBtnVal;
	}

	public void setProBulbBtnVal(Integer proBulbBtnVal) {
		this.proBulbBtnVal = proBulbBtnVal;
	}

	public Integer getCurtainBtnVal() {
		return curtainBtnVal;
	}

	public void setCurtainBtnVal(Integer curtainBtnVal) {
		this.curtainBtnVal = curtainBtnVal;
	}

}
