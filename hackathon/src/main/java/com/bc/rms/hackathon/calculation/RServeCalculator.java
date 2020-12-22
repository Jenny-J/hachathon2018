package com.bc.rms.hackathon.calculation;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
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
public class RServeCalculator {
	static RConnection c;
	private static final Logger logger = LoggerFactory.getLogger(RServeCalculator.class);	
	
	@Autowired
	DefaultRepository defaultRepository;
	
	@Autowired
	OracleRepository oracleRepository;
	
/*	 public static void main(String[] args) throws Exception
{
		 
//	 REngine eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", args, new REngineStdOutput(), false);
//	 RConnection c = new RConnection();
//	 REXP x = c.eval("R.version.string");
//	 System.out.println(x.asString());
	 
//	 REXP rexp = null;
	try {
		rexp = c.parseAndEval(" fahrenheit_to_kelvin <- function(temp_F) {   "
				+ "  temp_K <- ((temp_F - 32) * (5 / 9)) + 273.15; "
				+ "  return(temp_K) ;"
				+ " }", rexp, false);
		rexp = c.parseAndEval("fahrenheit_to_kelvin(154)");
		 System.out.println(rexp.asString());
		 
	 String scriptTest = "fTest <- function (REQUESTTYPE_VALUE, ACTIVITYTYPE_IDINFO, CBC_COUNTVACUUM_PRECOUNT) { "
//+" {  if (REQUESTTYPE_VALUE == 'Patient' && ACTIVITYTYPE_IDINFO == 'Activity.CN' && CBC_COUNTVACUUM_PRECOUNT > 5.98)  { return (TRUE);}   else {return (FALSE);}} ";
+ " if (REQUESTTYPE_VALUE == 'Patient' && ACTIVITYTYPE_IDINFO == 'Activity.CN' && CBC_COUNTVACUUM_PRECOUNT > 5.98)   "
+ "  {  return (TRUE); }   "
+ "  else { return (FALSE);}"
+ "}";
		 
		 RServeCalculator rc = new RServeCalculator();
		 Map<String, Object> param = new HashMap<String, Object>();
		 param.put("REQUESTTYPE_VALUE", "Patient");
		 param.put("ACTIVITYTYPE_IDINFO", "Activity.CN");
		 param.put("CBC_COUNTVACUUM_PRECOUNT", 5);
		 String result = null;
		try {
			result = rc.evaluateForTest(scriptTest, param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			c.close();
		}
		 System.out.println(result);
	
	
}
	 */
	
/*		 public static void main(String[] args) throws Exception
	{
			 
			 
		 String scriptTest = "fTest <- function (REQUESTTYPE_VALUE) {  d <- REQUESTTYPE_VALUE; "
	+ " return (sum(d)) } ";
			 
			 RServeCalculator rc = new RServeCalculator();
			 Map<String, Object> param = new HashMap<String, Object>();
			 ArrayList<Double> para = new ArrayList<Double>();
			 para.add(1.0);
			 para.add(2.0);
			 para.add(3.0);
			 param.put("REQUESTTYPE_VALUE", para);
			 String result = null;
			try {
				result = rc.evaluateForTestPredictive(scriptTest, param);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				c.close();
			}
			 System.out.println(result);
		
		
	}*/
	 
	 public String evaluateForTest(String script, Map<String, Object> param) throws Exception 
	 {
		try {
			logger.debug("starting connection to Rserve");
			c = new RConnection();
			logger.debug("evaluating script:" + script);
			c.parseAndEval(Utils.cleanupForR(script));
			String functionName = Utils.getFunctionNameR(script);
			List<String> parameterList = Utils.getParameterList(script);
			StringBuffer callPara = new StringBuffer("(");
			 for (String para: parameterList)
			 {
				c.assign(para, param.get(para).toString());
	
				 callPara.append(para);
				 callPara.append(",");
			 }
			 callPara.deleteCharAt(callPara.length()-1);
			 callPara.append(")");
			 REXP x = null;
		 
			x = c.eval(functionName+callPara.toString());
		
			return x.asString();
		} catch (Exception e) {
			throw e;
		}
		finally{
			c.close();
		}
	 }
	 
	 
	//Map<String, Object> param is actually Map<String, Object[]> from test run, Map<String, ArrayList> from actual run
	 public String evaluateForTestPredictive(String script, Map<String, Object> param, String algorithmName, Map<String, ParameterData> parameterDataMap) throws Exception 
	 {
		try {
			logger.debug("starting connection to Rserve");
			c = new RConnection();
			logger.debug("evaluating script:" + script);
			REXP vak = c.parseAndEval(Utils.cleanupForR(script));
			String functionName = Utils.getFunctionNameR(script);
			List<String> parameterList = Utils.getParameterList(script);
			StringBuffer callPara = new StringBuffer("(");
			 for (String para: parameterList)
			 {
				 RList list = new RList();
				 List paraList = new ArrayList();
				 Object obj = param.get(para);
				 if (obj instanceof ArrayList)
					 paraList = (ArrayList)obj;
				 else
				 {
					 Object[] objList = (Object[])param.get(para);
					 paraList.addAll(Arrays.asList(objList));
				 }
				 ParameterData parameterData = parameterDataMap.get(para);
				
				 for (Object onePara: paraList)
				 {
					 if (onePara instanceof Double ||onePara instanceof Float)
					 {
						 REXPDouble rexp = new REXPDouble((double) onePara);
						 list.add(rexp);
					 }else  if (onePara instanceof Integer)
					 {
						 REXPInteger rexp = new REXPInteger((int) onePara);
						 list.add(rexp);
					 }else if (onePara instanceof String)
					 {
						 REXPString rexp = new REXPString((String) onePara);
						 list.add(rexp);
					 }else if (onePara != null) //handle the Timestamp
					 {
						 REXPString rexp = new REXPString(onePara.toString());
						 list.add(rexp);
					 }
					 else {
						 if (parameterData.getParameterType().startsWith("Number"))
							 list.add(new REXPDouble(new Double(parameterData.getDefaultValue())));
						 else 
							 list.add(new REXPString(parameterData.getDefaultValue()));
					 }
					 
				 }
			
				 REXPList rexpList = new REXPList(list);
				 c.assign(para, rexpList);
				 callPara.append("unlist(");
				 callPara.append(para);
				 callPara.append("),");
			 }
		
			 callPara.deleteCharAt(callPara.length()-1);
			 callPara.append(")");
			 REXP x = null;
			 x = c.eval(functionName+callPara.toString());	
			 StringBuilder sb = new StringBuilder();
			 if (x != null && x.asStrings()!=null)
				for (String s :x.asStrings()) 
				{
					sb.append(s);
					sb.append("; ");
				}
			 String result = sb.toString();
			 logger.debug("result: " + result);
			 if (algorithmName != null)
			 {
				 param.put("PREDICTIVE_RESULT", result);
				 param.put("RUN_TIME", new Date());
					
				defaultRepository.savePredictiveRun(algorithmName, param);
			 }
				
			return result;
		} catch (Exception e) {
			throw e;
		}
		finally{
			c.close();
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
				return evaluateForTestPredictive (alData.getAlgorithmFunction(), Utils.toMapArray(dataValues, helper.getParams(), alData.getParameterDataMap()), alData.getAlgorithmName(), alData.getParameterDataMap());
			else
				return evaluate (alData, dataValues);
			
			
		}
	 
	 //for trigger run
		public String evaluate(AlgorithmData alData, List<Map<String, Object>> retrievedValue) throws Exception
		{
		
			try{
				String functionName = Utils.getFunctionNameR(alData.getAlgorithmFunction());
				List<String> paramList = Utils.getParameterList(alData.getAlgorithmFunction());
			//	Object[] objList = new Object[paramList.size()];
				
				Map<Integer, Integer> totalCount = new HashMap<Integer, Integer>();
				Map<Integer, Integer> firedCount = new HashMap<Integer, Integer>();
				c = new RConnection();
				
				c.parseAndEval(Utils.cleanupForR(alData.getAlgorithmFunction()));
				
				for (Map<String, Object> oneMap : retrievedValue)
				{
					StringBuffer callPara = new StringBuffer("(");
				
					for (int i = 0; i < paramList.size(); i++)
					{
						String para = paramList.get(i);
						c.assign(para, oneMap.get(para).toString());

						 callPara.append(para);
						 callPara.append(",");
					}
					
					 callPara.deleteCharAt(callPara.length()-1);
					 callPara.append(")");
					 REXP x = null;

					x = c.eval(functionName+callPara.toString());
					oneMap.put("TRIGGER_RESULT", x.asString());
					oneMap.put("RUN_TIME", new Date());
					
					Integer rms_id = (Integer) oneMap.get("RMS_ID");
					
					if (totalCount.get(rms_id) == null)
					{
						totalCount.put(rms_id, 0);
						firedCount.put(rms_id, 0);
					}
					totalCount.put(rms_id, totalCount.get(rms_id)+1);
					
					if ("TRUE".equals(x.asString())){
						if (firedCount.get(rms_id) == null)
							firedCount.put(rms_id, 0);
						firedCount.put(rms_id, firedCount.get(rms_id)+1);
					}
					
					logger.debug("result: " + x.asString());
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
	 
}

