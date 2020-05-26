package com.appmonitor.support;

import java.util.ArrayList;
import java.util.List;

import com.appmonitor.AppMonitorMain;
import com.appmonitor.systems.System;
import com.appmonitor.systems.metrics.Metric;

public class AMSupportNoJUnit {

	// use this class to test I/O
	// Specifically, test that we can test the following Goals:
	// 1. Erase the files
	// 2. throw a SystemsRecoveryException if unable to read back all the systems
	// 3. Write to the log file
	// 4. Write all the systems to a file
	// 5. Read back all the systems to a file
	// 6: The read back systems match the systems in memory
	
	private static final int numberOfSystems = 3;
	
	public static void main(String[] args)
	{
		

		// Test Goal 1:
		// Erase the Systems file		
		AMSupport.clearSystemsFile();
		
		// Erase the Log file
		AMSupport.clearLogFile();
		
		List<System> systems  = new ArrayList<System>();
		
		// Test Goal 2:
		// try reading systems from the file...this should result in SystemsRecoveryException
		try
		{
			systems = AMSupport.readSystemsFromFile();
			java.lang.System.out.println("Systems were read from the file - this is should not have happened!");
			java.lang.System.out.println("Failed Test!");
			return;
		}
		catch(SystemsRecoveryException sre)
		{
			// We should enter the catch.  Now generate a list of systems
			
			systems = AppMonitorMain.generateListOfSystems(numberOfSystems);
		}
		
		// Now let's simulate a metric refresh
		AppMonitorMain.trackSystems(systems);
		
		

		// Test Goal 3:
		// this should have printed nice text to the console
		// as well as logged information to the log file
		// so the next step is to ensure the log isn't empty
		
		java.lang.System.out.println("Checking if the log is empty...");
		
		if (AMSupport.isFileEmpty(AMSupport.LOG_FILE))
		{
			java.lang.System.out.println("File was still empty!  Test Failed!");
			return;
		}
		java.lang.System.out.println("Log was not empty");
		

		// Test Goal 4:
		// next, check that the Systems file isn't empty
		java.lang.System.out.println("Checking if the system file is empty...");
		
		if (AMSupport.isFileEmpty(AMSupport.SYSTEM_BACKUP_FILE))
		{
			java.lang.System.out.println("File was still empty!  Test Failed!");
			return;
		}
		java.lang.System.out.println("System file was not empty");
		
		// next, lets track the System 4 more times
		// this should provide each Metric with 5 data points
		AppMonitorMain.trackSystems(systems);
		AppMonitorMain.trackSystems(systems);
		AppMonitorMain.trackSystems(systems);
		AppMonitorMain.trackSystems(systems);
		

		// Test Goal 5:
		// The next step is read the list of systems from the file
		// and then compare the systems in the file with the actual list of systems
		// to ensure that they are the same
		List<System> fileSystems = new ArrayList<System>();
		try {
			fileSystems = AMSupport.readSystemsFromFile();
		} catch (SystemsRecoveryException e) {
			java.lang.System.out.println("Systems could not be read from the file - this is should not have happened!");
			java.lang.System.out.println("Failed Test!");
		}
		
		// Test Goal 6:
		// we will test just the first metric in each system.
		// if the first metric is the same, then so should the rest
		
		for (int c = 0; c < systems.size(); c++)
		{
			Metric firstSystemMetric = systems.get(c).getMetrics().get(0);
			Metric firstFileSystemMetric = fileSystems.get(c).getMetrics().get(0);
			
			// make sure they have the same names
			if (!(firstSystemMetric.getName().equals(firstFileSystemMetric.getName())))
			{
				java.lang.System.out.println("Metrics did not match!");
				java.lang.System.out.println("Failed Name Test!");
				java.lang.System.out.println("System Metric: " + firstSystemMetric);
				java.lang.System.out.println("File System Metric: " + firstFileSystemMetric);
				return;
			}
			if (firstSystemMetric.getValue() != firstFileSystemMetric.getValue())
			{
				java.lang.System.out.println("Metrics did not match!");
				java.lang.System.out.println("Failed Value Test!");
				java.lang.System.out.println("System Metric: " + firstSystemMetric);
				java.lang.System.out.println("File System Metric: " + firstFileSystemMetric);
				return;
			}
			if (firstSystemMetric.getValues().size() != firstFileSystemMetric.getValues().size())
			{
				java.lang.System.out.println("Metrics did not match!");
				java.lang.System.out.println("Failed Values Size Test!");
				java.lang.System.out.println("System Metric: " + firstSystemMetric + "\n" + firstSystemMetric.getValues());
				java.lang.System.out.println("File System Metric: " + firstFileSystemMetric + "\n" + firstFileSystemMetric.getValues());
				return;
			}	
			if (!firstSystemMetric.getAverageValue().equals(firstFileSystemMetric.getAverageValue()))
			{
				java.lang.System.out.println("Metrics did not match!");
				java.lang.System.out.println("Failed Average Test!");
				java.lang.System.out.println("System Metric: " + firstSystemMetric);
				java.lang.System.out.println("File System Metric: " + firstFileSystemMetric);
				java.lang.System.out.println("System Metric: " + firstSystemMetric + "\n" + firstSystemMetric.getValues() + "\n" + firstSystemMetric.getAverageValue());
				java.lang.System.out.println("File System Metric: " + firstFileSystemMetric + "\n" + firstFileSystemMetric.getValues() + "\n" + firstFileSystemMetric.getAverageValue());
				
				return;
			}			
			java.lang.System.out.println("System " + systems.get(c).getId() + " matched!");
			
		}
		
		
	}
	
}
