package com.appmonitor.systems.processes;

import java.util.List;

import com.appmonitor.systems.metrics.Metric;

public class Process {

	private String processId;
	private List<Metric> metrics;
	private String state;
	

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Process(String processId, List<Metric> metrics, String state) {
		super();
		this.processId = processId;
		this.metrics = metrics;
		this.state = state;
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
