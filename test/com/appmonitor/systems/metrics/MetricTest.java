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
	
	@Test
	public void testAveragingMetricValues() {
		testMetric.clearHistoricalValues();
		
		// get the current timestamp
		long metTimestamp = testMetric.getTimestamp();
		
		// add 11 values and ensure that the average is generated properly
		testMetric.setValue(40l);
		testMetric.setValue(41l);
		testMetric.setValue(42l);
		testMetric.setValue(43l);
		testMetric.setValue(44l);
		testMetric.setValue(45l);
		testMetric.setValue(46l);
		testMetric.setValue(47l);
		testMetric.setValue(48l);
		testMetric.setValue(49l);
		testMetric.setValue(50l);
		
		Assert.assertTrue("Average was actually: " + testMetric.getAverageValue(), testMetric.getAverageValue() == 45l);
		
		// now ensure that the timestamp is being updated properly
		long curTimestamp = testMetric.getTimestamp();
		
		// the delta should be 11 * the refresh rate
		long expDelta = AMSupport.MS_PER_REFRESH * 11;
		Assert.assertTrue("Delta timestamps: " + (curTimestamp - metTimestamp), (curTimestamp - metTimestamp) == expDelta);
		
	}
	
	
	
}
