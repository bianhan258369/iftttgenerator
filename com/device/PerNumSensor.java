package com.device;

public class PerNumSensor extends Device {

	public PerNumSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	
	public String perNumDatastrId="personNumber";
	
	public void addPerNumData(Integer value) {
		
		addData(value,this.perNumDatastrId);
	}
	
	public Integer getPerNumData() {
		return Integer.valueOf(getData(this.perNumDatastrId));
	}

}
