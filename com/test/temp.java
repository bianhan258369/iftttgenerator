package com.test;



import java.util.Scanner;

import com.device.AirConditioner;
import com.device.AirFreshener;
import com.device.AirHumidifier;
import com.device.Bulb;
import com.device.COConcentrateSensor;
import com.device.Blind;
import com.device.BrightnessSensor;
import com.device.HumiditySensor;
import com.device.MicroDistSensor;
import com.device.Microphone;
import com.device.PerNumSensor;
import com.device.ProDistSensor;
import com.device.Projector;
import com.device.RaindropSensor;
import com.device.TemperatureSensor;
import com.device.Window;

public class SmartConferRoom {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		PerNumSensor perNumSensor = new PerNumSensor("633465298","perNumSensor","LE8PNLJjcSs2upg43C36qw94K6Y=");
		AirConditioner airConditioner=new AirConditioner("629656342","airConditioner","=eJLxHzgGHAPCtMSJmYtIctKkIA=");
		TemperatureSensor temperatureSensor=new TemperatureSensor("629656613", "temperatureSensor", "5Q0OU=n8awltnweqe96erYSQ=bs=");
		Bulb bulb = new Bulb("629656775","bulb","=FrjeihALTs9N35JFRiTyfzYUPY=");
		Microphone microphone =new Microphone("629656942", "microphone", "NubWUiQPn3xLCQBev=iMMBiwNHU=");
		MicroDistSensor microDistSensor=new MicroDistSensor("629657086", "microDistSensor", "I2zlD0feRAgGF2Z==K5eGaLxdfs=");
		Projector projector =new Projector("629657353", "projector", "Ojajh1nbJsbmXk0NGC71eNjhrGE=");
		ProDistSensor proDistSensor=new ProDistSensor("629657489", "proDistSensor", "=kMVH1SJ8cLoQY5r0WQYkoUHKoE=");
		Window window=new Window("629657627","window","bg=g0jLmLSFdFNQwBPDrcOsF7wA=");
		Blind blind=new Blind("629657697", "blind", "rats3BsLf006Pgpedvs4qLCtUz4=");
		RaindropSensor raindropSensor=new RaindropSensor("629658352", "raindropSensor", "Pmy1URxm4pLBvFr=rMkBfkRtXWo=");
		HumiditySensor humiditySensor=new HumiditySensor("629662084", "humiditySensor", "xedHOnHDN7kuXe9XjAPCgcWYst0=");
		BrightnessSensor brightnessSensor = new BrightnessSensor("633695155", "brightnessSensor", "KV4iG=gBp8iR8E=SgwcO3E142zM=");
		AirFreshener airFreshener = new AirFreshener("634733220", "airFreshener", "HYkLbxMahuRstMzvbka4N8hJ=2I=");
		AirHumidifier airHumidifier = new AirHumidifier("634733103","airHumidifier","9XwR=auYtfMwaIDXcHZbOl4aPbk=");
		COConcentrateSensor coConcentrateSensor = new COConcentrateSensor("634732894", "COConcentrateSensor", "5Z6yX8s3BfQVraseu=XhJiUK1pQ=");
		
		
		Double tempeVal=10.0;
		Integer hBtnVal=0;
		Integer cBtnVal=0;
		Integer windowBtnVal=0;
		Integer bulbBtnVal=0;
		Double brightnessVal=50.0;
		Integer perNumVal=10;
		Integer microBtnVal=0;
		Double microDistVal=null;
		int microphoneCount=0;
		int microDistCount=0;
		Integer proBtnVal=0;
		Double proDistVal=null;
		Double humidityVal=20.0;
		int projectorCount=0;
		int proDistCount=0;
		Integer blindBtnVal=1;
		Integer raindropVal=0;
		int tempeCount=0;
		int brightCount=0;
		int humidityCount=0;
		int raindropCount=0;
		Integer airFreshBtnVal=0;
		Integer airHumBtnVal=0;
		Double coConcentrateVal=10.0;
		int coConcentrateCount=0;
		
		
		
		while(perNumVal==0) {
			System.out.println("多少人在会议室：");
			perNumVal=s.nextInt();
			perNumSensor.addPerNumData(perNumVal);
			Thread.sleep(3000);
		}
		
		proDistVal=0.5;
		microDistVal=0.4;
		
		temperatureSensor.addTempeData(tempeVal);
		airConditioner.addcButtonData(cBtnVal);
		airConditioner.addhButtonData(hBtnVal);
		bulb.addBulbButtonData(bulbBtnVal);
		microphone.addMicroButtonData(microBtnVal);
		microDistSensor.addMicroDistData(microDistVal);
		projector.addProjButtonData(proBtnVal);
		proDistSensor.addProDistData(proDistVal);
		window.addWindowButtonData(windowBtnVal);
		blind.addBlindButtonData(blindBtnVal);
		raindropSensor.addRaindropData(raindropVal);
		humiditySensor.addHumidityData(humidityVal);
		brightnessSensor.addBrightnessData(brightnessVal);
		airFreshener.addAirFreshButtonData(airFreshBtnVal);
		airHumidifier.addAirHumButtonData(airHumBtnVal);
		coConcentrateSensor.addCOConcentrateData(coConcentrateVal);
		Thread.sleep(3000);
		
		
		while(perNumVal>0) {
			perNumSensor.addPerNumData(perNumVal);
			//-----------
			//此处增加if...then...value的变化
			
			//-----------
			
			
			//button数据传输
			if(airConditioner.gethButtonData()!=hBtnVal) {
				airConditioner.addhButtonData(hBtnVal);
			}
			if(airConditioner.getcButtonData()!=cBtnVal) {
				airConditioner.addcButtonData(cBtnVal);
			}
			
			if(bulb.getBulbButtonData()!=bulbBtnVal) {
				bulb.addBulbButtonData(bulbBtnVal);
			}
			
			if(microphone.getMicroButtonData()!=microBtnVal) {
				microphone.addMicroButtonData(microBtnVal);
			}
			
			if(projector.getProjButtonData()!=proBtnVal) {
				projector.addProjButtonData(proBtnVal);
			}
			
			if(window.getWindowButtonData()!=windowBtnVal) {
				window.addWindowButtonData(windowBtnVal);
			}
			
			if(blind.getBlindButtonData()!=blindBtnVal) {
				blind.addBlindButtonData(blindBtnVal);
			}
			
			if(airFreshener.getAirFreshButtonData()!=airFreshBtnVal) {
				airFreshener.addAirFreshButtonData(airFreshBtnVal);
			}
			
			if(airHumidifier.getAirHumButtonData()!=airHumBtnVal) {
				airHumidifier.addAirHumButtonData(airHumBtnVal);
			}
			
			
			
			if(coConcentrateCount==3) {
				//每3秒发送一次co浓度
				if(airFreshener.getAirFreshButtonData()==1&coConcentrateVal>45) {
					//开空气净化器的co浓度变化
					coConcentrateVal=coConcentrateVal-2;
				}
				coConcentrateSensor.addCOConcentrateData(coConcentrateVal);
				coConcentrateCount=0;
			}
			
			if(tempeCount==3) {
				//每3秒钟发送一次温度数据
				if(airConditioner.gethButtonData()==1&&tempeVal<26) {
					//加热的温度变化
					tempeVal=tempeVal+0.5;
					temperatureSensor.addTempeData(tempeVal);
				}else if(airConditioner.getcButtonData()==1&&tempeVal>26){
					//制冷的温度变化
					tempeVal=tempeVal-0.5;
					temperatureSensor.addTempeData(tempeVal);	
				}else {
					//否则维持温度
					temperatureSensor.addTempeData(tempeVal);
				}
				//重新置零
				tempeCount=0;
			}
			
			if(brightCount==3) {
				//每3秒发送一次光亮数据
				if(bulb.getBulbButtonData()==1) {
					//如果灯开了，光度为200
					brightnessVal=200.0;
				}
				brightnessSensor.addBrightnessData(brightnessVal);
				brightCount=0;
			}
			
			if(humidityCount==3) {
				//每3秒发送一次湿度数据
				if(airHumidifier.getAirHumButtonData()==1&humidityVal<40) {
					humidityVal=humidityVal+2;
				}
				humiditySensor.addHumidityData(humidityVal);
				humidityCount=0;
			}
			
			if(raindropCount==3) {
				//每3秒发送一次雨滴传感器数据
				raindropSensor.addRaindropData(raindropVal);
				raindropCount=0;
			}
			
			//麦克风距离数据
			
			if(microDistCount==1) {
				if(microDistSensor.getMicroDistData()>0.5&&microDistVal<=0.5) {
					microphoneCount=0;
				}
				if(microDistSensor.getMicroDistData()<=0.5&&microDistVal<=0.5) {
					microphoneCount++;
				}
				microDistSensor.addMicroDistData(microDistVal);
				microDistCount=0;
			}
			
			if(proDistCount==1) {
				if(proDistSensor.getProDistData()>1.0&&proDistVal<=1.0) {
					projectorCount=0;
				}
				if(proDistSensor.getProDistData()<=1.0&&proDistVal<=1.0) {
					projectorCount++;
				}
				proDistSensor.addProDistData(proDistVal);
				proDistCount=0;
			}
			
			
			
			Thread.sleep(1000);
			microDistCount++;
			proDistCount++;
			tempeCount++;
			brightCount++;
			humidityCount++;
			raindropCount++;
			coConcentrateCount++;
		}
		s.close();
		
		while(perNumVal==0) {
			//恢复到默认设置
			if(airConditioner.getcButtonData()==1) {
				cBtnVal=0;
				airConditioner.addcButtonData(cBtnVal);
			}
			if(airConditioner.gethButtonData()==1) {
				hBtnVal=0;
				airConditioner.addhButtonData(hBtnVal);
			}
			
			if(window.getWindowButtonData()==1) {
				windowBtnVal=0;
				window.addWindowButtonData(windowBtnVal);
			}
			
			if(blind.getBlindButtonData()==1) {
				blindBtnVal=0;
				blind.addBlindButtonData(blindBtnVal);
			}
			
			if(projector.getProjButtonData()==1) {
				proBtnVal=0;
				projector.addProjButtonData(proBtnVal);
			}
			
			if(microphone.getMicroButtonData()==1) {
				microBtnVal=0;
				microphone.addMicroButtonData(microBtnVal);
			}
			
			if(bulb.getBulbButtonData()==1) {
				bulbBtnVal=0;
				bulb.addBulbButtonData(bulbBtnVal);
			}
			
			if(airFreshener.getAirFreshButtonData()==1) {
				airFreshBtnVal=0;
				airFreshener.addAirFreshButtonData(airFreshBtnVal);
			}
			
			if(airHumidifier.getAirHumButtonData()==1) {
				airHumBtnVal=0;
				airHumidifier.addAirHumButtonData(airHumBtnVal);
			}
		}
		

	}

}
