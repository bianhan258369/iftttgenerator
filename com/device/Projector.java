package com.device;

public class Projector extends Device {

	
	public Projector(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}

	private String projButtonDatastrId="projectorButton";
	
	public void addProjButtonData(Integer value) {
		addData(value,this.projButtonDatastrId);
	}
	
	public Integer getProjButtonData() {
		return Integer.valueOf(getData(this.projButtonDatastrId));
	}

}
