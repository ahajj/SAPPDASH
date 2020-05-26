package com.appmonitor.systems.processes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;


public class ProcessTest {

	// test generateStateBasedOnMetrics in order to make sure the process gets its state
	// from its metrics
	
	@Test
	public void testGenerateStateBasedOnMetrics() {
		// create some metrics
		List<Metric> metrics = new ArrayList<Metric>();
		
		
		metrics.add(new Metric("testMetric1", 45l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric2", 47l, "%", 50, 60, 32423424l, "rate", 20,60));
		
		// these metrics are all OK so test to make sure the process is okay
		Process process = new Process("testProcess", metrics);
		process.generateStateBasedOnMetricStates();
		
		Assert.assertTrue("Process is not OK state", process.getState().equals(AMSupport.OK_STATE));
		
		// now include a warning state metric
		metrics.add(new Metric("testMetric1", 55l, "%", 50, 60, 32423424l, "rate", 20,60));
		process = new Process("testProcess", metrics);
		process.generateStateBasedOnMetricStates();
		
		Assert.assertTrue("Process is not Warning state", process.getState().equals(AMSupport.WARNING_STATE));
	
		// now include a error state metric
		metrics.add(new Metric("testMetric1", 75l, "%", 50, 60, 32423424l, "rate", 20,60));
		process = new Process("testProcess", metrics);
		process.generateStateBasedOnMetricStates();
		
		Assert.assertTrue("Process is not Error state", process.getState().equals(AMSupport.ERROR_STATE));
		
	}
}
