package com.device;

public class Fan extends Device {

	public Fan(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	
	private String fanButtonDatastrId="fanButton";
	
	public void addFanButtonData(Integer value) {
		addData(value,this.fanButtonDatastrId);
	}
	
	public Integer getFanButtonData() {
		return Integer.valueOf(getData(this.fanButtonDatastrId));
	}

}
