package com.device;

public class CO2ConcentrateSensor extends Device {

	public CO2ConcentrateSensor(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
		
	}
	private String CO2ConcentrateDatastrId="CO2Concentrate";
	
	public void addCO2ConcentrateData(Double value) {
		value=Double.valueOf(String.format("%.1f", value));
		addData(value,this.CO2ConcentrateDatastrId);
	}
	
	public Double getCO2ConcentrateData() {
		return Double.valueOf(getData(this.CO2ConcentrateDatastrId));
	}

}
