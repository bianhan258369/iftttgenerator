package com.device;

public class BrightnessSensor extends Device {

	public BrightnessSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	
	private String brightDatastrId="brightness";
	
	public void addBrightnessData(Double value) {
		value=Double.valueOf(String.format("%.1f", value));
		addData(value,this.brightDatastrId);
	}
	
	public Double getBrightnessData() {
		return Double.valueOf(getData(this.brightDatastrId));
	}

}
