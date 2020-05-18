package com.appmonitor;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.DatabaseSystem;
import com.appmonitor.systems.HTML5System;
import com.appmonitor.systems.JavaSystem;
import com.appmonitor.systems.System;


class AppMonitorMainTest {

	@Test
	void testGenerateSystems() {
		
		// generate 5 systems and make sure 5 systems are generated
		
		List<System> generatedSystems = AppMonitorMain.generateListOfSystems(5);
		
		
		Assert.assertTrue(generatedSystems.size() == 5);
		
		// now check that it goes JavaSystem, HTML5System, DatabaseSystem, JavaSystem, HTML5System
		
		Assert.assertTrue(generatedSystems.get(0) instanceof JavaSystem);
		Assert.assertTrue(generatedSystems.get(1) instanceof HTML5System);
		Assert.assertTrue(generatedSystems.get(2) instanceof DatabaseSystem);
		Assert.assertTrue(generatedSystems.get(3) instanceof JavaSystem);
		Assert.assertTrue(generatedSystems.get(4) instanceof HTML5System);
	}
	
	@Test
	void testGenerateStateBasedOnNumber() {
		// test that generateStateBasedOnNumber returns the correct state for each number
		
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(0).equals(AMSupport.OK_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(1).equals(AMSupport.OK_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(2).equals(AMSupport.OK_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(3).equals(AMSupport.WARNING_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(4).equals(AMSupport.ERROR_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(5).equals(AMSupport.CRITICAL_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(6).equals(AMSupport.OK_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(7).equals(AMSupport.OK_STATE));
		Assert.assertTrue(AppMonitorMain.generateStateBasedOnNumber(8).equals(AMSupport.OK_STATE));

	}

}
