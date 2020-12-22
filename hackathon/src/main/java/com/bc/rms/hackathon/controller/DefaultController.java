package com.bc.rms.hackathon.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.bc.rms.hackathon.Utils;
import com.bc.rms.hackathon.calculation.DefaultCalculator;
import com.bc.rms.hackathon.calculation.RServeCalculator;
import com.bc.rms.hackathon.dao.AlgorithmData;
import com.bc.rms.hackathon.dao.DefaultRepository;
import com.bc.rms.hackathon.dao.ParameterData;

@Controller
public class DefaultController {
	
	@Autowired
	DefaultRepository defaultRepository;
	
	@Autowired
	DefaultCalculator defaultCalculator;
	
	@Autowired
	RServeCalculator rServerCalculator;
	
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
	// inject via application.properties
		@Value("${welcome.message:test}")
		private String message = "Hello World";

		@RequestMapping("/")
		public String welcome(Map<String, Object> model, HttpServletRequest request) throws Exception 
		{
			Map<String,String> allTables = defaultRepository.retrieveAllTables();
			request.getSession().setAttribute( "allTables", allTables );
			
			AlgorithmForm form = new AlgorithmForm();
			form.setAllTables(allTables);
			String table_name = allTables.get(allTables.keySet().iterator().next());
			form.setTableName(table_name);
			ArrayList<String> columns = defaultRepository.retrieveTableColumns(table_name);
			form.setAllColumns(columns);
			request.getSession().setAttribute( "table_name", table_name );
			request.getSession().setAttribute( "allcolumns", columns );
			model.put("form", form);
		
			
			return "algorithmInput";
		}
		
		
		@RequestMapping("/test")
		public String test(@ModelAttribute("form") AlgorithmForm form, HttpServletRequest request)
		{
			if (form.getTableName() != null && !form.getTableName().equals((String)request.getSession().getAttribute( "table_name")))
			{
				request.getSession().setAttribute( "table_name", form.getTableName() );
				ArrayList<String> columns = defaultRepository.retrieveTableColumns(form.getTableName());
				form.setAllColumns(columns);
				request.getSession().setAttribute( "allcolumns", columns);
			}
			form.setTableName((String)request.getSession().getAttribute( "table_name"));
			form.setAllColumns((List<String>)request.getSession().getAttribute( "allcolumns"));
			
			List<String> selectedColumns= form.getSelectedColumns();
			Map<String, String> defaultValues = form.getDefaultValues();
			Map<String, String> defaultTypes = form.getSelectedTypes();
			Map<String, String> testValues = form.getTestValues();
			AlgorithmData algorithm = form.getAlgorithmData();
			algorithm.setPredictive(false);
			Map<String, Object> model = new HashMap<String, Object> ();
			try {
				Map<String, ParameterData> paramMap = new HashMap<String, ParameterData>(); 
				for (String var : selectedColumns)
				{
					ParameterData data = new ParameterData();
					data.setParameterName(var);
					data.setParameterType(defaultTypes.get(var));
					data.setDefaultValue(defaultValues.get(var));
					paramMap.put(var, data);
				}
				
				algorithm.setParameterDataMap(paramMap);
				algorithm.setTableName((String)request.getSession().getAttribute( "table_name"));
				
				String functionName = Utils.getFunctionName(algorithm.getAlgorithmFunction());
				List<String> paramListNew = Utils.getParameterList(algorithm.getAlgorithmFunction());
				
				algorithm.setFunctionName(functionName);
			
				
				if ("Test".equals(form.getParam()))
				{
				
					Map<String, Object> paramsForCal = new HashMap<String, Object>();
					for (String para: paramListNew)
					{
						if ("Number".equals(defaultTypes.get(para)))
							paramsForCal.put(para, Float.valueOf(testValues.get(para)));
						paramsForCal.put(para, testValues.get(para));
					}
					Object result = null;
					if ("R".equals(algorithm.getLanguage()))
						result =	rServerCalculator.evaluateForTest(algorithm.getAlgorithmFunction(), paramsForCal);
					else 
						result =	defaultCalculator.evaluateForTest(algorithm.getAlgorithmFunction(), paramsForCal);
					form.setMessage("Test Result: " + result);
				}
				else if ("Save".equals(form.getParam()))
				{
					defaultRepository.saveAlgorithm( algorithm);
					form.setMessage("Trigger " +algorithm.getAlgorithmName() + " saved successfully");
				}
			} 
			catch (Exception e) {
				logger.error("error in test action", e);
				StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            String exceptionAsString = sw.toString();
				form.setMessage("Error: " + ((exceptionAsString.length()>200)? exceptionAsString.substring(0,200) : exceptionAsString));
			}
			
			model.put("form", form);
			return "algorithmInput";
		    // validate your result
		    // if no errors, save it and redirect to successView.
		}
		
		
	
		@RequestMapping("/run/retrievetrigger")
		public String retrievetriggers(@ModelAttribute("form") RunTriggerForm form, HttpServletRequest request) throws Exception 
		{
			Map<String, Object> model = new HashMap<String, Object> ();
			try{
		
				if ("Run".equals(form.getParam()))
				{
					List<Integer> rmsIDList = Utils.parseInteger(form.getRmsIDs());
				
					AlgorithmData algorithmData = (AlgorithmData) request.getSession().getAttribute( "selectedAlg");
					Object result = null;
					
					if ("R".equals(algorithmData.getLanguage()))
						result = rServerCalculator.calculator(algorithmData , rmsIDList, form.getHours()); 
					else 
						result = defaultCalculator.calculator(algorithmData , rmsIDList, form.getHours()); 
					form.setSelectedAlg(((List<AlgorithmData>) request.getSession().getAttribute("allAlg")).get(form.getSelectedAlgIndex()));
					form.setAllAlg((List<AlgorithmData>) request.getSession().getAttribute( "allAlg"));
					
					form.setMessage("Result: " + result);
					
				}
				
				else if (request.getParameter("selectedAlgIndex") != null && request.getSession().getAttribute("allAlg")!=null)
				{
					int selected = Integer.valueOf(request.getParameter("selectedAlgIndex"));
					form.setSelectedAlgIndex(selected);
					form.setSelectedAlg(((List<AlgorithmData>) request.getSession().getAttribute("allAlg")).get(selected));
					form.setAllAlg((List<AlgorithmData>) request.getSession().getAttribute( "allAlg"));
					request.getSession().setAttribute( "selectedAlg", form.getSelectedAlg());
					model.put("form", form);
					
				}
				else{
					List<AlgorithmData> allAlg = defaultRepository.retrieveAllAlgorithm(false);
		
					form.setAllAlg(allAlg);
					form.setSelectedAlgIndex(0);
					form.setSelectedAlg(allAlg.get(0));
					request.getSession().setAttribute( "allAlg", allAlg );
					request.getSession().setAttribute( "selectedAlg", allAlg.get(0) );
				
				}
			
			}catch(Exception e){
				logger.error("error in test action", e);
				StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            String exceptionAsString = sw.toString();
				form.setMessage("Error: " + ((exceptionAsString.length()>200)? exceptionAsString.substring(0,200) : exceptionAsString));
			}
		
			model.put("form", form);
			return "runTrigger";
		}

		
		
		@RequestMapping("/predictive")
		public String predictiveStart(Map<String, Object> model, HttpServletRequest request) throws Exception 
		{
			Map<String, String> allTables = defaultRepository.retrieveAllTables();
			request.getSession().setAttribute( "allTables", allTables );
			String table_name = allTables.get(allTables.keySet().iterator().next());
			ArrayList<String> columns = defaultRepository.retrieveTableColumns(table_name);
			AlgorithmForm form = new AlgorithmForm();
			form.setTableName(table_name);
			form.setAllColumns(columns);
			model.put("form", form);
			request.getSession().setAttribute( "table_name", table_name );
			request.getSession().setAttribute( "allcolumns", columns );
			return "predictiveModel";
		}
		
		
		
		@RequestMapping("/predictive/test")
		public String predictiveTest(@ModelAttribute("form") AlgorithmForm form, HttpServletRequest request)
		{
			if (form.getTableName() != null && !form.getTableName().equals((String)request.getSession().getAttribute( "table_name")))
			{
				request.getSession().setAttribute( "table_name", form.getTableName() );
				ArrayList<String> columns = defaultRepository.retrieveTableColumns(form.getTableName());
				form.setAllColumns(columns);
				request.getSession().setAttribute( "allcolumns", columns);
				form.reset();
			}
			List<String> selectedColumns= form.getSelectedColumns();
			Map<String, String> defaultValues = form.getDefaultValues();
			Map<String, String> defaultTypes = form.getSelectedTypes();
			Map<String, String> testValues = form.getTestValues();
			AlgorithmData algorithm = form.getAlgorithmData();
			algorithm.setPredictive(true);
			Map<String, Object> model = new HashMap<String, Object> ();
			try {
				Map<String, ParameterData> paramMap = new HashMap<String, ParameterData>(); 
				for (String var : selectedColumns)
				{
					ParameterData data = new ParameterData();
					data.setParameterName(var);
					data.setParameterType(defaultTypes.get(var));
					data.setDefaultValue(defaultValues.get(var));
					paramMap.put(var, data);
				}
				
				algorithm.setParameterDataMap(paramMap);
				algorithm.setTableName((String)request.getSession().getAttribute( "table_name"));
				
				String functionName = Utils.getFunctionName(algorithm.getAlgorithmFunction());
				List<String> paramListNew = Utils.getParameterList(algorithm.getAlgorithmFunction());
				
				algorithm.setFunctionName(functionName);
				
			
				
				form.setTableName((String)request.getSession().getAttribute( "table_name"));
				form.setAllColumns((List<String>)request.getSession().getAttribute( "allcolumns"));
				
				if ("Test".equals(form.getParam()))
				{
				
					Map<String, Object> paramsForCal = new HashMap<String, Object>();
					for (String para: paramListNew)
					{
						boolean isNumber = "NumberList".equals(defaultTypes.get(para));
						boolean isDateTime = "DateTimeList".equals(defaultTypes.get(para));
						Object[] paramList = Utils.parseTestValues(testValues.get(para), isNumber, isDateTime );
						paramsForCal.put(para,paramList );
					}
					Object result = null;
					if ("R".equals(algorithm.getLanguage()))
						result =	rServerCalculator.evaluateForTestPredictive(algorithm.getAlgorithmFunction(), paramsForCal, null,algorithm.getParameterDataMap() ); //passing in null to escape saving
					else
						result =	(defaultCalculator).evaluateForTest(algorithm.getAlgorithmFunction(), paramsForCal);
					form.setMessage("Test Result: " + result);
				}
				
				else if ("Save".equals(form.getParam()))
				{
					defaultRepository.saveAlgorithm( algorithm);
					form.setMessage("Predictive Model " +algorithm.getAlgorithmName() + " saved successfully");
				}
			} 
			catch (Exception e) {
				logger.error("error in test action", e);
				StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            String exceptionAsString = sw.toString();
				form.setMessage("Error: " + ((exceptionAsString.length()>200)? exceptionAsString.substring(0,200) : exceptionAsString));
			}
			
			model.put("form", form);
			return "predictiveModel";
		    // validate your result
		    // if no errors, save it and redirect to successView.
		}
		
		
		@RequestMapping("/predictive/run/retrieve")
		public String retrieveModels(@ModelAttribute("form") RunTriggerForm form, HttpServletRequest request) throws Exception 
		{
			Map<String, Object> model = new HashMap<String, Object> ();
			try{
			if ("Run".equals(form.getParam()))
			{
				List<Integer> rmsIDList = Utils.parseInteger(form.getRmsIDs());
				AlgorithmData algorithm = (AlgorithmData) request.getSession().getAttribute( "selectedAlg");
				Object result = null;
				
					if ("R".equals(algorithm.getLanguage()))
						result = rServerCalculator.calculator((AlgorithmData) request.getSession().getAttribute( "selectedAlg"), rmsIDList, form.getHours()); //
					else 
						result = defaultCalculator.calculator((AlgorithmData) request.getSession().getAttribute( "selectedAlg"), rmsIDList, form.getHours()); //
					form.setSelectedAlg(((List<AlgorithmData>) request.getSession().getAttribute("allAlg")).get(form.getSelectedAlgIndex()));
					form.setAllAlg((List<AlgorithmData>) request.getSession().getAttribute( "allAlg"));
					form.setMessage("Result: " + result);
				
			}
			
			else if (request.getParameter("selectedAlgIndex") != null && request.getSession().getAttribute("allAlg")!=null)
			{
				int selected = Integer.valueOf(request.getParameter("selectedAlgIndex"));
				form.setSelectedAlgIndex(selected);
				form.setSelectedAlg(((List<AlgorithmData>) request.getSession().getAttribute("allAlg")).get(selected));
				form.setAllAlg((List<AlgorithmData>) request.getSession().getAttribute( "allAlg"));
				model.put("form", form);
				request.getSession().setAttribute( "selectedAlg", ((List<AlgorithmData>) request.getSession().getAttribute("allAlg")).get(selected));
			}
			else{
				List<AlgorithmData> allAlg = defaultRepository.retrieveAllAlgorithm(true);
	
				form.setAllAlg(allAlg);
				form.setSelectedAlgIndex(0);
				form.setSelectedAlg(allAlg.get(0));
				request.getSession().setAttribute( "allAlg", allAlg );
				request.getSession().setAttribute( "selectedAlg", allAlg.get(0) );
			
			}
			}catch(Exception e){
				logger.error("error in test action", e);
				StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            String exceptionAsString = sw.toString();
				form.setMessage("Error: " + ((exceptionAsString.length()>200)? exceptionAsString.substring(0,200) : exceptionAsString));
			}
			
			model.put("form", form);
			return "runAnalyzer";
		}
		

}
