package com.appmonitor.support;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.appmonitor.systems.DatabaseSystem;
import com.appmonitor.systems.HTML5System;
import com.appmonitor.systems.JavaSystem;
import com.appmonitor.systems.System;
import com.appmonitor.systems.metrics.Metric;

public abstract class AMDBSupport {
	
	// sql to insert the systems 
	private static final String insertSystemString = "INSERT INTO SYSTEM(ID, TYPE, ACCOUNT, APPLICATION) VALUES(?, ?, ?, ?)";
	
	// sql to insert a systems state
	private static final String insertSystemStateString = "INSERT INTO SYSTEM_STATE(SYSTEM_ID, STATE) VALUES(?, ?)";
	
	// sql to insert the metric 
	private static final String insertMetricString = "INSERT INTO METRIC(NAME, SYSTEM_ID, STATE, METRIC_VALUE, WARNING_THRESHOLD, ERROR_THRESHOLD, TIMESTAMP, METRIC_TYPE, UNIT, MIN, MAX) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	// sql to get a system.  Joins to the system state table to get the latest state
	private static final String getSystemsString = "SELECT A.*, B.STATE FROM SYSTEM A INNER JOIN SYSTEM_STATE B ON A.ID = B.SYSTEM_ID AND B.TIMESTAMP = (SELECT MAX(C.TIMESTAMP) FROM SYSTEM_STATE C WHERE A.ID = C.SYSTEM_ID)";
	
	// sql to get a systems metrics.  Fetches the latest row per metric
	private static final String getMetricsString = "SELECT * FROM METRIC A WHERE SYSTEM_ID = ? AND A.TIMESTAMP = (SELECT MAX(B.TIMESTAMP) FROM METRIC B WHERE A.NAME = B.NAME) ORDER BY NAME, TIMESTAMP ";

	// sql to drop the tables
	private static final String dropMetricsTable = "DROP TABLE IF EXISTS METRIC";
	private static final String dropSystemTable = "DROP TABLE IF EXISTS SYSTEM";
	private static final String dropSystemStateTable = "DROP TABLE IF EXISTS SYSTEM_STATE";
	
	// sql to add the tables
	private static final String addMetricTable = "CREATE TABLE METRIC (\n" + 
			"NAME VARCHAR(255)  NOT NULL,\n" + 
			"SYSTEM_ID VARCHAR(100)  NOT NULL,\n" + 
			"STATE VARCHAR(50) NOT NULL,\n" + 
			"METRIC_VALUE INTEGER NOT NULL,\n" + 
			"WARNING_THRESHOLD INTEGER NOT NULL,\n" + 
			"ERROR_THRESHOLD INTEGER NOT NULL,\n" + 
			"TIMESTAMP INTEGER NOT NULL,\n" + 
			"METRIC_TYPE VARCHAR(100) NOT NULL,\n" + 
			"UNIT VARCHAR(50) NOT NULL,\n" + 
			"MIN INTEGER NOT NULL,\n" + 
			"MAX INTEGER NOT NULL,\n" + 
			"PRIMARY KEY (NAME, SYSTEM_ID, TIMESTAMP))";
	
	private static final String addSystemTable = "CREATE TABLE SYSTEM (\n" + 
			"ID  VARCHAR(100) PRIMARY KEY  NOT NULL,\n" + 
			"TYPE VARCHAR(50) NOT NULL,\n" + 
			"ACCOUNT VARCHAR(100) NOT NULL,\n" + 
			"APPLICATION VARCHAR (200) NOT NULL)";
	
	private static final String addSystemStateTable = "CREATE TABLE SYSTEM_STATE (\n" + 
			"ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" + 
			"SYSTEM_ID VARCHAR(100) NOT NULL,\n" + 
			"STATE VARCHAR(50) NOT NULL,\n" + 
			"TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP)";
	
	 private static Connection getConnection() {
	    String url = "jdbc:sqlite:/Users/Andrew/BU Class/METCS622/Assignments/Week6/SAPPDASH.db";
	    try {
	    	Connection conn = DriverManager.getConnection(url);
	    	return conn;
	    }
	    catch (Exception e)
	    {
	    	java.lang.System.out.println("Error connecting to database..." + e);
	    }
	    return null;
	  }
	 
	 public static void updateSystem(System system) {
			Connection conn = getConnection();
			// first try and update the system state
		     try (PreparedStatement pstmt = conn.prepareStatement(insertSystemStateString)) {
		      pstmt.setString(1, system.getId());
		      pstmt.setString(2, system.getState());

		      pstmt.executeUpdate();
		    } catch (SQLException e) {

		    	java.lang.System.out.println("Error writing System State to the database..." + e);
			}
		     
		     // next we have to insert the metrics
		     for (Metric metric: system.getMetrics())
		     {
		    	 try (PreparedStatement pstmt = conn.prepareStatement(insertMetricString)) {
		    		 pstmt.setString(1, metric.getName());
		    		 pstmt.setString(2, system.getId());
		    		 pstmt.setString(3, metric.getState());
		    		 
		    		 pstmt.setLong(4, metric.getValue());
		    		 pstmt.setLong(5, metric.getWarningThreshold());
		    		 pstmt.setLong(6, metric.getErrorThreshold());
		    		 pstmt.setLong(7, metric.getTimestamp());
		    		 pstmt.setString(8, metric.getMetricType());
		    		 pstmt.setString(9, metric.getUnit());
		    		 pstmt.setLong(10, metric.getMin());
		    		 pstmt.setLong(11, metric.getMax());
		    		 
		    		 pstmt.executeUpdate();
		    	 } catch (SQLException e) {
		    		 
		    		 java.lang.System.out.println("Error writing Metric to the database..." + e);
		    	 }
		    	 
		     }
	 }
	 
	 public static void insertSystem(System system) {
		Connection conn = getConnection();
		// first try and insert the system
	     try (PreparedStatement pstmt = conn.prepareStatement(insertSystemString)) {
	      pstmt.setString(1, system.getId());
	      pstmt.setString(2, system.getType());
	      pstmt.setString(3, system.getAccount());
	      
	      // each system type is slightly different
	      // so check for each and insert accordingly
	      if(system instanceof JavaSystem)
	      {
		      pstmt.setString(4, ((JavaSystem) system).getApplication());
	      }
	      else if (system instanceof HTML5System)
	      {
		      pstmt.setString(4, ((HTML5System) system).getApplication());
	      }
	      else if (system instanceof DatabaseSystem)
	      {
		      pstmt.setString(4, ((DatabaseSystem) system).getDbSystem());
	      }
	      else
	      {
	    	  pstmt.setString(4, "");
	      }

	      pstmt.executeUpdate();
	    } catch (SQLException e) {

	    	java.lang.System.out.println("Error writing System to the database..." + e);
		}
	     
	     // next we insert the system state
	     try (PreparedStatement pstmt = conn.prepareStatement(insertSystemStateString)) {
	      pstmt.setString(1, system.getId());
	      pstmt.setString(2, system.getState());

	      pstmt.executeUpdate();
	    } catch (SQLException e) {

	    	java.lang.System.out.println("Error writing System State to the database..." + e);
		}
	     
	     // next we have to insert the metrics
	     for (Metric metric: system.getMetrics())
	     {
	    	 try (PreparedStatement pstmt = conn.prepareStatement(insertMetricString)) {
	    		 pstmt.setString(1, metric.getName());
	    		 pstmt.setString(2, system.getId());
	    		 pstmt.setString(3, metric.getState());
	    		 
	    		 pstmt.setLong(4, metric.getValue());
	    		 pstmt.setLong(5, metric.getWarningThreshold());
	    		 pstmt.setLong(6, metric.getErrorThreshold());
	    		 pstmt.setLong(7, metric.getTimestamp());
	    		 pstmt.setString(8, metric.getMetricType());
	    		 pstmt.setString(9, metric.getUnit());
	    		 pstmt.setLong(10, metric.getMin());
	    		 pstmt.setLong(11, metric.getMax());
	    		 
	    		 pstmt.executeUpdate();
	    	 } catch (SQLException e) {
	    		 
	    		 java.lang.System.out.println("Error writing Metric to the database..." + e);
	    	 }
	    	 
	     }
	 }
	 
	 public static List<System> getSystems() throws SystemsRecoveryException
	 {
		 // need to get systems and metrics
		 List<System> systems =new ArrayList<System>();

		 // establish the connection and execute the query to get systems
		 try (Statement stmt = getConnection().createStatement();
		         ResultSet rs = stmt.executeQuery(getSystemsString)) {
			 // now loop through the result set
		      while (rs.next()) {
		    	// we need to know the system type in order create the list correctly
		        String sysType = rs.getString(2);
		        System system;
				switch (sysType) {
				// java system
				case AMSupport.JAVA_STRING: 
					 system = new JavaSystem(rs.getString(1), sysType,rs.getString(3),rs.getString(4), rs.getString(5));
					break;
				// html5 system
				case AMSupport.HTML5_STRING:
					system = new HTML5System(rs.getString(1), sysType,rs.getString(3),rs.getString(4), rs.getString(5));
					break;
				// database system
				case AMSupport.DATABASE_STRING:
					system = new DatabaseSystem(rs.getString(1), sysType,rs.getString(3),rs.getString(4), rs.getString(5));
					break;
				default:
					system = new JavaSystem(rs.getString(1), sysType,rs.getString(3),rs.getString(4), rs.getString(5));
					break;
				}
				
				systems.add(system);
		      }
		    } catch (SQLException e) {
		    	// if some kind of exception happened then just regenerate the systems
				 
				 // now drop and create the tables to have a fresh start
				 dropAndCreateTables();
				 
				 // throw systems recovery exception to let main know to start fresh
		    	throw new SystemsRecoveryException();
			}
		 
		 // get the system metrics for systems in the list
		 for (System sys : systems)
		 {
			 // lookup and add metrics to the system
			 // only get the metric with the latest timestamp
			 // this will get the latest state
			 try (PreparedStatement stmt2 = getConnection().prepareStatement(getMetricsString)) {
				 stmt2.setString(1, sys.getId());
				 ResultSet rs2 = stmt2.executeQuery();
				 while (rs2.next())
				 {
					 sys.addMetric(new Metric(rs2.getString(1), rs2.getLong(4),rs2.getString(9),rs2.getInt(5), rs2.getInt(6), rs2.getLong(7), rs2.getString(8), rs2.getInt(10), rs2.getInt(11)));						 
				 }
				 
			 }
			 catch(SQLException e)
			 {
				 // if there was an error getting the metrics then regenerate the systems
				 java.lang.System.out.println("Error getting metrics..." + e);
				 
				 // now drop and create the tables to have a fresh start
				 dropAndCreateTables();
				 
				 // throw systems recovery exception to let main know to start fresh
				 throw new SystemsRecoveryException();
			 }
			 
		 }
		 return systems;
	 }
	 
	 public static void dropAndCreateTables() {
		 try {
			 // drop the system table
			 Statement stmt = getConnection().createStatement();
			 stmt.execute(dropSystemTable);
			 
			 // drop the system state table
		     stmt = getConnection().createStatement();
			 stmt.execute(dropSystemStateTable);
		    
			 // drop the metrics table
			 stmt = getConnection().createStatement();
			 stmt.execute(dropMetricsTable);
			 
			 // create the tables
			 stmt = getConnection().createStatement();
			 stmt.execute(addSystemTable);
			 
			 // drop the system state table
		     stmt = getConnection().createStatement();
			 stmt.execute(addSystemStateTable);
		    
			 // drop the metrics table
			 stmt = getConnection().createStatement();
			 stmt.execute(addMetricTable);
			 
		    } catch (SQLException e) {
		    	java.lang.System.out.println("Error dropping the table." + e);
			}
	 }
	 
}
