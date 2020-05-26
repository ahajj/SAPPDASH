package com.appmonitor.systems;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;


public class SystemTest {

	System testSystem;
	
	@Before
	public void setupSystemsAndMetrics() 
	{
		List<Metric> metrics = new ArrayList<Metric>();
		
		metrics.add(new Metric("testMetric1", 45l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric2", 51l, "%", 50, 60, 32423424l, "rate", 20,60));
		
		// Create a HTML5Ssystem for this test since it just overrides the abstract and toString() methods
		testSystem = new HTML5System("testSystem1","testAccount", metrics, "testApplication");
	}
	
	// test getMetricNamed
	@Test
	public void testGettingAMetric()
	{
		
		// now try to find an existing metric 
		Metric myMetric = testSystem.getMetricNamed("testMetric2");
		
		Assert.assertTrue("The metric wasn't found!", !myMetric.equals(null) && myMetric.getName().equals("testMetric2"));
		
		// Now try to find a metric that doesn't exist and make sure it is null
		
		myMetric = testSystem.getMetricNamed("doesn't exist");
		
		Assert.assertNull(myMetric);
		
	}
	
	// test generateState
	@Test
	public void testGenerateSystemState() 
	{
		// test that the system state get generated from the metrics
		// ensure that the metrics are in an OK state
		
		List<Metric> metrics = testSystem.getMetrics();
		
		for (Metric metric: metrics)
		{
			metric.setValue(30l);
		}
		
		testSystem.setMetrics(metrics);
		
		testSystem.generateState();
		
		Assert.assertTrue("The system state was not OK...it was " + testSystem.getState(), testSystem.getState().equals(AMSupport.OK_STATE));
		
		// now change one of the metrics so that it is warning levels and make sure the system is in warning
		metrics.get(0).setValue(55);
		testSystem.generateState();
		
		Assert.assertTrue("The system state was not Warning...it was " + testSystem.getState(), testSystem.getState().equals(AMSupport.WARNING_STATE));

		// now change the other metric so that it is errorred and make sure the system is in error state
		metrics.get(1).setValue(65);
		testSystem.generateState();
		
		Assert.assertTrue("The system state was not Error...it was " + testSystem.getState(), testSystem.getState().equals(AMSupport.ERROR_STATE));
		
	}
	
	// test countMetricsForState
	@Test
	public void testCountMetricsForState()
	{
		// set the metrics back to an okay state
		List<Metric> metrics = testSystem.getMetrics();
		
		for (Metric metric: metrics)
		{
			metric.setValue(30l);
		}
		
		// now add in 3 warning state metrics
		metrics.add(new Metric("testMetric3", 51l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric4", 52l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric5", 53l, "%", 50, 60, 32423424l, "rate", 20,60));
		
		// now add in 5 error state metrics
		metrics.add(new Metric("testMetric6", 61l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric7", 62l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric8", 63l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric9", 64l, "%", 50, 60, 32423424l, "rate", 20,60));
		metrics.add(new Metric("testMetric10", 65l, "%", 50, 60, 32423424l, "rate", 20,60));
		
		testSystem.setMetrics(metrics);
		
		// now make sure the number of OK metrics is 2
		Assert.assertTrue("The actual number of OK systems is " + testSystem.countMetricsForState(AMSupport.OK_STATE), testSystem.countMetricsForState(AMSupport.OK_STATE) == 2);
		
		// make sure the number of Warning metrics is 3
		Assert.assertTrue("The actual number of Warning systems is " + testSystem.countMetricsForState(AMSupport.WARNING_STATE), testSystem.countMetricsForState(AMSupport.WARNING_STATE) == 3);
		
		// make sure the number of error metrics is 5
		Assert.assertTrue("The actual number of Error systems is " + testSystem.countMetricsForState(AMSupport.ERROR_STATE), testSystem.countMetricsForState(AMSupport.ERROR_STATE) == 5);
		
	}
	
}
