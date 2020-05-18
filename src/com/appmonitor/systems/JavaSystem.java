package com.appmonitor.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;
import com.appmonitor.systems.processes.Process;


public class JavaSystem extends System {
	
	private String application;
	private List<Process> processes;
	
	private final int MAX_NUMBER_OF_PROCESSES = 3;

	public JavaSystem(String type, String account, String state, String application) {
		super(type, account, state);
		this.setId(type+account+application);
		this.application = application;
		this.processes = new ArrayList<Process>();
		
	}
	
	public JavaSystem(String type, String account, String state, List<Metric> metrics, String application) {
		super(type, account, state, metrics);
		this.setId(type+account+application);
		this.application = application;
		this.processes = new ArrayList<Process>();
		
	}
	
	
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public List<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

	public void addProcess(Process process) {
		this.processes.add(process);
	}
	
	@Override
	public String getSystemHealth() {
		
		// check each individual process to see if there is any unhealthy one
		// if there are any processes with unhealthy metrics then return the recommended status
		for (Process process: this.getProcesses())
		{
			if (!AMSupport.getStatusForState(process.getState()).equals(AMSupport.HEALTHY_STATUS))
			{
				// need to add additional checks to this to check for more severe process states before returning
				return AMSupport.getStatusForState(process.getState());
			}
		}
		
		// use the generic get system health if there are no processes
		return super.getSystemHealth();
	}

	@Override
	public String generateSystemStats() {
		String outputString = "Java " + super.toString();
		
		if (!processes.isEmpty())
		{
			outputString += "		Application Processes: \n";
			
			for (Process process: processes)
			{
				outputString += process;
			}
		}
		
		return outputString;
	}
	
	public String generateSimpleSystemStats() {
		String outputString = "Java " + generateHeader();
		
		outputString += "\n		Average Response Time: " + ((getMetricNamed(AMSupport.REQ_PER_MIN)) != null ? getMetricNamed(AMSupport.REQ_PER_MIN).getValue() : "N/A");
		
		return outputString;
	}
	
	public void generateProcesses() {
		
		// Generate a random number of processes up to MAX_NUMBER_OF_PROCESSES
		Random rand = new Random();
		
		int randInt = rand.nextInt(MAX_NUMBER_OF_PROCESSES);
		
		// make sure there is at least one process
		randInt = (randInt == 0) ? 1 : randInt;
		
		List<Metric> pMetrics = new ArrayList<Metric>();
		for (int i = 0; i<randInt; i++) {
			pMetrics = new ArrayList<Metric>();
			
			pMetrics.add(new Metric(AMSupport.USED_DISC_SPACE, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
			pMetrics.add(new Metric(AMSupport.MEMORY_USAGE, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
			pMetrics.add(new Metric(AMSupport.CPU_UTILIZATION, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));

			processes.add(new Process(getId()+"_process"+i, pMetrics, AMSupport.OK_STATE));
			
		}
	}

	@Override
	public void generateMetrics() {
		Random rand = new Random();
		// the standard generate metrics function will generate 8 metrics
		addMetric(new Metric(AMSupport.USED_DISC_SPACE, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.REQ_PER_MIN, AMSupport.OK_STATE, rand.nextInt(), "requests", 0, 0, rand.nextLong(), AMSupport.PERFORMANCE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.CPU_LOAD, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.DISK_IO, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.OS_MEM_USAGE, AMSupport.OK_STATE,  rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.HEAP_MEM_USAGE, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.AVG_RESP_TIME, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "ms", 0, 0, rand.nextLong(), AMSupport.PERFORMANCE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.BUSY_THREADS, AMSupport.OK_STATE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
	}
	
	
	
}
