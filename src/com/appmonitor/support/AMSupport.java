package com.appmonitor.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public abstract class AMSupport {
	
	
	// The following states and system statuses are public so that they can be used anywhere
	// and are final as they can not be changed
	
	// Various system states
	public static final String OK_STATE = "Ok";
	public static final String WARNING_STATE = "Warning";
	public static final String CRITICAL_STATE = "Critical";
	public static final String ERROR_STATE = "Error";
	public static final String UNKNOWN_STATE = "Unknown";
	
	// Various system health statuses
	public static final String HEALTHY_STATUS = "Healthy";
	public static final String UNHEALTHY_STATUS = "Unhealthy";
	public static final String RESTART_STATUS = "Restart Recommended";
	public static final String UNKNOWN_STATUS = "Unable to determine";
	
	// Different types of Metrics
	public static final String USED_DISC_SPACE = "Used Disc Space";
	public static final String REQ_PER_MIN = "Requests per Minute";
	public static final String CPU_LOAD = "CPU Load";
	public static final String DISK_IO = "Disk I/O";
	public static final String OS_MEM_USAGE = "OS Memory Usage";
	public static final String HEAP_MEM_USAGE = "Heap Memory Usage";
	public static final String AVG_RESP_TIME = "Average Response Time";
	public static final String BUSY_THREADS = "Busy Threads";
	public static final String HTML5_METRIC1 = "HTML5 Metric 1";
	public static final String HTML5_METRIC2 = "HTML5 Metric 2";
	
	// More types of Metrics for Processes
	public static final String MEMORY_USAGE = "Memory Usage";
	public static final String CPU_UTILIZATION = "CPU Utilization";
	
	// Different messages about metrics
	public static final String WITHIN_RANGE = "Metric Value in Acceptable Range.";
	public static final String WARNING_RANGE = "Warning! Metric Value is slightly above the Acceptable Range!";
	public static final String ERROR_RANGE = "Error! Metric Value is greatly above the Acceptable Range!";
	
	// Different analysis metrics
	public static final String ERROR_PERCENT = "Metric '%s' has errored out %.02f%% of the time.";
	public static final String WARNING_PERCENT = "Metric '%s' has had warning %.02f%% of the time.";
	
	// Different type values
	public static final String RATE = "rate";
	public static final String PERFORMANCE = "performance";
	public static final String requests = "requests";
	
	// The Warning and Error Thresholds
	public static final int WARNING_THRESHOLD = 90;
	public static final int ERROR_THRESHOLD = 95;
	public static final int HTML5_WARNING_THRESHOLD = 20;
	public static final int HTML5_ERROR_THRESHOLD = 30;
	
	// The max possible value in the random function for metrics
	public static final int MAX_RAND_VALUE = 101;
	public static final int MAX_RAND_VALUE_HTML5 = 40;
	
	// A baseline ms since epoch.  This will help make the timestamps make more sense
	public static final long MIN_SEC_EPOCH = 1589854769496l;
	
	// max number of ms from MIN_SEC_EPOCH 
	public static final int MAX_MS = 3500000;
	
	// number of milliseconds between refreshs
	public static final int MS_PER_REFRESH = 1000;
	
	// history threshold
	public static final int HISTORY_LIMIT = 500;
	
	// threshold for a metric percent in error state
	public static final Double METRIC_ERROR_PER = 35.0;
	
	// numberic system types
	public static final int JAVA = 0;
	public static final int HTML5 = 1;
	public static final int DATABASE = 2;
	
	// the following are names of files used
	public static final String LOG_FILE = "ApplicationMonitorLog.log";
	public static final String SYSTEM_BACKUP_FILE = "SystemBackup.dat";
	
	public static String getStatusForState(String state) {
		switch (state) {
		case OK_STATE: 
			return HEALTHY_STATUS;
		case WARNING_STATE: 
			return UNHEALTHY_STATUS;
		case CRITICAL_STATE:
		case ERROR_STATE: 
			return RESTART_STATUS;
		default: 
			return UNKNOWN_STATUS;
			
		}
	}
	
	public static String getMessageForValueWithThresholds(double value, int warningThreshold, int errorThreshold)
	{
		if (value >= errorThreshold)
		{
			return ERROR_RANGE;
		}
		else if (value >= warningThreshold)
		{
			return WARNING_RANGE;
		}
		return WITHIN_RANGE;
	}
	
	// Function to append the given string the log file
	public static void appendToLogFile(String string) { 
		
		// add a timestamp to the long line
	    Date date = new Date();
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(LOG_FILE, true)); 
			bw.write("INFO [" + format.format(date) + "]: " + string);  
			bw.newLine(); 
			bw.flush();
		} 
		catch (IOException e) {
			System.out.println("Error writing to the log file...");
			e.printStackTrace();
		} 
		finally { 
			if (bw != null) 
				try {
					bw.close();
				} 
			catch (IOException e) {
				e.printStackTrace();
			}
		} 
	} 
	
	// function to backup the list of systems
	public static void writeSystemsToFile(List<com.appmonitor.systems.System> systems)
	{
	    FileOutputStream file = null;
	    ObjectOutputStream out = null; 
		
		try {
		    file = new FileOutputStream(SYSTEM_BACKUP_FILE);
		    out = new ObjectOutputStream(file);  
		    
		    // write the list of systems to the output file
		    out.writeObject(systems); 
		} catch(Exception ex) {
		    ex.printStackTrace();
		}
		finally {
        	// close both output streams in the finally block assumming they are not null
			if (out != null) 
				try {
					out.close();
				} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			if (file != null) 
				try {
					file.close();
				} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// read the list of systems from the backedup file
	public static List<com.appmonitor.systems.System> readSystemsFromFile() throws SystemsRecoveryException
	{
		List<com.appmonitor.systems.System> systems = new ArrayList<com.appmonitor.systems.System>();
	       
		// try reading the file...
		// if it isn't there then we need to generate one.
		
		FileInputStream file = null;
		ObjectInputStream in = null;
        try
        {    
            // Create the file input stream
            file = new FileInputStream(SYSTEM_BACKUP_FILE); 
            in = new ObjectInputStream(file); 
              
            // read the list of systems
            systems = (List<com.appmonitor.systems.System>)in.readObject(); 
              
        } 
        // throw a SystemsRecoveryException if the file is not found, or the an IOException occures
        // this will signal the caller to create a list of systems
        catch (FileNotFoundException fnfe)
        {
        	throw new SystemsRecoveryException();
        }
          
        catch(IOException ex) 
        { 
        	throw new SystemsRecoveryException();
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            java.lang.System.out.println("ClassNotFoundException is caught"); 
        } 
        finally
        {
        	// close both input streams in the finally block assumming they are not null
			if (in != null) 
				try {
					in.close();
				} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			if (file != null) 
				try {
					file.close();
				} 
			catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        // if there are no systems recovered
        // then also throw SystemsRecoveryException()
        // so the application knows to generate them
        if (systems.size() == 0)
        {
        	throw new SystemsRecoveryException();
        }
        
        return systems;
	}
	
	// clear the log file
	 public static void clearLogFile() {
		 
		 eraseContentsOf(LOG_FILE);
	 }
	 
	 public static void clearSystemsFile() {
		 eraseContentsOf(SYSTEM_BACKUP_FILE);
	 }
	 
	 // check if the passed in file is empty
	 // used for testing purposes
	 public static boolean isFileEmpty(String file) {
		File newFile = new File(file); 
		if (newFile.length() == 0)
		{
			 return true; 
		}
		return false;
	 }
	 
	 private static void eraseContentsOf(String aFileName) {
		 
		 PrintWriter writer;
		 try {
			 writer = new PrintWriter(new File(aFileName));
			 writer.print("");
			 writer.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	 }
	 
	 // Calculates the % the passed in state appears in the list of states
	 // useful for checking to see the percentage of refreshes a system/metric was in an error/warning/okay state
	 public static Double calcPerInStateForStates(String state, List<String> states)
	 {
			// return 0 if there are no states
			if (states.size() == 0)
			{
				return 0d;
			}
			
			int count = 0;
			
			for (String sysState: states) {
				if (AMSupport.getStatusForState(sysState).equals(state))
				{
					count++;
				}
			}
			
			return  (((double)count*100)/states.size());
			
	 }

}
