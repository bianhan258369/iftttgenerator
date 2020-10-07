package com.device;

public class Microphone extends Device {

	public Microphone(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	private String microButtonDatastrId="microphoneButton";
	
	public void addMicroButtonData(Integer value) {
		addData(value,this.microButtonDatastrId);
	}
	
	public Integer getMicroButtonData() {
		return Integer.valueOf(getData(this.microButtonDatastrId));
	}


}
