package com.appmonitor.systems.metrics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appmonitor.support.AMSupport;


public class MetricTest {

	Metric testMetric;		
	int errorThreshold = 75;
	int warningThreshold = 60;
	
	@Before
	public void setUpMetric() {
		
		// set up the metric for ok_state
		int value = 30;

		testMetric = new Metric("testMetric", value, "%", warningThreshold, errorThreshold, 33333333, "rate", 25, 40);
		
	}
	
	// Test Metric states are generated properly based on ther value and thresholds
	@Test
	public void testMetricStateGenerationOKStatus() {
		
		// the metric setup in @Before should have an ok status since the value isnt in the error or warning thresholds
		Assert.assertTrue("The metric state was actually: " + testMetric.getState(), testMetric.getState().equals(AMSupport.OK_STATE));
		
	}
	
	@Test
	public void testMetricStateGenerationWarningStatus() {
		testMetric.setValue(warningThreshold+1);
		
		// the metric should have a warning status since it is >= the warning threshold
		Assert.assertTrue("The metric state was actually: " + testMetric.getState(), testMetric.getState().equals(AMSupport.WARNING_STATE));
		
		// now put it at the actual threshold value to make sure that works as well
		testMetric.setValue(warningThreshold);
		Assert.assertTrue("The metric state was actually: " + testMetric.getState(), testMetric.getState().equals(AMSupport.WARNING_STATE));
		
			
	}
	
	@Test
	public void testMetricStateGenerationCriticalStatus() {
		testMetric.setValue(errorThreshold+1);
		
		// the metric should have a warning status since it is >= the warning threshold
		Assert.assertTrue("The metric state was actually: " + testMetric.getState(), testMetric.getState().equals(AMSupport.ERROR_STATE));
		
		// now put it at the actual threshold value to make sure that works as well
		testMetric.setValue(errorThreshold);
		Assert.assertTrue("The metric state was actually: " + testMetric.getState(), testMetric.getState().equals(AMSupport.ERROR_STATE));
		
			
	}
	
	
	
}
