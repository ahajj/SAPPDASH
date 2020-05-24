package com.appmonitor.systems.processes;

import java.io.Serializable;
import java.util.List;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;

public class Process implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String processId;
	private List<Metric> metrics;
	private String state;
	

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Process(String processId, List<Metric> metrics) {
		super();
		this.processId = processId;
		this.metrics = metrics;
		this.generateStateBasedOnMetricStates();
	}
	public Process(String processId, String state, List<Metric> metrics) {
		super();
		this.processId = processId;
		this.metrics = metrics;
		this.state = state;
		this.generateStateBasedOnMetricStates();
	}	
	protected void generateStateBasedOnMetricStates()
	{
		
		String procState = AMSupport.OK_STATE;
		
		// base the process state on the metric state
		for (Metric metric : metrics)
		{
			String metricState = metric.getState();
			
			// set the process to critical/error/unknown if any metric is one of those states
			if (metricState.equals(AMSupport.CRITICAL_STATE) || metricState.equals(AMSupport.ERROR_STATE) || metricState.equals(AMSupport.UNKNOWN_STATE))
			{
				state = metricState;
				return;
			}
			// make note of metrics that are in warning state
			// we don't want to just set the process to that state
			// in case a process further down the line has a more severe state
			else if (metricState.equals(AMSupport.WARNING_STATE))
			{
				procState = metricState;			
			}
		}
		
		state = procState;
	}
	
	@Override
	public String toString()
	{
		String processString = "";
		
		processString += "\nProcess: " + processId;
		processString += "\n		State: " + state;
		
		if (!metrics.isEmpty())
		{
			processString += "\n		Process Metrics: \n";
			
			for (Metric metric: metrics)
			{
				processString += metric;
			}
		}
		return processString;
	}
}
