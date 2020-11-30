package com.device;

import java.util.Timer;
import java.util.TimerTask;

public class HumiditySensor extends Device {
	
	public HumiditySensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String humidityDatastrId="humidity";
	
	public void addHumidityData(Double value) {
		value=Double.valueOf(String.format("%.1f", value));
		addData(value,this.humidityDatastrId);
	}
	
	public Double getHumidityData() {
		return Double.valueOf(getData(this.humidityDatastrId));
	}
	
	//定时发送humidity数据到onenet
	public void timerAddHumidityData(Double value) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		},0,5000);
	}

}
