package com.appmonitor.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.appmonitor.systems.HTML5System;
import com.appmonitor.systems.System;
import com.appmonitor.systems.metrics.Metric;

public class HistoricalAnalyzerTest {
	

	// the other functions all are void and used to write to the log and/or console
	// they also call other functions in Metric & System
	
	@Test
	public void testAnalyzer() {
		
		// Create a new system
		
		// add some metrics
		List<Metric> metrics = new ArrayList<Metric>();
		
		metrics.add(new Metric("testMetric1", 45l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric2", 51l, "%", 50, 60, 32423424l, "rate", 20,60));
		
		// Create a HTML5Ssystem for this test since it just overrides the abstract and toString() methods
		System testSystem = new HTML5System("testSystem1","testAccount", metrics, "testApplication");
		
		// skip the engine
		HistoricalAnalyzer hsAnalyzer = new HistoricalAnalyzer();
		String stats = hsAnalyzer.generateHistoricalStats(testSystem);
		
		Assert.assertTrue("The generated stats string was actually " + stats, stats.equals("Historical Analysis Breakdown:\n" + 
				"	Healthy 0.00%  | Unhealthy 100.00%  | Restart Recommended 0.00% "));
		
	}

}
