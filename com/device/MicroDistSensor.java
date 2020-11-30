package com.device;

import java.util.Timer;
import java.util.TimerTask;

public class MicroDistSensor extends Device {

	
	public MicroDistSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String microDistDatastrId="microDistance";
	
	public void addMicroDistData(Double value) {
		value=Double.valueOf(String.format("%.1f", value));
		addData(value,this.microDistDatastrId);
	}
	
	public Double getMicroDistData() {
		return Double.valueOf(getData(this.microDistDatastrId));
	}
	
	public void timerAddMicroDistData(Double value) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				addMicroDistData(value);
			}
			
		},0,3000);
	}

}
