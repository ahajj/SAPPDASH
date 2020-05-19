package com.appmonitor.systems;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.appmonitor.AppMonitorMain;
import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;
import com.appmonitor.systems.processes.Process;

public class JavaSystemTest {

	@Test
	void testGetSystemHealthWithByProcess() {
		
		// create the JavaSystem
		
		JavaSystem js = new JavaSystem("test1", "test2",AMSupport.OK_STATE, "test4");
		
		// add one process at a time to control the test

		js.addProcess(new Process("test1"+"_process1", new ArrayList<Metric>()));
		js.getProcesses().get(0).setState(AMSupport.OK_STATE);
		
		// having a process with OK_STATE and no metrics should generate the status based on the state of the system
		// in this case, an ok state should mean the system is healthy
		Assert.assertTrue("System status was instead: " + js.getSystemHealth(), js.getSystemHealth().equals(AMSupport.HEALTHY_STATUS));
	
		// now test with a Warning process

		js.addProcess(new Process("test1"+"_process1", new ArrayList<Metric>()));
		js.getProcesses().get(0).setState(AMSupport.WARNING_STATE);
		// having a process with WARNING_STATE and no metrics should return a unhealthy status
		Assert.assertTrue("System status was instead: " + js.getSystemHealth(), js.getSystemHealth().equals(AMSupport.UNHEALTHY_STATUS));
	
		// now test with a Critical process

		js.addProcess(new Process("test1"+"_process1", new ArrayList<Metric>()));
		js.getProcesses().get(0).setState(AMSupport.CRITICAL_STATE);
		// having a process with CRITICAL_STATE and no metrics should return a restart status
		
		Assert.assertTrue("System status was instead: " + js.getSystemHealth(), js.getSystemHealth().equals(AMSupport.RESTART_STATUS));
	
	}
	
	
}
