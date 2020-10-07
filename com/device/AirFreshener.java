package com.device;

public class AirFreshener extends Device {

	public AirFreshener(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	
	private String airFreshButtonDatastrId="airFreshenerButton";
	
	public void addAirFreshButtonData(Integer value) {
		addData(value,this.airFreshButtonDatastrId);
	}
	
	public Integer getAirFreshButtonData() {
		return Integer.valueOf(getData(this.airFreshButtonDatastrId));
	}
	

}
