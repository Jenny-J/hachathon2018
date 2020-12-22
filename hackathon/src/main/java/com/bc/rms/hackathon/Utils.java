package com.bc.rms.hackathon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bc.rms.hackathon.calculation.DefaultCalculator;
import com.bc.rms.hackathon.dao.ParameterData;

public class Utils {
	
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);	
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public static String  getFunctionName(String function)
	{
		if (StringUtils.isEmpty(function))
			return null;
		if (function.indexOf("function") < 0)
			return null;
		if (function.indexOf('(') <0)
			return null;
		String functionName = function.substring((function.indexOf("function") + 8), function.indexOf('(')).trim();
		return functionName;
		
	} 
	
	public static List<String> getParameterList(String function)
	{
		if (StringUtils.isEmpty(function))
			return null;
		if (function.indexOf("(") < 0)
			return null;
		if (function.indexOf(')') <0)
			return null;
		String params = function.substring((function.indexOf("(") + 1), function.indexOf(')')).trim();
		StringTokenizer st = new StringTokenizer(params, ",");
		List<String> paramList = new ArrayList<String>();
		while (st.hasMoreElements())
		{
			paramList.add(st.nextToken().trim());
		}
		return paramList;
		
	}
	
	public static List<Integer> parseInteger(String integerList)
	{
		StringTokenizer st = new StringTokenizer(integerList, ",");
		List<Integer> paramList = new ArrayList<Integer>();
		while (st.hasMoreElements())
		{
			paramList.add(Integer.valueOf(st.nextToken().trim()));
		}
		return paramList;
	}
	
	// String dateInString = "2014-10-05T15:23:01Z";
	public static Object[] parseTestValues(String testValue, boolean isNumber, boolean isDatetime) throws ParseException
	{
		StringTokenizer st = new StringTokenizer(testValue, ",");
		List<Object> paramList = new ArrayList<Object>();
		while (st.hasMoreElements())
		{
			if (isNumber)
				paramList.add(Double.valueOf(st.nextToken().trim()));
			else if (isDatetime)
			{
				 Date date = formatter.parse(st.nextToken().trim().replaceAll("Z$", "+0000"));
				 paramList.add(date);
			}
				
			else
				paramList.add(st.nextToken().trim());
		}
		return paramList.toArray();
	}
	
	public static String dateToString(Date testValue) throws ParseException
	{
		return formatter.format(testValue);
	}
	
	public static String  getFunctionNameR(String function)
	{
		if (StringUtils.isEmpty(function))
			return null;
		if (function.indexOf("<-") < 0)
			return null;
		String functionName = function.substring(0, function.indexOf("<-")).trim();
		return functionName;
	} 
	

	public static String cleanupForR(String script)
	{
		return script.replaceAll("(\\r|\\n)", "");
	}
	
	
	//return Map<String, ArrayList>
	public static  Map<String, Object>  toMapArray  (List<Map<String, Object>> dataValues,  List<String> paramList, Map<String, ParameterData> parameterTypeMap)
	{
		Map<String, Object> mapArray = new HashMap<String, Object>();
		for (int i = 0; i < dataValues.size(); i++)
		{
			Map<String, Object> oneRow = dataValues.get(i);
			for (String key : oneRow.keySet())
			{
				if (mapArray.get(key) ==null)
				{
					ArrayList valueList = new ArrayList();
					mapArray.put(key, valueList);
				}
				if (paramList.contains(key) && "NumberList".equals(parameterTypeMap.get(key).getParameterType()))
				{
					Double value = null;
					try{
						 value = Double.valueOf(oneRow.get(key).toString());
					}catch(Exception nex)
					{
						logger.info("parsing error " + key + ":" + oneRow.get(key));
						
					}
					((ArrayList)mapArray.get(key)).add(value);
				}
				else
					((ArrayList)mapArray.get(key)).add(oneRow.get(key));
			}
			
		}
		return mapArray;
		
	}

	
}
