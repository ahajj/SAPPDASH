package com.appmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.appmonitor.support.AMSupport;
import com.appmonitor.support.SystemsRecoveryException;
import com.appmonitor.systems.DatabaseSystem;
import com.appmonitor.systems.HTML5System;
import com.appmonitor.systems.JavaSystem;
import com.appmonitor.systems.System;

public class AppMonitorMain {
	
	// This sets the number of systems generated to 6.
	private final static int numberOfSystems = 3;
	
	private static List<System> systems;

	// main function for the app monitor
	public static void main(String[] args) {
				
		// First, clear the log so it is fresh for each runtime.
		AMSupport.clearLogFile();
		
		// Assignment 2 - Post Condition 4 & 5: System Recovery and Handle New Systems (Exception Handling)
		try
		{
			// Attempt to read serialized systems from a file.
			// throws SystemsRecoveryException if the file is not found
			systems = AMSupport.readSystemsFromFile();			
		}
		catch(SystemsRecoveryException sre)
		{
			
			// if we've entered this section then there was a problem reading the systems from the file
			// instead we will generate a fresh list
			// and print the message
			sre.messageToConsole();
			
			systems = generateListOfSystems(numberOfSystems);
		}

		// This next bit is done to simulate getting status & metric value updates MS_PER_REFRESH milliseconds
		// it is a scheduled executor that runs trackSystems() which will set a new value on each metric
		// it also updates states
	    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	    executorService.scheduleAtFixedRate(AppMonitorMain::trackSystems, 0, AMSupport.MS_PER_REFRESH, TimeUnit.MILLISECONDS);
		
	}
	
	public static void trackSystems()
	{
		trackSystems(systems);
	}
	
	public static void trackSystems(List<System> systems)
	{

		java.lang.System.out.println("************************ Refreshing ************************");
		// Assignment 2 - Post Condition 1: Advanced Visual Monitoring
		for (System system: systems)
		{	
			
			// randomly generates a new value for each metric on the system
			// also will track up to the last 500 values per metric in order to keep a history
			// to use when calculating the average
			system.simulateUpdatingMetrics();
			
			// Generate the state of the system based on the metrics and processes (when applicable)
			system.generateState();
			
			// for this version of the application we are showing a nice version of the application to the console
			// then a more detailed, complete version will be shown in the log
			system.niceOutput();
			
			// Assignment 2 - Post Condition 3: Detailed Log file
			// Also log the full details of the system metrics & processes to the log file
			
			// Treat Java apps as special.  We only want to print out the full list of metrics if
			// the state is not OK...otherwise it will clutter the console
			if (system instanceof JavaSystem && system.getState().equals(AMSupport.OK_STATE))
			{
				AMSupport.appendToLogFile(((JavaSystem) system).generateSimpleSystemStats());
			}
			else {
				AMSupport.appendToLogFile(system.generateSystemStats());
			}
			
		}
		
		// This will 'backup' the systems
		// next time AppMonitorMain runs it will try to recover them
		// Assignment 2 - Post Conditiion 2: Serialize Systems
		AMSupport.writeSystemsToFile(systems);
	}

	// ideally, this would be a server querying systems it is set to monitor
	// for the scope of the project (and because my systems I'd use this for at work would be unreachable
	// by the facilitator) I've incorporated a function to generate i number of systems
	public static List<System> generateListOfSystems(int i)
	{
		List<System> systems = new ArrayList<System>();
		
		java.lang.System.out.println("Generating Systems...");
		
		// this list isn't totally random.  It will cycle through java, html5 and database
		for (int c = 0; c < i; c++)
		{
			// add some nice output while generating
			for (int k =0; k < 3; k++)
			{
				// slow things down a little...wait three seconds and print three *s to the console between systems
				java.lang.System.out.println("*");
				try {
					TimeUnit.MILLISECONDS.sleep(AMSupport.MS_PER_REFRESH);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			System system;
			
			switch (c%3) {
			// java system
			case 0: 
				system = new JavaSystem("java","account"+ c,"applicationName"+c);
				break;
			// html5 system
			case 1:
				system = new HTML5System("html5","account"+ c,"applicationName"+c);
				break;
			// database system
			case 2:
				system = new DatabaseSystem("database","account"+ c,"applicationName"+c);
				break;
			default:
				system = new JavaSystem("java","account"+ c,"applicationName"+c);
				break;
			}
			
			// Generate the metrics for each system
			// Polymorphism
			system.generateMetrics();
			
			// Generate the processes for Java systems
			// Downcasting
			if (system instanceof JavaSystem) {
				((JavaSystem) system).generateProcesses();
			}
			// Generate the state of the system based on the metrics and processes (when applicable)
			system.generateState();
			
			// Print to the console the newly created System!
			java.lang.System.out.println("Generated System: " + system.getId() + "!");
			
			systems.add(system);
		}
		
		return systems;
	}

}
