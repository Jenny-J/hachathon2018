package com.bc.rms.hackathon.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bc.rms.hackathon.dao.AlgorithmData;

public class AlgorithmForm {
	
	String tableName;
	String message;
	
	Map<String, String> allTables;
 
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	AlgorithmData algorithmData;
	String param;
	
	

	public AlgorithmForm(AlgorithmData algorithmData, List<String> allColumns, List<String> selectedColumns,
			Map<String, String> selectedTypes, Map<String, String> defaultValues, Map<String, String> testValues, String param) {
		super();
		this.algorithmData = algorithmData;
		this.allColumns = allColumns;
		this.selectedColumns = selectedColumns;
		this.selectedTypes = selectedTypes;
		this.defaultValues = defaultValues;
		this.testValues = testValues;
		this.param = param;
	}

	public AlgorithmForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	List<String> allColumns = new ArrayList<String>();
	
	List<String> selectedColumns = new ArrayList<String>();
	
	
	
	Map<String, String> selectedTypes = new HashMap<String, String>();
	Map<String, String> defaultValues = new HashMap<String, String>();
	Map<String, String> testValues = new HashMap<String, String>();
	

	public AlgorithmData getAlgorithmData() {
		return algorithmData;
	}

	public void setAlgorithmData(AlgorithmData algorithmData) {
		this.algorithmData = algorithmData;
	}

	

	public List<String> getAllColumns() {
		return allColumns;
	}

	public void setAllColumns(List<String> allColumns) {
		this.allColumns = allColumns;
	}

	public List<String> getSelectedColumns() {
		return selectedColumns;
	}

	public void setSelectedColumns(List<String> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

	public Map<String, String> getSelectedTypes() {
		return selectedTypes;
	}

	public void setSelectedTypes(Map<String, String> selectedTypes) {
		this.selectedTypes = selectedTypes;
	}

	public Map<String, String> getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(Map<String, String> defaultValues) {
		this.defaultValues = defaultValues;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, String> getTestValues() {
		return testValues;
	}

	public void setTestValues(Map<String, String> testValues) {
		this.testValues = testValues;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public Map<String, String> getAllTables() {
		return allTables;
	}

	public void setAllTables(Map<String, String> allTables) {
		this.allTables = allTables;
	}


	void reset()
	{
		this.selectedColumns = new ArrayList<String>();
		this.selectedTypes = new HashMap<String, String>();
		this.defaultValues = new HashMap<String, String>();
		this.testValues = new HashMap<String, String>();
	}
}
