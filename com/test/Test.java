package com.test;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.device.Default;

import cmcc.iot.onenet.javasdk.api.datapoints.AddDatapointsApi;
import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.model.Datapoints;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		
		String tempeSensorId="622232778";
		String tempeSenApi_key="L=LjJfTEj5IILpPvmtuoeDvF1Ls=";
		String heatButtonId="622232654";
		String heatBtnApi_key="JtponmGC7L3crDSAJaQgdHm=Idc=";
		String coldButtonId="622232561";
		String coldBtnApi_key="VrCmMpfp0WNbN8PJXUebag6qYNU=";
		String windowBtnId="622234837";
		String windowBtnApi_key="2YHAfkm4CcXUyq3WeY3YS79ZXRY=";
		String bulbBtnId="622235640";
		String bulbBtnApi_key="P6OnEtP=rJMD6iSVKmPG1Mubx4Y=";
		String brightSensorId="627094115";
		String brightSenApi_key="i=xw27M2=bZ234USZSC6l3pQiak=";
		String perNumSenId="626026544";
		String perNumSenApi_key="xyuZ65VffFUn1nfaISQq33qClAM=";
		String microBtnId="622234453";
		String microBtnApi_key="O71brFucisysadsfXgavnpwhRKY=";
		String microDistSensorId="622234223";
		String microDistSenApi_key="qXEvA5mh=YI=3xEl1g8134G=99I=";
		String microClockId="625834052";
		String microClockApi_key="IZmgWKSNhufaH0e1rRebgEQn1qI=";
		String proBtnId="622233552";
		String proBtnApi_key="u1YCw=B7Y9qfhoncjlx7LDaOm=A=";
		String proDistSensorId="622233681";
		String proDistSenApi_key="qlqyi08as9NRPx8rdJ4rwzA4=u0=";
		String proClockId="625980488";
		String proClockApi_key="wfFfqyNhRIK0XetZuDveUcNeZ5A=";
		String proBulbBtnId="625997439";
		String proBulbBtnApi_key="mA1N=oHbNqKbaVOq178kTKSZHi4=";
		String curtainBtnId="622234892";
		String curtainBtnApi_key="4=sa126ONLdTqF0uXo7=oiITdt4=";
		String dropletSensorId="622235047";
		String dropletSenApi_key="kxenlUh1erhp8cgZJMZ82ArwK6Q=";

	
		String tempeDatastrId="temperature";
		String hBtnDatastrId="heatButton";
		String cBtnDatastrId="coldButton";
		String windowBtnDatastrId="windowBtn";
		String bulbBtnDataId="bulbButton";
		String brightSenDatastrId="brightness";
		String perNumDataId="personNum";
		String microBtnDatastrId="microButton";
		String microDistDatastrId="microDistance";
		String microClockDatastrId="microClock";
		String proBtnDatastrId="proButton";
		String proDistDatastrId="proDistance";
		String proClockDatastrId="proClock";
		String proBulbBtnDatastrId="proBulbBtn";
		String curtainBtnDatastrId="curtainButton";
		String dropletSenDatastrId="droplet";

		
		Integer type=null;
		Map<String,List<Datapoints>> map=null;
		
		Double tempeVal=10.0;
		Integer hBtnVal=0;
		Integer cBtnVal=0;
		Integer windowBtnVal=0;
		Integer bulbBtnVal=0;
		Double brightnessVal=50.0;
		Integer perNumVal=0;
		Integer perNumVal1=0;
		Integer microBtnVal=0;
		Double microDistVal=null;
		Double microDistVal1=null;   //用来存前一次距离
		Integer microClockVal=0;
		Integer proBtnVal=0;
		Double proDistVal=null;
		Double proDistVal1=null; //用来存前一次距离
		Integer proClockVal=0;
		Integer proBulbBtnVal=0;
		Integer curtainBtnVal=1;
		Integer dropletSenVal=0;
		
		Integer clock=0;
		 
//		String tempeData=null;
//		String hBtnData=null;
//		String cBtnData=null;
//		String windowBtnData=null;
//		String bulbBtnData=null;
//		String brightData=null;
//		String perNumData=null;
//		String microBtnData=null;
//		String microDistData=null;
//		String microClockData=null;
//		String proBtnData=null;
//		String proDistData=null;
//		String proClockData=null;
//		String proBulbBtnData=null;
//		String curtainBtnData=null;
//		String dropletSenData=null;
		
		String hBtnStr=null;
		String cBtnStr=null;
		String windowBtnStr=null;
		String bulbBtnStr=null;
		String microBtnStr=null;
		String proBtnStr=null;
		String proBulbBtnStr=null;
		String curtainBtnStr=null;
		
		String start=null;
		String end=null;
		Integer duration=null;
		Integer limit=null;
		String cursor=null;
		Integer interval=null;
		String metd=null;
		Integer first=null;
		String sort=null;

		
		
		
		while(perNumVal==0) {
			System.out.println("多少人在会议室：");
			perNumVal=s.nextInt();
			Test.addDatapoints(map, type, perNumSenId, perNumSenApi_key, perNumVal, perNumDataId);
			Thread.sleep(3000);
		
		}
		//会议室有人则其他设备开始工作，此处传递初始值
		Test.addDatapoints(map, type, tempeSensorId, tempeSenApi_key, tempeVal, tempeDatastrId);
		Test.addDatapoints(map, type, heatButtonId, heatBtnApi_key, hBtnVal, hBtnDatastrId);
		Test.addDatapoints(map, type, coldButtonId, coldBtnApi_key, cBtnVal, cBtnDatastrId);
		Test.addDatapoints(map, type, windowBtnId, windowBtnApi_key, windowBtnVal, windowBtnDatastrId);
		Test.addDatapoints(map, type, bulbBtnId, bulbBtnApi_key, bulbBtnVal, bulbBtnDataId);
		Test.addDatapoints(map, type, brightSensorId, brightSenApi_key, brightnessVal, brightSenDatastrId);
		Test.addDatapoints(map, type, perNumSenId, perNumSenApi_key, perNumVal, perNumDataId);
//		Test.addDatapoints(map, type, microBtnId, microBtnApi_key, microBtnVal, microBtnDatastrId);
//		Test.addDatapoints(map, type, microDistSensorId, microDistSenApi_key, microDistVal, microDistDatastrId);
//		Test.addDatapoints(map, type, microClockId, microClockApi_key, microClockVal, microClockDatastrId);
//		Test.addDatapoints(map, type, proBtnId, proBtnApi_key, proBtnVal, proBtnDatastrId);
//		Test.addDatapoints(map, type, proDistSensorId, proDistSenApi_key, proDistVal, proDistDatastrId);
//		Test.addDatapoints(map, type, proClockId, proClockApi_key, proClockVal, proClockDatastrId);
		Test.addDatapoints(map, type, proBulbBtnId, proBulbBtnApi_key, proBulbBtnVal, proBulbBtnDatastrId);
		Test.addDatapoints(map, type, curtainBtnId, curtainBtnApi_key, curtainBtnVal, curtainBtnDatastrId);
		Test.addDatapoints(map, type, dropletSensorId, dropletSenApi_key, dropletSenVal, dropletSenDatastrId);
		
		proDistVal=0.5;
		proDistVal1=0.5;
		microDistVal=0.4;
		microDistVal1=0.4;
		
		//会议室有人则其他设备开始工作
		while(perNumVal>0) {
			Test.addDatapoints(map, type, perNumSenId, perNumSenApi_key, perNumVal, perNumDataId);
			//air conditioner
			//如果室内温度低于15，则空调加热
			if(tempeVal<15&&hBtnVal==0) {
				cBtnVal=0;
				hBtnVal=1;
				Test.addDatapoints(map, type, heatButtonId, heatBtnApi_key, hBtnVal, hBtnDatastrId);
			}
			//如果室内温度高于30，则空调制冷
			if(tempeVal>30&&cBtnVal==0) {
				hBtnVal=0;
				cBtnVal=1;
				Test.addDatapoints(map, type, coldButtonId, coldBtnApi_key, cBtnVal, cBtnDatastrId);
			}
			//如果开了空调，就要关窗
			if((hBtnVal==1||cBtnVal==1)&&windowBtnVal==1) {
				windowBtnVal=0;
				Test.addDatapoints(map, type, windowBtnId, windowBtnApi_key, windowBtnVal, windowBtnDatastrId);
			}
			//加热温度变化函数
			if(hBtnVal==1&&tempeVal<26) {
				tempeVal+=0.5;
				Test.addDatapoints(map, type, tempeSensorId, tempeSenApi_key, tempeVal, tempeDatastrId);
			}else if(cBtnVal==1&&tempeVal>26) {
				//制冷温度变化函数
				tempeVal-=0.5;
				Test.addDatapoints(map, type, tempeSensorId, tempeSenApi_key, tempeVal, tempeDatastrId);
			}else {
				Test.addDatapoints(map, type, tempeSensorId, tempeSenApi_key, tempeVal, tempeDatastrId);
			}
			
			
			
			//如果下雨就关窗
			if(dropletSenVal==1&&windowBtnVal==1) {
				windowBtnVal=0;
				Test.addDatapoints(map, type, windowBtnId, windowBtnApi_key, windowBtnVal, windowBtnDatastrId);
			}
			if(dropletSenVal==1&&windowBtnVal==0&&(hBtnVal==1||cBtnVal==1)) {
				windowBtnVal=1;
				Test.addDatapoints(map, type, windowBtnId, windowBtnApi_key, windowBtnVal, windowBtnDatastrId);
			}
			Test.addDatapoints(map, type, dropletSensorId, dropletSenApi_key, dropletSenVal, dropletSenDatastrId);
			
			//projector
			//如果有人靠近projector 10s就开projector
			if(proDistVal<=1&&proBtnVal==0) {
				Test.addDatapoints(map, type, proDistSensorId, proDistSenApi_key, proDistVal, proDistDatastrId);
				if(proDistVal1>1) {
					//如果上次探测到的距离大于1，时钟重新置零
					proClockVal=0;
					Test.addDatapoints(map, type, proClockId, proClockApi_key, proClockVal, proClockDatastrId);
				}else {
					proClockVal+=1;
					Test.addDatapoints(map, type, proClockId, proClockApi_key, proClockVal, proClockDatastrId);
				}
				proDistVal1=proDistVal;				
				if(proClockVal==10) {
					proClockVal=0;
					proBtnVal=1;
					Test.addDatapoints(map, type, proBtnId, proBtnApi_key, proBtnVal, proBtnDatastrId);
					
				}
			}
			//如果有人远离projector 30s就关projector
			if(proDistVal>1.5&&proBtnVal==1) {
				Test.addDatapoints(map, type, proDistSensorId, proDistSenApi_key, proDistVal, proDistDatastrId);
				if(proDistVal1<1.5) {
					//如果上次探测到的距离小于1.5，时钟重新置零
					proClockVal=0;
					Test.addDatapoints(map, type, proClockId, proClockApi_key, proClockVal, proClockDatastrId);
				}else {
					proClockVal+=1;
					Test.addDatapoints(map, type, proClockId, proClockApi_key, proClockVal, proClockDatastrId);
				}
				proDistVal1=proDistVal;				
				if(proClockVal==30) {
					proClockVal=0;
					proBtnVal=0;
					Test.addDatapoints(map, type, proBtnId, proBtnApi_key, proBtnVal, proBtnDatastrId);
					
				}
			}
			//开了projector后，关窗帘和projector附近的灯
			if(proBtnVal==1) {
				if(curtainBtnVal==1) {
					curtainBtnVal=0;
					Test.addDatapoints(map, type, curtainBtnId, curtainBtnApi_key, curtainBtnVal, curtainBtnDatastrId);
				}
				if(proBulbBtnVal==1) {
					proBulbBtnVal=0;
					Test.addDatapoints(map, type, proBulbBtnId, proBulbBtnApi_key, proBulbBtnVal, proBulbBtnDatastrId);
				}
			}
			
			
			//microphone
			//如果距离小于等于0.5则打开麦克风
			if(microDistVal<=0.5&&microBtnVal==0) {
				Test.addDatapoints(map, type, microDistSensorId, microDistSenApi_key, microDistVal, microDistDatastrId);
				if(microDistVal1>0.5) {
					microClockVal=0;
					Test.addDatapoints(map, type, microClockId, microClockApi_key, microClockVal, microClockDatastrId);
				}else {
					microClockVal+=1;
					Test.addDatapoints(map, type, microClockId, microClockApi_key, microClockVal, microClockDatastrId);
				}
				microDistVal1=microDistVal;
				if(microClockVal==5) {
					microClockVal=0;
					microBtnVal=1;
					Test.addDatapoints(map, type, microBtnId, microBtnApi_key, microBtnVal, microBtnDatastrId);
				}
			}
			//如果距离大于0.5则关闭麦克风
			if(microDistVal>0.5&&microBtnVal==1) {
				Test.addDatapoints(map, type, microDistSensorId, microDistSenApi_key, microDistVal, microDistDatastrId);
				if(microDistVal1<0.5) {
					microClockVal=0;
					Test.addDatapoints(map, type, microClockId, microClockApi_key, microClockVal, microClockDatastrId);
				}else {
					microClockVal+=1;
					Test.addDatapoints(map, type, microClockId, microClockApi_key, microClockVal, microClockDatastrId);
				}
				microDistVal1=microDistVal;
				if(microClockVal==5) {
					microClockVal=0;
					microBtnVal=0;
					Test.addDatapoints(map, type, microBtnId, microBtnApi_key, microBtnVal, microBtnDatastrId);
				}
			}
			
			
			//bulb
			//室内光线太暗就开灯
			if(brightnessVal<100&&bulbBtnVal==0) {
				bulbBtnVal=1;
				Test.addDatapoints(map, type, bulbBtnId, bulbBtnApi_key, bulbBtnVal, bulbBtnDataId);
				if(proBulbBtnVal==0&&proBtnVal==0) {
					proBulbBtnVal=1;
					Test.addDatapoints(map, type, proBulbBtnId, proBulbBtnApi_key, proBulbBtnVal, proBulbBtnDatastrId);
				}
			}
			if(bulbBtnVal==1&&proBulbBtnVal==1) {
				brightnessVal=250.0;
				Test.addDatapoints(map, type, brightSensorId, brightSenApi_key, brightnessVal, brightSenDatastrId);				
			}
			if(bulbBtnVal==1&&proBulbBtnVal==0) {
				brightnessVal=200.0;
				Test.addDatapoints(map, type, brightSensorId, brightSenApi_key, brightnessVal, brightSenDatastrId);
			}
			
//			if (tempeVal>30){
//				windowBtnVal=1;
//				Test.addDatapoints(map, type, windowBtnId, windowBtnApi_key, windowBtnVal, windowBtnDatastrId);
//				blindBtnVal=1;
//				Test.addDatapoints(map, type, blindBtnId, blindBtnApi_key, blindBtnVal, blindBtnDatastrId);
//				}

				
			
			
			Thread.sleep(3000);
		}
		
		while(perNumVal==0) {
			
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
			
			if(perNumVal==0) {
				Test.addDatapoints(map, type, perNumSenId, perNumSenApi_key, perNumVal, perNumDataId);
				if(perNumVal1!=0) {
					clock=0;
				}else {
					clock+=1;
				}
				if(clock==30) {
					if(hBtnVal==1) {
						hBtnVal=0;
						Test.addDatapoints(map, type, heatButtonId, heatBtnApi_key, hBtnVal, hBtnDatastrId);
					}
					if(cBtnVal==1) {
						cBtnVal=0;
						Test.addDatapoints(map, type, coldButtonId, coldBtnApi_key, cBtnVal, cBtnDatastrId);
					}
					if(windowBtnVal==1) {
						windowBtnVal=0;
						Test.addDatapoints(map, type, windowBtnId, windowBtnApi_key, windowBtnVal, windowBtnDatastrId);
					}
					if(microBtnVal==1) {
						microBtnVal=0;
						Test.addDatapoints(map, type, microBtnId, microBtnApi_key, microBtnVal, microBtnDatastrId);
					}
					if(bulbBtnVal==1) {
						bulbBtnVal=0;
						Test.addDatapoints(map, type, bulbBtnId, bulbBtnApi_key, bulbBtnVal, bulbBtnDataId);
					}
					if(proBtnVal==1) {
						proBtnVal=0;
						Test.addDatapoints(map, type, proBtnId, proBtnApi_key, proBtnVal, proBtnDatastrId);
					}
					if(proBulbBtnVal==1) {
						proBulbBtnVal=0;
						Test.addDatapoints(map, type, proBulbBtnId, proBulbBtnApi_key, proBulbBtnVal, proBulbBtnDatastrId);
					}
					if(curtainBtnVal==1)
					{
						curtainBtnVal=0;
						Test.addDatapoints(map, type, curtainBtnId, curtainBtnApi_key, curtainBtnVal, curtainBtnDatastrId);
					}
				}
				
			}
			Thread.sleep(3000);
		
		}
		s.close();

	}
	//传数据到onenet平台
	public static void addDatapoints(Map<String,List<Datapoints>> map, Integer type, String devId,String key, Integer value, String datastrId) {
		String data=null;
		String valStr=value.toString();
		data="{\"datastreams\": [{\"id\": \""+datastrId+"\",\"datapoints\": [{\"value\": "+valStr+"}]}]}";
		AddDatapointsApi addDp=new AddDatapointsApi(map,data,type,devId,key);
		BasicResponse<Void> response=addDp.executeApi();
		System.out.println(datastrId+": "+valStr);
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
		
	}
	public static void addDatapoints(Map<String,List<Datapoints>> map, Integer type, String devId,String key, Double value, String datastrId) {
		String data=null;
		String valStr=value.toString();
		data="{\"datastreams\": [{\"id\": \""+datastrId+"\",\"datapoints\": [{\"value\": "+valStr+"}]}]}";
		AddDatapointsApi addDp=new AddDatapointsApi(map,data,type,devId,key);
		BasicResponse<Void> response=addDp.executeApi();
		System.out.println(datastrId+": "+valStr);
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
		
	}
	
	//获取设备当前数据
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
	
	
//	AirConditioner airConditioner=new AirConditioner();
//	airConditioner.airCon();
	
//	Microphone microphone=new Microphone();
//	microphone.micro();
	
//	Projector projector=new Projector();
//	projector.proj();
//	projector.setProDistVal(2.0);
//	projector.proj();
	
//	WindowCurtain windowCurtain=new WindowCurtain();
//	windowCurtain.winCur();
	
//	Bulb bulb=new Bulb();
//	bulb.bulb();
//	
//	
//	Default def=new Default();
//	def.def();


}
