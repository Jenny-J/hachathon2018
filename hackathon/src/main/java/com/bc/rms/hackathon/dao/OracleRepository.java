package com.bc.rms.hackathon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bc.rms.hackathon.calculation.DefaultCalculator;

@Repository
public class OracleRepository {
	@Autowired
	DataSource oracleDataSource;
	
	private static final Logger logger = LoggerFactory.getLogger(OracleRepository.class);	
	
	public List<Map<String, Object>> retrieveRMSData (RunHelperData helper)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		for(String par: helper.getParams())
		{
			sb.append(par + ", ");
		}
		sb.append(helper.getPartitionCol() + " AS PARTITIONCOL, ");
		sb.append("RMS_ID, ");
		sb.append("MESSAGE_ID ");
		sb.append("FROM ");
		sb.append(helper.getTableName() + " ");
		sb.append("WHERE RMS_ID IN (");
		for (Integer rmsID: helper.getRmsIDs())
		{
			sb.append(rmsID + ", ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append(") ");
		sb.append("AND " + helper.getPartitionCol() + " >= sysdate - " + helper.getHours() + "/24 ");
		
		sb.append("order by PARTITIONCOL");
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>(); 
		try {
			connection= oracleDataSource.getConnection();
			statement = connection.prepareStatement(sb.toString());
			logger.debug(sb.toString());
			resultset = statement.executeQuery();
			while (resultset.next())
			{
				Map<String, Object> oneRow = new LinkedHashMap<String, Object>();
				oneRow.put("RMS_ID", resultset.getInt("RMS_ID"));
				oneRow.put("MESSAGE_ID", resultset.getInt("MESSAGE_ID"));
				oneRow.put(helper.getPartitionCol(), resultset.getTimestamp("PARTITIONCOL"));
				for(String param: helper.getParams())
				{
					oneRow.put(param, resultset.getObject(param));
				}
				results.add(oneRow);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				resultset.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return results;
		
	}
	
	
}
