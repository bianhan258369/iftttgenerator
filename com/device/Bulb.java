package com.device;

public class Bulb extends Device {

	
	
	public Bulb(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String bulbButtonDatastrId="bulbButton";
	
	public void addBulbButtonData(Integer value) {
		addData(value,this.bulbButtonDatastrId);
	}
	
	public Integer getBulbButtonData() {
		return Integer.valueOf(getData(this.bulbButtonDatastrId));
	}

}
