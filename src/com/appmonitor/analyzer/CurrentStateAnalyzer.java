package com.appmonitor.analyzer;

import java.util.ArrayList;
import java.util.List;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.System;
import com.appmonitor.systems.metrics.Metric;

public class CurrentStateAnalyzer extends AnalyzerSet {

	@Override
	public void analyzeSystem(System system) {
		
		// Analyze the system in its current state and print it to the console
		String sysHealth = system.getSystemHealth();
		
		// Print the system health to the console
		java.lang.System.out.println("Current Analysis: System Health and/or Recommended Action: " + sysHealth);

		// Log the system health and reason
		writeDetailsToLogForSysHealth(system);
	
	}
	
	@Override
	protected void writeDetailsToLogForSysHealth(System system) {
		
		// If provide the metrics causing the problems if the system is
		// Unhealthy or Restart Recommended
		String sysHealth = system.getSystemHealth();
		
		List<Metric> metrics = new ArrayList<Metric>();
		
		switch (sysHealth) {
		case (AMSupport.RESTART_STATUS):
			// do restart analysis here
			metrics = system.getMetricsForHealthStatus(AMSupport.RESTART_STATUS);
		
			AMSupport.appendToLogFile("The following Metrics have an " + AMSupport.ERROR_RANGE);
		
			for (Metric metric : metrics)
			{
				AMSupport.appendToLogFile(metric.generateNiceSummary());
			}
			// don't break the switch statement as we also want to include the warning metrics
		case (AMSupport.UNHEALTHY_STATUS):
			// do unhealthy analysis here
			metrics = system.getMetricsForHealthStatus(AMSupport.UNHEALTHY_STATUS);
		
			// break out if there are no unhealthy metrics. 
			// we would hit this when getting metrics for the restart (error state)
			// as there could be errored metrics without warning metrics
			if (metrics.size() == 0)
			{
				break;
			}
		
			AMSupport.appendToLogFile("The following Metrics have a " + AMSupport.WARNING_RANGE);
		
			for (Metric metric : metrics)
			{
				AMSupport.appendToLogFile(metric.generateNiceSummary());
			}
			break;
		default:
			// do healthy analysis here
			AMSupport.appendToLogFile("All metrics show the system is healthy.");
		}
		
		// add a new line to the log for separation
		AMSupport.appendToLogFile("\n");
	}

}
