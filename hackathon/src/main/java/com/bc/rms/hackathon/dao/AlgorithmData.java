package com.bc.rms.hackathon.dao;


import java.util.Map;

public class AlgorithmData {
	
	
	String tableName;
	String algorithmName;
	String functionName;
	String algorithmFunction;
	String partitionCol;
	String language;
	
	boolean predictive;
	
	Map<String, ParameterData> ParameterDataMap;

	public AlgorithmData() {
	}
	public String getAlgorithmName() {
		return algorithmName;
	}
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	public String getAlgorithmFunction() {
		return algorithmFunction;
	}
	public void setAlgorithmFunction(String algorithmFunction) {
		this.algorithmFunction = algorithmFunction;
	}
	
	public Map<String, ParameterData> getParameterDataMap() {
		return ParameterDataMap;
	}
	public void setParameterDataMap(Map<String, ParameterData> parameterDataMap) {
		ParameterDataMap = parameterDataMap;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getPartitionCol() {
		return partitionCol;
	}
	public void setPartitionCol(String partitionCol) {
		this.partitionCol = partitionCol;
	}
	public boolean isPredictive() {
		return predictive;
	}
	public void setPredictive(boolean predictive) {
		this.predictive = predictive;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}




}
