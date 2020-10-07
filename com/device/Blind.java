package com.device;

public class Blind extends Device {
	
	public Blind(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}
	private String blindButtonDatastrId="blindButton";
	
	public void addBlindButtonData(Integer value) {
		addData(value,this.blindButtonDatastrId);
	}
	public Integer getBlindButtonData() {
		return Integer.valueOf(getData(this.blindButtonDatastrId));
	}
	
	

}
