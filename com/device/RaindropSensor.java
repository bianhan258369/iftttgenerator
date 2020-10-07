package com.device;

import java.util.Timer;
import java.util.TimerTask;

public class RaindropSensor extends Device {

	
	public RaindropSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String raindropDatastrId="isRaining";
	
	public void addRaindropData(Integer value) {
		addData(value,this.raindropDatastrId);
	}
	
	public Integer getRaindropData() {
		return Integer.valueOf(getData(this.raindropDatastrId));
	}
	
	public void timerAddRaindropData(Integer value) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				addRaindropData(value);
			}
			
		}, 0,10000);
	}

}
