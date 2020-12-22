package com.bc.rms.hackathon.dao;

import java.util.List;

public class RunHelperData {
	
	List<Integer> rmsIDs; 
	int hours; 
	List<String> params; 
	String partitionCol; 
	String tableName;
	public List<Integer> getRmsIDs() {
		return rmsIDs;
	}
	public void setRmsIDs(List<Integer> rmsIDs) {
		this.rmsIDs = rmsIDs;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
	public String getPartitionCol() {
		return partitionCol;
	}
	public void setPartitionCol(String partitionCol) {
		this.partitionCol = partitionCol;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	

}
