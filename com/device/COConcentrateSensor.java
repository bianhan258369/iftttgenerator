package com.device;

public class COConcentrateSensor extends Device {

	public COConcentrateSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	
	private String COConcentrateDatastrId="COConcentrate";
	
	public void addCOConcentrateData(Double value) {
		value=Double.valueOf(String.format("%.1f", value));
		addData(value,this.COConcentrateDatastrId);
	}
	
	public Double getCOConcentrateData() {
		return Double.valueOf(getData(this.COConcentrateDatastrId));
	}
	
	

}
