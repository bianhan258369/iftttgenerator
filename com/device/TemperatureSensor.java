package com.device;

import java.util.Timer;
import java.util.TimerTask;

public class TemperatureSensor extends Device {

	public TemperatureSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String tempeDatastrId="temperature";
	
	public void addTempeData(Double value) {
		value=Double.valueOf(String.format("%.1f", value));
		addData(value,this.tempeDatastrId);
	}
	
	public Double getTempeData() {
		return Double.valueOf(getData(this.tempeDatastrId));
	}
	
//	定时发送，但这个实在不合理
	public void timerAddTempeData(Double value) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				addTempeData(value);
			}
			
		}, 0,5000);
	}

}
