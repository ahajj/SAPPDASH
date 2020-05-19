package com.appmonitor.systems;

import java.util.ArrayList;
import java.util.List;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;

public abstract class System {

	// private members shared across all System types
	private String id;
	private String type;
	private String account;
	private String state;
	private List<Metric> metrics;
	

	
	// Setters & Getters
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<Metric> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
	
	public void addMetric(Metric metric) {
		this.metrics.add(metric);
	}
	// Constructors
	public System()
	{
		super();
		this.setId("");
		this.type = "";
		this.account = "";
		this.state = "";
		this.metrics = new ArrayList<Metric>();
	}
	
	public System(String type, String account, List<Metric> metrics) {
		super();
		this.setId(""); 
		this.type = type;
		this.account = account;
		this.metrics = metrics;
		this.generateState();
	}
	
	public System(String type, String account) {
		super();
		this.setId("");
		this.type = type;
		this.account = account;
		this.metrics = new ArrayList<Metric>();
	}
	
	// Default function to get the system health
	public String getSystemHealth() {
		
		// run through the metrics
		for (Metric metric: this.getMetrics())
		{
			if (!AMSupport.getStatusForState(metric.getState()).equals(AMSupport.HEALTHY_STATUS))
			{
				return AMSupport.getStatusForState(metric.getState());
			}
		}
		
		return AMSupport.getStatusForState(this.state);
	}
	
	
	protected String generateHeader() {
		String outputString = "";
		
		outputString += "Application: " + id;
		outputString += "\n		Account: " + account;
		outputString += "\n		State: " + state;
		outputString += "\n		Type: " + type;
		return outputString;
	}
	
	protected Metric getMetricNamed(String name)
	{
		for (Metric metric: metrics)
		{
			if (metric.getName().equals(name))
			{
				return metric;
			}
		}
		return null; // returns null if the metric is not found;
	}
	
	// Make this function abstract so all system classes have to implement it.
	public abstract String generateSystemStats();
	public abstract void generateMetrics();

	public void generateState() {
			
		String sysState = AMSupport.OK_STATE;
		
		// base the system state on the metric state
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
			// in case a metric further down the line has a more severe state
			else if (metricState.equals(AMSupport.WARNING_STATE))
			{
				sysState = metricState;			
			}
		}
		
		state = sysState;
	}
	
	@Override
	public String toString()
	{
		String outputString = generateHeader();
		if (!metrics.isEmpty())
		{
			outputString += "\n	Metrics: \n";
			
			for (Metric metric: metrics)
			{
				outputString += metric;
			}
		}
		return outputString;
	}
	

}
