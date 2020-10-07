package com.device;

public class AirHumidifier extends Device {

	public AirHumidifier(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	
	private String airHumButtonDatastrId ="airHumidifierButton";
	
	public void addAirHumButtonData(Integer value) {
		addData(value,this.airHumButtonDatastrId);
	}
	
	public Integer getAirHumButtonData() {
		return Integer.valueOf(getData(this.airHumButtonDatastrId));
	}

}
