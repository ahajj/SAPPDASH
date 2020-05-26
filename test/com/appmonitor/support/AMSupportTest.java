package com.appmonitor.support;

import org.junit.Assert;
import org.junit.Test;

public class AMSupportTest {

	@Test
	public void testGetStatusForState()
	{
		// make sure the OK state gets a healthy status
		Assert.assertTrue(AMSupport.getStatusForState(AMSupport.OK_STATE).equals(AMSupport.HEALTHY_STATUS));
		
		// make sure the Warning state gets an unhealthy status
		Assert.assertTrue(AMSupport.getStatusForState(AMSupport.WARNING_STATE).equals(AMSupport.UNHEALTHY_STATUS));
		
		// make sure the Error state gets a restart status
		Assert.assertTrue(AMSupport.getStatusForState(AMSupport.ERROR_STATE).equals(AMSupport.RESTART_STATUS));
		
		// make sure the Critical state gets a restart status
		Assert.assertTrue(AMSupport.getStatusForState(AMSupport.CRITICAL_STATE).equals(AMSupport.RESTART_STATUS));
			
	}
	
	@Test
	public void testGenerateMessageForValueWithThresholdValues()
	{
		// test the error range
		Assert.assertTrue(AMSupport.getMessageForValueWithThresholds(80l, 40, 60).equals(AMSupport.ERROR_RANGE));
				
		// test the warning range
		Assert.assertTrue(AMSupport.getMessageForValueWithThresholds(50l, 40, 60).equals(AMSupport.WARNING_RANGE));
		
		// test the within threshold range
		Assert.assertTrue(AMSupport.getMessageForValueWithThresholds(30l, 40, 60).equals(AMSupport.WITHIN_RANGE));
		
	}
	
}
