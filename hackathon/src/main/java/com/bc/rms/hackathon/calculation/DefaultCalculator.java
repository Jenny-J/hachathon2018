package com.bc.rms.hackathon.calculation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bc.rms.hackathon.Utils;
import com.bc.rms.hackathon.dao.AlgorithmData;
import com.bc.rms.hackathon.dao.DefaultRepository;
import com.bc.rms.hackathon.dao.OracleRepository;
import com.bc.rms.hackathon.dao.ParameterData;
import com.bc.rms.hackathon.dao.RunHelperData;

@Component
public class DefaultCalculator {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultCalculator.class);	
	final ScriptEngineManager manager = new ScriptEngineManager();
	final ScriptEngine engine = manager.getEngineByName("nashorn");
	final Invocable invocable = (Invocable)engine;

	@Autowired
	DefaultRepository defaultRepository;
	
	@Autowired
	OracleRepository oracleRepository;
	
	public Object evaluateForTest(String script, Map<String, Object> param) throws Exception
	{
	
		try{
			String functionName = Utils.getFunctionName(script);
			List<String> paramList = Utils.getParameterList(script);
			Object[] objList = new Object[paramList.size()];
			for (int i = 0; i < paramList.size(); i++)
			{
				objList[i] = param.get(paramList.get(i));
			}
			
			engine.eval(script);
			Object result = invocable.invokeFunction(functionName, objList);
			logger.debug( result.toString());
			return result;
		}catch (Exception ex){
			logger.error("error", ex);
			throw ex;
		
		}
		
	}
	public Object evaluateForTestPredictive(String script, Map<String, Object[]> param) throws Exception
	{
		try{
			String functionName = Utils.getFunctionName(script);
			List<String> paramList = Utils.getParameterList(script);
			Object[] objList = new Object[paramList.size()];
			for (int i = 0; i < paramList.size(); i++)
			{
				objList[i] = param.get(paramList.get(i));
			}
			
			engine.eval(script);
			Object result = invocable.invokeFunction(functionName, objList);
			logger.debug( result.toString());
			return result;
		}catch (Exception ex){
			logger.error("error", ex);
			throw ex;
		
		}
		
	}


	
	public String evaluate(AlgorithmData alData, List<Map<String, Object>> retrievedValue) throws Exception
	{
	
		try{
			String functionName = Utils.getFunctionName(alData.getAlgorithmFunction());
			List<String> paramList = Utils.getParameterList(alData.getAlgorithmFunction());
			Object[] objList = new Object[paramList.size()];
			
			Map<Integer, Integer> totalCount = new HashMap<Integer, Integer>();
			Map<Integer, Integer> firedCount = new HashMap<Integer, Integer>();
			engine.eval(alData.getAlgorithmFunction());
			
			for (Map<String, Object> oneMap : retrievedValue)
			{
			
				for (int i = 0; i < paramList.size(); i++)
				{
					objList[i] = oneMap.get(paramList.get(i));
				}
				
	//			engine.eval(alData.getAlgorithmFunction());
				boolean result = (boolean)invocable.invokeFunction(functionName, objList);
				oneMap.put("TRIGGER_RESULT", result);
				oneMap.put("RUN_TIME", new Date());
				
				Integer rms_id = (Integer) oneMap.get("RMS_ID");
				
				if (totalCount.get(rms_id) == null)
				{
					totalCount.put(rms_id, 0);
					firedCount.put(rms_id, 0);
				}
				totalCount.put(rms_id, totalCount.get(rms_id)+1);
				
				if (result){
					if (firedCount.get(rms_id) == null)
						firedCount.put(rms_id, 0);
					firedCount.put(rms_id, firedCount.get(rms_id)+1);
				}
				
				logger.debug("result: " + result);
			}
			
			defaultRepository.saveTriggerRun(alData.getAlgorithmName(), retrievedValue);
			StringBuffer sb = new StringBuffer();
			for (Integer rmsID : totalCount.keySet()){
				sb.append("RMS ID: " + rmsID + ", Total Run:" + totalCount.get(rmsID) + ", Trigger Fired: " + firedCount.get(rmsID) + "; ");
			}
			return sb.toString();
		}catch (Exception ex){
			logger.error("error", ex);
			throw ex;
		
		}
		
	}
	
	public String evaluatePredictive(AlgorithmData alData, List<Map<String, Object>> retrievedValue) throws Exception
	{
	
		try{

			String functionName = Utils.getFunctionName(alData.getAlgorithmFunction());
			List<String> paramList = Utils.getParameterList(alData.getAlgorithmFunction());
			Object[] objList = new Object[paramList.size()];
			
			Map<String, Object> mappedValues = new LinkedHashMap<String, Object>();
			
			Map<String, ParameterData> parameterTypeMap = alData.getParameterDataMap();

			for (Map<String, Object> oneMap : retrievedValue)
			{
				for (String col: oneMap.keySet())
				{
					if (mappedValues.get(col)==null)
						mappedValues.put(col, new ArrayList<Object>());
					
					if (paramList.contains(col) && "NumberList".equals(parameterTypeMap.get(col).getParameterType()))
					{
						Double value = null;
						try{
							 value = Double.valueOf(oneMap.get(col).toString());
						}catch(Exception nex)
						{
							logger.info("parsing error " + col + ":" + oneMap.get(col));
							
						}
						
						((ArrayList<Object>) mappedValues.get(col)).add(value);
					}
					else 
						((ArrayList<Object>) mappedValues.get(col)).add(oneMap.get(col));
				}
			}
				
			for (int i = 0; i < paramList.size(); i++)
			{
				objList[i] =  ((ArrayList<Object>)mappedValues.get(paramList.get(i))).toArray();
			}
				
			engine.eval(alData.getAlgorithmFunction());
			String result = (String)invocable.invokeFunction(functionName, objList);
			
			mappedValues.put("PREDICTIVE_RESULT", result);
			mappedValues.put("RUN_TIME", new Date());
			
			logger.debug("result: " + result);
			
			
			defaultRepository.savePredictiveRun(alData.getAlgorithmName(), mappedValues);
			return result;
		}catch (Exception ex){
			logger.error("error", ex);
			throw ex;
		
		}
		
	}
	
	public String calculator (AlgorithmData alData, List<Integer> rmsIDs, int hours) throws Exception
	{
		
		RunHelperData helper = new RunHelperData();
		helper.setHours(hours);
		helper.setParams(new ArrayList(alData.getParameterDataMap().keySet()));
		helper.setPartitionCol(alData.getPartitionCol());
		helper.setRmsIDs(rmsIDs);
		helper.setTableName(alData.getTableName());
		
		List<Map<String, Object>> dataValues =  oracleRepository.retrieveRMSData(helper);
		if (dataValues == null || dataValues.size() == 0)
			return "no records found";
		if (alData.isPredictive())
			return evaluatePredictive (alData, dataValues);
		else
			return evaluate (alData, dataValues);
		
		
	}
	
	
	
	

	

}
