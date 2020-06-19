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
	
	private static final String insertSystemString = "INSERT INTO SYSTEM(ID, TYPE, ACCOUNT, APPLICATION, STATE) VALUES(?, ?, ?, ?, ?)";
	private static final String updateSystemString = "UPDATE SYSTEM SET STATE = ? WHERE ID = ?";
	private static final String insertMetricString = "INSERT INTO METRIC(NAME, SYSTEM_ID, STATE, METRIC_VALUE, WARNING_THRESHOLD, ERROR_THRESHOLD, TIMESTAMP, METRIC_TYPE, MIN, MAX) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String getSystemsString = "SELECT * FROM SYSTEM A, METRIC B WHERE A.ID = B.SYSTEM_ID";

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
			// first try and update the system
		     try (PreparedStatement pstmt = conn.prepareStatement(updateSystemString)) {
		      pstmt.setString(1, system.getState());
		      pstmt.setString(2, system.getId());

		      pstmt.executeUpdate();
		    } catch (SQLException e) {

		    	java.lang.System.out.println("Error writing System to the database..." + e);
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
		    		 pstmt.setLong(9, metric.getMin());
		    		 pstmt.setLong(10, metric.getMax());
		    		 
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

	      pstmt.setString(5, system.getState());

	      pstmt.executeUpdate();
	    } catch (SQLException e) {

	    	java.lang.System.out.println("Error writing System to the database..." + e);
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
	    		 pstmt.setLong(9, metric.getMin());
	    		 pstmt.setLong(10, metric.getMax());
	    		 
	    		 pstmt.executeUpdate();
	    	 } catch (SQLException e) {
	    		 
	    		 java.lang.System.out.println("Error writing Metric to the database..." + e);
	    	 }
	    	 
	     }
	 }
	 
	 public static List<System> getSystems()
	 {
		 // need to get systems and metrics
		 List<System> systems =new ArrayList<System>();

		 try (Statement stmt = getConnection().createStatement();
		         ResultSet rs = stmt.executeQuery(getSystemsString)) {
		      while (rs.next()) {
		    	//System system = 
		        java.lang.System.out.printf("%d\t%‐10s\t%‐10s\t%tD%n",  
		            rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4));
		      }
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return systems;
	 }
	 
}
