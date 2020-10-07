package com.device;


public class AirConditioner extends Device {
	
	public AirConditioner(String devId, String devName, String devApi_key) {
		super(devId, devName, devApi_key);
		// TODO Auto-generated constructor stub
	}



	private String hButtonDatastrId="hButton";
	private String cButtonDatastrId="cButton";

	
	
	
	
	public void addhButtonData(Integer value) {
		addData(value,this.hButtonDatastrId);
//		if(value==1) {
//			addData(0,this.cButtonDatastrId);
//		}
	}
	
	public void addcButtonData(Integer value) {
		addData(value,this.cButtonDatastrId);
//		if(value==1) {
//			addData(0,this.hButtonDatastrId);
//		}
	}
	
	
	public Integer gethButtonData() {
		return Integer.valueOf(getData(this.hButtonDatastrId));
	}
	
	public Integer getcButtonData() {
		return Integer.valueOf(getData(this.cButtonDatastrId));
	}
//	//定时传送hButton的数据到onenet
//	public void timerAddhButtonData(Integer value) {
//		Timer timer=new Timer();
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				addhButtonData(value);
//			}
//			
//		}, 0,3000);
//	}
//	//定时传送cButton的数据到onenet
//	public void timerAddcButtonData(Integer value) {
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				addcButtonData(value);
//			}
//			
//		}, 0,3000);
//	}
	
	

}
