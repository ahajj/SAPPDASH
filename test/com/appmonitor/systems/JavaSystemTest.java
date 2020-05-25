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
		
		JavaSystem js = new JavaSystem("test1", "test2", "test4");
		js.setState(AMSupport.OK_STATE);
		
		// add one process at a time to control the test

		js.addProcess(new Process("test1"+"_process1",AMSupport.OK_STATE, new ArrayList<Metric>()));
		
		// having a process with OK_STATE and no metrics should generate the status based on the state of the system
		// in this case, an ok state should mean the system is healthy
		Assert.assertTrue("System status was instead: " + js.getSystemHealth(), js.getSystemHealth().equals(AMSupport.HEALTHY_STATUS));
	
		// now test with a Warning process
		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(new Metric("metric1", 50l, "%", 45, 60, 23442433, "rate", 50, 70));
		js.addProcess(new Process("test1"+"_process1", metrics));
		// having a process with WARNING_STATE and a metric in warning state should return a Unhealthy system
		Assert.assertTrue("System status was instead: " + js.getSystemHealth(), js.getSystemHealth().equals(AMSupport.UNHEALTHY_STATUS));
	
		// now test with a Critical process
		List<Metric> metrics2 = new ArrayList<Metric>();
		metrics2.add(new Metric("metric2", 65l, "%", 45, 60, 23442433, "rate", 50, 70));
		js.addProcess(new Process("test1"+"_process1", metrics2));
		// having a process with CRITICAL_STATE and no metrics should return a restart status
		
		Assert.assertTrue("System status was instead: " + js.getSystemHealth(), js.getSystemHealth().equals(AMSupport.RESTART_STATUS));
	
	}
	
	@Test
	void testCountProcessState() {
		// create the JavaSystem
		
		JavaSystem js = new JavaSystem("test1", "test2", "test4");
		js.setState(AMSupport.OK_STATE);
		
		// add one process at a time to control the test

		js.addProcess(new Process("test1"+"_process1",AMSupport.OK_STATE, new ArrayList<Metric>()));
	
		// now add in two warning processes
		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(new Metric("metric1", 50l, "%", 45, 60, 23442433, "rate", 50, 70));
		js.addProcess(new Process("test1"+"_process1", metrics));
		js.addProcess(new Process("test1"+"_process5", metrics));

	
		// now add in three critical processes
		List<Metric> metrics2 = new ArrayList<Metric>();
		metrics2.add(new Metric("metric2", 65l, "%", 45, 60, 23442433, "rate", 50, 70));
		js.addProcess(new Process("test1"+"_process1", metrics2));

		js.addProcess(new Process("test1"+"_process3", metrics2));
		js.addProcess(new Process("test1"+"_process4", metrics2));
		
		// Check that there is 1 OK process
		Assert.assertTrue("There were actually " + js.countProcessesWithState(AMSupport.OK_STATE), js.countProcessesWithState(AMSupport.OK_STATE) == 1);
		
		// Check that there are 2 Warning processes
		Assert.assertTrue("There were actually " + js.countProcessesWithState(AMSupport.WARNING_STATE), js.countProcessesWithState(AMSupport.WARNING_STATE) == 2);
		// Check that there are 3 Error processes	
		Assert.assertTrue("There were actually " + js.countProcessesWithState(AMSupport.ERROR_STATE), js.countProcessesWithState(AMSupport.ERROR_STATE) == 3);
		
	}
	
	
}
