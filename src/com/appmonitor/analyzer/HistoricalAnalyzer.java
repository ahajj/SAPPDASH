package com.appmonitor.analyzer;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.System;

public final class HistoricalAnalyzer extends AnalyzerSet {

	@Override
	public final void analyzeSystem(System system) {
		
		// Analyze the system by getting the metrics and examining their historical values
		
		
		// Print the system health to the console
		java.lang.System.out.println(generateHistoricalStats(system));

		// Log the system health and reason
		writeDetailsToLogForSysHealth(system);
	
	}
	
	// get average requests for java systems
	protected final String generateHistoricalStats(System system)
	{
		return "Historical Analysis Breakdown:\n	" + AMSupport.HEALTHY_STATUS + " " + String.format("%.02f", system.calcPerInState(AMSupport.HEALTHY_STATUS)) + "% "
				+ " | " + AMSupport.UNHEALTHY_STATUS + " " + String.format("%.02f", system.calcPerInState(AMSupport.UNHEALTHY_STATUS)) + "% "
				+ " | " + AMSupport.RESTART_STATUS + " " + String.format("%.02f", system.calcPerInState(AMSupport.RESTART_STATUS)) + "% ";
		
	}
	
	@Override
	protected final void writeDetailsToLogForSysHealth(System system) {
		

		AMSupport.appendToLogFile("Analyzing " +  system.getId() + "'s historical state...");
		system.printProblemMetrics();
		AMSupport.appendToLogFile("Finished Historical Analysis\n");

	}

}
