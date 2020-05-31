package com.appmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.appmonitor.analyzer.AnalyzingEngine;
import com.appmonitor.analyzer.CurrentStateAnalyzer;
import com.appmonitor.analyzer.HistoricalAnalyzer;
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
	
	private static Scanner reader = new Scanner(java.lang.System.in);

	// main function for the app monitor
	public static void main(String[] args) {
				
		// First, clear the log so it is fresh for each runtime.
		AMSupport.clearLogFile();
		
		// check if the system should be run in continuous mod
		if (promptAndCheckIfContinuousMode())
		{
			beginContinuousMode();
		}
		// otherwise it is sandbox mode
		// prompt the user for the number of each system 
		// then create those systems 
		else {
			 //Scanner reader = new Scanner(java.lang.System.in);
			// reader.nextLine();
			 String answer = reader.nextLine();
			 java.lang.System.out.println("\nYou have selected--->" + answer);
			 reader.close();
			 
			 StringTokenizer answerTokens = new StringTokenizer(answer);
			 ArrayList<Integer> answerReturn = new ArrayList<Integer>();
			 while (answerTokens.hasMoreTokens()){
				 answerReturn.add(new Integer(answerTokens.nextToken()));
			 }
			 
			 // answerReturn should have 4 integers
			 List<System> systems = new ArrayList<System>();
			 
			 for (int i = 0; i < 3; i++)
			 {
				 // create the java (i=0), html5 (i=1) and database (i=2) systems
				 systems.addAll(createSystemsList(i, answerReturn.get(i)));
			 }
			 
			 // a bit strange to have two different ways to run the refresh
			 // but given the point of this class is not thread sleeps/scheduled executor service
			 // using both for different circumstances is easiest
			 // ex: SES for continuous refreshing...thread.sleep for a given number
			 for (int c =0; c< answerReturn.get(3); c++)
			 {
				 trackSystems(systems);
				 try {
					Thread.sleep(AMSupport.MS_PER_REFRESH);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
	}
	
	public static void beginContinuousMode()
	{
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
		
		// Create the AnalyzerEngines
		AnalyzingEngine<CurrentStateAnalyzer> curStateAnalyzer = new AnalyzingEngine<CurrentStateAnalyzer>();
		curStateAnalyzer.setAnalyzerSet(new CurrentStateAnalyzer());
		
		// Create the Historical Analyzer
		AnalyzingEngine<HistoricalAnalyzer> hisAnalyzer = new AnalyzingEngine<HistoricalAnalyzer>();
		hisAnalyzer.setAnalyzerSet(new HistoricalAnalyzer());
		
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
			
			// Analyze the current system state
			curStateAnalyzer.analyze(system);
			
			// Analyze the historical system states
			hisAnalyzer.analyze(system);
			
		}
		
		// This will 'backup' the systems
		// next time AppMonitorMain runs it will try to recover them
		// Assignment 2 - Post Conditiion 2: Serialize Systems
		AMSupport.writeSystemsToFile(systems);
	}
	
	public static boolean promptAndCheckIfContinuousMode()
	{
		// prompt the user to select either full on monitor mode
		
		// or sandbox mode
		
		 java.lang.System.out.print("Welcome to SAPPDASH! \nCurrently two modes are supported...\nContinuous will generate/recover systems and will run until the application is closed\nSandbox will allow you to input systems and time to monitor.");
		 
		 // just keep looping until valid input is received
		 while(true)
		 {
			 java.lang.System.out.print("Please select the sytem mode - [C]ontinuous or [S]andbox:  ");
			// Scanner reader = new Scanner(java.lang.System.in);
			 String answer = reader.nextLine();
			 java.lang.System.out.print("You have selected--->" + answer);
			 
			 switch (answer) {
			 case("c"):
			 case("C"):
				 java.lang.System.out.print("Beginning Continuous mode!");
				 reader.close();
			 	 return true;
			 case("s"):
			 case("S"):
				 java.lang.System.out.print("Beginning Sandbox Mode:\n Please enter the number of each system you would like to generate followed by the number of refreshes you would like to run.\n For example, creating 3 Java systems, 2 HTML5 systems and 1 Database system to be updated 45 times would look like '3 2 1 45' : ");
				return false;
			 }	 
		 }

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
			

			
			systems.add(createSystem(c));
		}
		
		return systems;
	}
	
	public static System createSystem(int sysType)
	{
		System system;
		
		switch (sysType%3) {
		// java system
		case AMSupport.JAVA: 
			system = new JavaSystem("java","account"+ sysType,"applicationName"+sysType);
			break;
		// html5 system
		case AMSupport.HTML5:
			system = new HTML5System("html5","account"+ sysType,"applicationName"+sysType);
			break;
		// database system
		case AMSupport.DATABASE:
			system = new DatabaseSystem("database","account"+ sysType,"applicationName"+sysType);
			break;
		default:
			system = new JavaSystem("java","account"+ sysType,"applicationName"+sysType);
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
		
		return system;
	}
	
	public static List<System> createSystemsList(int sysType, int numberOfSystems)
	{
		List<System> systems = new ArrayList<System>();
		
		for (int i = 0; i < numberOfSystems; i++)
		{
			systems.add(createSystem(sysType));
		}
		
		return systems;
	}

}
