package com.synchroteam.beans;

public class TrackingDataBean {

	private String preodicity;
	private String frequency;
	private StringBuilder tabJours;
	private String startTime;
	private String endTime;
	private boolean isTrackingOnOff;
	public String getPreodicity() {
		return preodicity;
	}

	public String getFrequency() {
		return frequency;
	}

	public StringBuilder getTabJours() {
		return tabJours;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}
	
	public boolean isTrackingOnOff(){
		return isTrackingOnOff;
	}

	public TrackingDataBean(String preodicity,String frequency,StringBuilder tabJours,String startTime,String endTime,boolean isTrackingOnOff) {
		// TODO Auto-generated constructor stub
	this.preodicity=preodicity;
	this.frequency=frequency;
	this.tabJours=tabJours;
	this.startTime=startTime;
	this.endTime=endTime;
	this.isTrackingOnOff=isTrackingOnOff;
	
	}

	

}
