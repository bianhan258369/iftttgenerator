package com.device;

public class Window extends Device {

	
	public Window(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String windowButtonDatastrId="windowButton";
	
	public void addWindowButtonData(Integer value) {
		addData(value,this.windowButtonDatastrId);
	}
	
	public Integer getWindowButtonData() {
		return Integer.valueOf(getData(this.windowButtonDatastrId));
	}

}
