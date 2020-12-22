package com.bc.rms.hackathon.controller;

import java.util.List;

import com.bc.rms.hackathon.dao.AlgorithmData;

public class RunTriggerForm {
	
	int selectedAlgIndex;
	List<AlgorithmData> allAlg ;
	
	AlgorithmData selectedAlg;
	
	int hours;
	
	String rmsIDs;
	String param;
	String message;

	public int getSelectedAlgIndex() {
		return selectedAlgIndex;
	}

	public void setSelectedAlgIndex(int selectedAlgIndex) {
		this.selectedAlgIndex = selectedAlgIndex;
	}

	public List<AlgorithmData> getAllAlg() {
		return allAlg;
	}

	public void setAllAlg(List<AlgorithmData> allAlg) {
		this.allAlg = allAlg;
	}

	public AlgorithmData getSelectedAlg() {
		return selectedAlg;
	}

	public void setSelectedAlg(AlgorithmData selectedAlg) {
		this.selectedAlg = selectedAlg;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public String getRmsIDs() {
		return rmsIDs;
	}

	public void setRmsIDs(String rmsIDs) {
		this.rmsIDs = rmsIDs;
	}

	
	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/*public RunTriggerForm(int selectedAlgIndex, List<AlgorithmData> allAlg, AlgorithmData selectedAlg, int hours,
			String rmsIDs, String param) {
		super();
		this.selectedAlgIndex = selectedAlgIndex;
		this.allAlg = allAlg;
		this.selectedAlg = selectedAlg;
		this.hours = hours;
		this.rmsIDs = rmsIDs;
		this.param = param;
	}
	*/
	
	
	

}
