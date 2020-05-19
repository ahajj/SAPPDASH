package com.appmonitor;

import java.util.ArrayList;
import java.util.List;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.DatabaseSystem;
import com.appmonitor.systems.HTML5System;
import com.appmonitor.systems.JavaSystem;
import com.appmonitor.systems.System;

public class AppMonitorMain {
	
	// This sets the number of systems generated to 20.
	private final static int numberOfSystems = 20;

	public static void main(String[] args) {
		
		// main function for the app monitor
		
		// Post Condition 1: Generate the Systems
		List<System> systems = generateListOfSystems(numberOfSystems);
		
		// Post Condition 2: Analysis of each System is on the console
		for (System system: systems)
		{
			// Treat Java apps as special.  We only want to print out the full list of metrics if
			// the state is not OK...otherwise it will clutter the console
			if (system instanceof JavaSystem && system.getState().equals(AMSupport.OK_STATE))
			{
				java.lang.System.out.println(((JavaSystem) system).generateSimpleSystemStats());
			}
			else {
				java.lang.System.out.println(system.generateSystemStats());
			}
			
		}

	}

	// ideally, this would be a server querying systems it is set to monitor
	// for the scope of the project (and because my systems I'd use this for at work would be unreachable
	// by the facilitator) I've incorporated a function to generate i number of systems
	public static List<System> generateListOfSystems(int i)
	{
		List<System> systems = new ArrayList<System>();
		
		// this list isn't totally random.  It will cycle through java, html5 and database
		for (int c = 0; c < i; c++)
		{
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
			systems.add(system);
		}
		
		return systems;
	}

}
