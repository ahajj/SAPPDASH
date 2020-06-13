package com.appmonitor.runnable;

import com.appmonitor.AppMonitorMain;
import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.System;

public class RefreshRunnable implements Runnable {

	private static Object lockObj = new Object();
	private System system;
	private boolean continuous = true;
	private int numRunTimes = 0;
	
	// normal constructor means the system should be refreshed continuously
	public RefreshRunnable(System system) {
		this.system = system;
	}
	
	// this constructor passes in the number of times to refresh the system
	public RefreshRunnable(System system, int numRunTimes) {
		this.system = system;
		this.numRunTimes = numRunTimes;
		this.continuous = false;
	}
	
	@Override
	public void run() {
		
		// running will simulate an application refresh
		// the number of times to run the system is passed in when RefreshRunnable is created
		// or set to run continuously if it is not passed in
		int runs = 0;
    	while(((continuous) ? true : runs < numRunTimes))
    	{
			// this should be synchronized so that we don't have overlapping logs
    		// in the console or logs
	        synchronized (lockObj) 
	        {	
        		// track the system
        		AppMonitorMain.trackSystem(system);
        		
        		// increase the number of runs this system has gone
        		runs++;
        		
        		// display it in the console
        		java.lang.System.out.println("System refreshed " + runs + " times.");

	        } 
	        // now make the thread sleep until the next time it should get refreshed
    		try 
    		{
    			// make this thread sleep until the next track time
    			Thread.sleep(AMSupport.MS_PER_REFRESH); 
    		} 
    		catch(InterruptedException ex)
    		{
    			ex.printStackTrace();
    			Thread.currentThread().interrupt();
    		} 
    	}
	}
}
