package com.test;



import java.util.Random;
import java.util.Scanner;

import com.device.AirConditioner;
import com.device.AirFreshener;
import com.device.AirHumidifier;
import com.device.Bulb;
import com.device.CO2ConcentrateSensor;
import com.device.COConcentrateSensor;
import com.device.Fan;
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
		Random random=new Random();
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
		CO2ConcentrateSensor co2ConcentrateSensor=new CO2ConcentrateSensor("642064527","CO2ConcentrateSensor","nomwB=7HJCuGGERbLAlnBIy6Erw=");
		//////////////////fan//////////////////////
		Fan fan=new Fan("657048356","fan","fW9FfVzB0LVRkm4rkZ58C1vJ=TQ=");
		/////////////////////////////////////////////
		
		Double tempeVal=random.nextDouble()*10+21;
		Integer hBtnVal=0;
		Integer cBtnVal=0;
		Integer windowBtnVal=0;
		Integer bulbBtnVal=0;
		Double brightnessVal=random.nextDouble()*20+10;
		Integer perNumVal=0;
		Integer microBtnVal=0;
		Double microDistVal=10.0;
		int microphoneCount=0;
		int microDistCount=0;
		Integer proBtnVal=0;
		Double proDistVal=10.0;
		Double humidityVal=random.nextDouble()*20+10;
		int projectorCount=0;
		int proDistCount=0;
		Integer blindBtnVal=0;
		Integer raindropVal=0;
		int tempeCount=0;
		int brightCount=0;
		int humidityCount=0;
		int raindropCount=0;
		Integer airFreshBtnVal=0;
		Integer airHumBtnVal=0;
		Double coConcentrateVal=10.0;
		Double co2ConcentrateVal=random.nextDouble()*100+801;
		int coConcentrateCount=0;
		int co2ConcentrateCount=0;
		int personCount=0;
		int perNumCount=0;
		/////////////////////////fan//////////////////////
		Integer fanBtnVal=0;
		//////////////////////////////////////////////////
		
		double rain=random.nextDouble();
		
		int count=0;  //总的计时
		while(true) {
//			while(perNumVal==0) {
//				System.out.println("多少人在会议室：");
//				perNumSensor.addPerNumData(perNumVal);
//				perNumVal=random.nextInt(5);
//				Thread.sleep(3000);
//			}
//			
//			proDistVal=0.5;
//			microDistVal=0.4;
			
			perNumSensor.addPerNumData(perNumVal);
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
			//coConcentrateSensor.addCOConcentrateData(coConcentrateVal);
			co2ConcentrateSensor.addCO2ConcentrateData(co2ConcentrateVal);
			///////////////////////fan///////////////////////
			fan.addFanButtonData(fanBtnVal);
			////////////////////////////////////////////////
			Thread.sleep(3000);
			
			
			
			perNumVal=SmartConferRoom.getPerNum(count);
			perNumSensor.addPerNumData(perNumVal);
			while(perNumVal>0&&personCount<30) {
				//会议室没人超过30则退出该循环
				
//				if(perNumCount==10) {
//					//每10秒检测人数
//					perNumVal=random.nextInt(perNumVal*2+1);
//					perNumSensor.addPerNumData(perNumVal);
//				}
				perNumVal=SmartConferRoom.getPerNum(count);
				perNumSensor.addPerNumData(perNumVal);
				if(perNumVal==perNumSensor.getPerNumData()&&perNumVal==0) {
					personCount++;
//					if(personCount>=30) {
//						//会议室没人超过30则退出该循环
//						break;
//					}
				}else {
					personCount=0;
				}
				
				
				
				
				//-----------
if ((hBtnVal==0 && cBtnVal==1) && (windowBtnVal==1)){
cBtnVal=0;
hBtnVal=0;
}
if(airConditioner.getcButtonData()!=cBtnVal){
airConditioner.addcButtonData(cBtnVal);
}
if(airConditioner.gethButtonData()!=hBtnVal){
airConditioner.addhButtonData(hBtnVal);
}


if ((hBtnVal==1 && cBtnVal==0) && (windowBtnVal==1)){
cBtnVal=0;
hBtnVal=0;
}
if(airConditioner.getcButtonData()!=cBtnVal){
airConditioner.addcButtonData(cBtnVal);
}
if(airConditioner.gethButtonData()!=hBtnVal){
airConditioner.addhButtonData(hBtnVal);
}


if ((windowBtnVal==1) && (hBtnVal==1||cBtnVal==1)){
cBtnVal=0;
hBtnVal=0;
}
if(airConditioner.getcButtonData()!=cBtnVal){
airConditioner.addcButtonData(cBtnVal);
}
if(airConditioner.gethButtonData()!=hBtnVal){
airConditioner.addhButtonData(hBtnVal);
}


if ((proBtnVal==1)){
windowBtnVal=0;
}
if(window.getWindowButtonData()!=windowBtnVal){
window.addWindowButtonData(windowBtnVal);
}


if (tempeVal>20){
cBtnVal=0;
hBtnVal=0;
}
if(airConditioner.getcButtonData()!=cBtnVal){
airConditioner.addcButtonData(cBtnVal);
}
if(airConditioner.gethButtonData()!=hBtnVal){
airConditioner.addhButtonData(hBtnVal);
}


if (tempeVal<20 && (windowBtnVal==0)){
hBtnVal=1;
cBtnVal=0;
}
if(airConditioner.gethButtonData()!=hBtnVal){
airConditioner.addhButtonData(hBtnVal);
}
if(airConditioner.getcButtonData()!=cBtnVal){
airConditioner.addcButtonData(cBtnVal);
}


if (tempeVal>20){
fanBtnVal=1;
}
if(fan.getFanButtonData()!=fanBtnVal){
fan.addFanButtonData(fanBtnVal);
}


if (brightnessVal<35){
bulbBtnVal=1;
}
if(bulb.getBulbButtonData()!=bulbBtnVal){
bulb.addBulbButtonData(bulbBtnVal);
}


if (microDistVal<2){
microBtnVal=1;
}
if(microphone.getMicroButtonData()!=microBtnVal){
microphone.addMicroButtonData(microBtnVal);
}


if (proDistVal<2){
proBtnVal=1;
}
if(projector.getProjButtonData()!=proBtnVal){
projector.addProjButtonData(proBtnVal);
}


if (proDistVal>2){
proBtnVal=0;
}
if(projector.getProjButtonData()!=proBtnVal){
projector.addProjButtonData(proBtnVal);
}


if ((proBtnVal==1)){
blindBtnVal=0;
}
if(blind.getBlindButtonData()!=blindBtnVal){
blind.addBlindButtonData(blindBtnVal);
}


if (co2ConcentrateVal>800){
airFreshBtnVal=1;
}
if(airFreshener.getAirFreshButtonData()!=airFreshBtnVal){
airFreshener.addAirFreshButtonData(airFreshBtnVal);
}


if (humidityVal<70){
airHumBtnVal=1;
}
if(airHumidifier.getAirHumButtonData()!=airHumBtnVal){
airHumidifier.addAirHumButtonData(airHumBtnVal);
}


if (co2ConcentrateVal<800){
airFreshBtnVal=0;
}
if(airFreshener.getAirFreshButtonData()!=airFreshBtnVal){
airFreshener.addAirFreshButtonData(airFreshBtnVal);
}


if (humidityVal>70){
airHumBtnVal=0;
}
if(airHumidifier.getAirHumButtonData()!=airHumBtnVal){
airHumidifier.addAirHumButtonData(airHumBtnVal);
}


if (microDistVal>2){
microBtnVal=0;
}
if(microphone.getMicroButtonData()!=microBtnVal){
microphone.addMicroButtonData(microBtnVal);
}


if (tempeVal<20){
fanBtnVal=0;
}
if(fan.getFanButtonData()!=fanBtnVal){
fan.addFanButtonData(fanBtnVal);
}


if (perNumVal==0){
blindBtnVal=0;
}
if(blind.getBlindButtonData()!=blindBtnVal){
blind.addBlindButtonData(blindBtnVal);
}


if (perNumVal==0){
cBtnVal=0;
hBtnVal=0;
}
if(airConditioner.getcButtonData()!=cBtnVal){
airConditioner.addcButtonData(cBtnVal);
}
if(airConditioner.gethButtonData()!=hBtnVal){
airConditioner.addhButtonData(hBtnVal);
}


if (perNumVal==0){
fanBtnVal=0;
}
if(fan.getFanButtonData()!=fanBtnVal){
fan.addFanButtonData(fanBtnVal);
}


if (perNumVal==0){
proBtnVal=0;
}
if(projector.getProjButtonData()!=proBtnVal){
projector.addProjButtonData(proBtnVal);
}


if (perNumVal==0){
airFreshBtnVal=0;
}
if(airFreshener.getAirFreshButtonData()!=airFreshBtnVal){
airFreshener.addAirFreshButtonData(airFreshBtnVal);
}


if (perNumVal==0){
microBtnVal=0;
}
if(microphone.getMicroButtonData()!=microBtnVal){
microphone.addMicroButtonData(microBtnVal);
}


if (perNumVal==0){
bulbBtnVal=0;
}
if(bulb.getBulbButtonData()!=bulbBtnVal){
bulb.addBulbButtonData(bulbBtnVal);
}


if (perNumVal==0){
airHumBtnVal=0;
}
if(airHumidifier.getAirHumButtonData()!=airHumBtnVal){
airHumidifier.addAirHumButtonData(airHumBtnVal);
}


if (perNumVal==0){
windowBtnVal=0;
}
if(window.getWindowButtonData()!=windowBtnVal){
window.addWindowButtonData(windowBtnVal);
}
				
				//此处增加if...then...value的变化
				//-----------
				
				//button数据传输

				
				
			
				
				if(co2ConcentrateCount==1) {
						if(airFreshener.getAirFreshButtonData()==1) {
							co2ConcentrateVal=co2ConcentrateVal-30.0;
						}
						co2ConcentrateCount=0;
					}
					co2ConcentrateSensor.addCO2ConcentrateData(co2ConcentrateVal);
					
				
				if(tempeCount==1) {
					//每1秒钟发送一次温度数据
					if(airConditioner.gethButtonData()==1) {
						//加热的温度变化
						tempeVal=tempeVal+0.5;
						
					}else if(airConditioner.getcButtonData()==1){
						//制冷的温度变化
						tempeVal=tempeVal-0.5;
					}
					else if(fan.getFanButtonData()==1){
						//风扇的温度变化
						tempeVal=tempeVal-0.2;
					}
					//重新置零
					tempeCount=0;
				}
				temperatureSensor.addTempeData(tempeVal);
				
				if(brightCount==1) {
					//每1秒发送一次光亮数据
					if(bulb.getBulbButtonData()==1) {
						//如果灯开了，光度为50
						brightnessVal=50.0;
					}
					
					brightCount=0;
				}
				brightnessSensor.addBrightnessData(brightnessVal);
				
				if(humidityCount==1) {
					//每1秒发送一次湿度数据
					if(airHumidifier.getAirHumButtonData()==1) {
						humidityVal=humidityVal+0.5;
					}
					
					humidityCount=0;
				}
				humiditySensor.addHumidityData(humidityVal);
				
				if(raindropCount==1) {
					//每3秒发送一次雨滴传感器数据
					raindropVal=SmartConferRoom.isRain(raindropVal);
					
					raindropCount=0;
				}
				raindropSensor.addRaindropData(raindropVal);
				
				//麦克风距离数据
				
				if(microDistCount==1) {
					//随机micro距离
//					microDistVal=random.nextDouble()*3;
//					if(microDistSensor.getMicroDistData()>0.5&&microDistVal<=0.5) {
//						microphoneCount=0;
//					}
//					if(microDistSensor.getMicroDistData()<=0.5&&microDistVal<=0.5) {
//						microphoneCount++;
//					}
					microDistVal=SmartConferRoom.getDistMc(count);
					
					microDistCount=0;
				}
				microDistSensor.addMicroDistData(microDistVal);
				
				
				if(proDistCount==1) {
					//随机pro距离
//					proDistVal=random.nextDouble()*5;
//					if(proDistSensor.getProDistData()>1.0&&proDistVal<=1.0) {
//						projectorCount=0;
//					}
//					if(proDistSensor.getProDistData()<=1.0&&proDistVal<=1.0) {
//						projectorCount++;
//					}
					proDistVal=SmartConferRoom.getDistPro(count);
					
					proDistCount=0;
				}
				proDistSensor.addProDistData(proDistVal);
				
				
				
				Thread.sleep(1000);
				count++;
				microDistCount++;
				proDistCount++;
				tempeCount++;
				brightCount++;
				humidityCount++;
				raindropCount++;
				coConcentrateCount++;
				perNumCount++;
				co2ConcentrateCount++;
			}
			s.close();
			
			while(perNumVal==0 && personCount>0) {
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
				
				///////////////////////fan///////////////////////////////
				if(fan.getFanButtonData()==1) {
					fanBtnVal=0;
					fan.addFanButtonData(fanBtnVal);
				}
				////////////////////////////////////////////////////////
				
				if(bulb.getBulbButtonData()==0) {
					brightnessVal=30.0;
					brightnessSensor.addBrightnessData(brightnessVal);
				}
				
				
				
				
				personCount=0;
				
			}
			count++;
		}
		
		
		

	}
	//下雨随机事件
	public static Integer isRain(Integer isRain) {
		Random random=new Random();
		double rain=random.nextDouble();
		if(isRain==1) {
			if(rain>0.1) {
				isRain=1;
			}
			if(rain<=0.1) {
				isRain=0;
			}
		}
		if(isRain==0) {
			if(rain>0.9) {
				isRain=1;
			}
			if(rain<=0.9) {
				isRain=0;
			}
		}
		
		return isRain;
		
	}
	
	public static Integer getPerNum(Integer count) {
		//function
		Integer perNum=0;
		if(count>=0&&count<=10) {
			perNum=Integer.valueOf(((int)(0.15*count*count)));
		}
		if(count>10&&count<=40) {
			perNum=15;
		}
		if(count>40&&count<=50) {
			perNum=Integer.valueOf((int)(0.15*(count-50)*(count-50)));
		}
		if(count>50 ) {
			perNum=0;
		}
		return perNum;
	}
	
	public static Double getDistMc(Integer count) {
		//function
		Double dist=10.0;
		if(count>=0&&count<3) {
			dist=10.0;
		}
		if(count>=3&&count<=10) {
			dist=Double.valueOf((count-3)*(count-3)*0.13-9/4*(count-3)+10);
		}
		if(count>10&&count<=43) {
			dist=0.6;
		}
		if(count>43&&count<=50) {
			dist=Double.valueOf(count*count*0.13-43/4*count+445/2);
		}
		if(count>50) {
			dist=10.0;
		}
		return dist;
	}
	
	public static Double getDistPro(Integer count) {
		//function
		
		Double dist=10.0;
		if(count>=0&&count<3) {
			dist=10.0;
		}
		if(count>=3&&count<=10) {
			dist=Double.valueOf((count-3)*(count-3)*0.13-9/4*(count-3)+10);
		}
		if(count>10&&count<=43) {
			dist=0.6;
		}
		if(count>43&&count<=50) {
			dist=Double.valueOf(count*count*0.13-43/4*count+445/2);
		}
		if(count>50) {
			dist=10.0;
		}
		return dist;
	}

}
