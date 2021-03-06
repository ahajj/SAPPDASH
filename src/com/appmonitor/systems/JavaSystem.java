package com.appmonitor.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;
import com.appmonitor.systems.processes.Process;


public class JavaSystem extends System {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String application;
	private List<Process> processes;
	
	private final int MAX_NUMBER_OF_PROCESSES = 3;

	public JavaSystem(String type, String account, String application) {
		super(type, account);
		this.setId(type+account+application);
		this.application = application;
		this.processes = new ArrayList<Process>();
		
	}
	public JavaSystem(String id, String type, String account, String application, String state) {
		super(type, account);
		this.setId(id);
		this.application = application;
		this.setState(state);
		this.processes = new ArrayList<Process>();
		
	}
	
	public JavaSystem(String type, String account, List<Metric> metrics, String application) {
		super(type, account, metrics);
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
		
		String sysStatus = AMSupport.HEALTHY_STATUS;
		
		// if the process status is unknown or critical then return it right away
		// otherwise, if any one process is unhealthy then return that
		// finally, go to System.getSystemHealth() if all processes are healthy
		for (Process process: this.getProcesses())
		{
			String procStatus = AMSupport.getStatusForState(process.getState());
			if (procStatus.equals(AMSupport.UNHEALTHY_STATUS))
			{
				sysStatus = AMSupport.getStatusForState(process.getState());
			}
			else if (procStatus.equals(AMSupport.UNKNOWN_STATUS) || procStatus.equals(AMSupport.RESTART_STATUS))
			{
				return AMSupport.getStatusForState(process.getState());				
			}
		}
		
		if (!sysStatus.equals(AMSupport.HEALTHY_STATUS))
		{
			return sysStatus;
		}
		
		// use the generic get system health if there are no processes
		return super.getSystemHealth();
	}
	
	@Override
	public void generateState() {
		
		super.generateState();
		
		
		// can't get more severe than error or critical
		if (this.getState().equals(AMSupport.CRITICAL_STATE) || this.getState().equals(AMSupport.ERROR_STATE) || this.getState().equals(AMSupport.UNKNOWN_STATE))
		{
			return;
		}
		// remove the last state in the list of historical states
		// this is because generateState() in System.java pushes to the list
		// but JavaSystems do additional processing
		this.removePreviousState();
		
		String sysState = this.getState();
		
		// now we need to check the processes' states to see if there are any more severe than the metrics
		for (Process process : processes)
		{
			String procState = process.getState();
			
			// set the process to critical/error/unknown if any metric is one of those states
			if (procState.equals(AMSupport.CRITICAL_STATE) || procState.equals(AMSupport.ERROR_STATE) || procState.equals(AMSupport.UNKNOWN_STATE))
			{
				this.setState(procState);
				return;
			}
			// make note of metrics that are in warning state
			// we don't want to just set the process to that state
			// in case a process further down the line has a more severe state
			else if (procState.equals(AMSupport.WARNING_STATE))
			{
				sysState = procState;			
			}
		}
		
		this.setState(sysState);
		
	}

	// The function that will generate the complete information (including metrics and processes) about the java system
	@Override
	public String generateSystemStats() {
		
		if (this.getState().equals(AMSupport.OK_STATE))
		{
			return generateSimpleSystemStats();
		}
		
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
	
	// the function called to just show basic information about the java system
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
			
			pMetrics.add(new Metric(AMSupport.USED_DISC_SPACE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
			pMetrics.add(new Metric(AMSupport.MEMORY_USAGE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
			pMetrics.add(new Metric(AMSupport.CPU_UTILIZATION, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));

			processes.add(new Process(getId()+"_process"+i, pMetrics));
			
		}
	}

	@Override
	public void generateMetrics() {
		Random rand = new Random();
		// the standard generate metrics function will generate 8 metrics
		addMetric(new Metric(AMSupport.USED_DISC_SPACE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.REQ_PER_MIN, rand.nextInt(AMSupport.MAX_RAND_VALUE), "requests", 0, 0, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.PERFORMANCE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.CPU_LOAD, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.DISK_IO, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.OS_MEM_USAGE,  rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.HEAP_MEM_USAGE, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.AVG_RESP_TIME, rand.nextInt(AMSupport.MAX_RAND_VALUE), "ms", 0, 0, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.PERFORMANCE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.BUSY_THREADS, rand.nextInt(AMSupport.MAX_RAND_VALUE), "", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
	}
	
	@Override
	public void niceOutput() {
		super.niceOutput();
		printStringWithMetricIndex("| # of Processes: %25d |", processes.size(),4);
		printStringWithMetricIndex("|    # Error: %29d |", countProcessesWithState(AMSupport.ERROR_STATE),5);
		printStringWithMetricIndex("|    # Warning: %27d |", countProcessesWithState(AMSupport.WARNING_STATE),6);
		printStringWithMetricIndex("|    # Ok: %32d |", countProcessesWithState(AMSupport.OK_STATE),7);
		super.niceOutputBottom();
	}
	
	// Counts the number processes with the given state
	protected int countProcessesWithState(String state)
	{
		int count = 0;
		for (Process process: processes) {
			if (process.getState().contentEquals(state))
			{
				count++;
			}
		}
		
		return count;
	}
	
	
	
}
