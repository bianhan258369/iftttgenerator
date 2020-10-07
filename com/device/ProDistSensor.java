package com.device;

import java.util.Timer;
import java.util.TimerTask;

public class ProDistSensor extends Device {

	
	
	public ProDistSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String proDistDatastrId="proDistance";
	
	public void addProDistData(Double value) {
		addData(value,this.proDistDatastrId);
	}
	
	public Double getProDistData() {
		return Double.valueOf(getData(this.proDistDatastrId));
	}
	
	public void timerAddProDistData(Double value) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				addProDistData(value);
			}
			
		},0,3000);
	}

}
