package com.bc.rms.hackathon.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class DefaultRepository {
	
	@Autowired
    MongoTemplate mongoTemplate;
	private static final Logger logger = LoggerFactory.getLogger(DefaultRepository.class);	
	
	
	public Map<String,String> retrieveAllTables ()
	{
		Map<String,String> allTables = new LinkedHashMap<String,String>();
		Query query = new Query();
		List<LinkedHashMap> resultsMapList = mongoTemplate.findAll(LinkedHashMap.class, "TABLE_COLUMNS");
		for (LinkedHashMap one:resultsMapList )
		{
			allTables.put((String) one.get("TABLE_NAME"), (String) one.get("TABLE_NAME"));
		}
		return allTables;
		
	}
	
	public ArrayList<String> retrieveTableColumns (String tableName)
	{
//		Set<String> collections = mongoTemplate.getCollectionNames();
//		logger.debug(collections.toString());
		Query query = new Query(Criteria.where("TABLE_NAME").is(tableName));
		LinkedHashMap<String, Object> resultsMap = mongoTemplate.findOne(query, LinkedHashMap.class, "TABLE_COLUMNS");
		ArrayList<String> results = (ArrayList<String>) resultsMap.get("COLUMNS");
		return results;
		
	}
	
	public void saveAlgorithm (AlgorithmData algorithm)
	{
		Query query = new Query(Criteria.where("TABLE_NAME").is(algorithm.getTableName()));
		LinkedHashMap<String, Object> resultsMap = mongoTemplate.findOne(query, LinkedHashMap.class, "TABLE_COLUMNS");
		String partitionCol = (String) resultsMap.get("PARTITION_COL");
		algorithm.setPartitionCol(partitionCol);
		mongoTemplate.save(algorithm, "ALGORITHMS");
	}
	
	public AlgorithmData retrieveAlgorithm (String algorithmName, boolean predictive)
	{
		Query query = new Query();
		Criteria criteria =  new Criteria().andOperator(Criteria.where("algorithmName").is(algorithmName), (Criteria.where("predictive").is(predictive)));
		query.addCriteria(criteria);
		AlgorithmData data = mongoTemplate.findOne(query, AlgorithmData.class, "ALGORITHMS");
		return data;
	}
	
	public List<AlgorithmData> retrieveAllAlgorithm (boolean predictive)
	{
		Query query = new Query(Criteria.where("predictive").is(predictive));
		List<AlgorithmData> algorithmList = mongoTemplate.find(query, AlgorithmData.class, "ALGORITHMS");
		return algorithmList;
	}
	
	public void saveTriggerRun (String algorithmName, List<Map<String, Object>> trigger_run)
	{
		mongoTemplate.insert(trigger_run, "TRIGGER_RUN_" + algorithmName);
	}
	
	public void savePredictiveRun (String algorithmName, Map<String, Object> predictive_run)
	{
		mongoTemplate.insert(predictive_run, "PREDICTIVE_RUN_" + algorithmName);
	}

}
