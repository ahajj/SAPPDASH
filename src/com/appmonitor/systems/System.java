package com.appmonitor.systems;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;


public abstract class System implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	// private members shared across all System types
	private String id;
	private String type;
	private String account;
	private String state;
	private List<Metric> metrics;
	private List<String> states;
	

	
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
		
		// add the state to the list of states
		addStateToStates(state);
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
		this.states = new ArrayList<String>();
	}
	
	public System(String type, String account, List<Metric> metrics) {
		super();
		this.setId(""); 
		this.type = type;
		this.account = account;
		this.metrics = metrics;
		this.states = new ArrayList<String>();
		this.generateState();
	}
	
	public System(String type, String account) {
		super();
		this.setId("");
		this.type = type;
		this.account = account;
		this.metrics = new ArrayList<Metric>();
		this.states = new ArrayList<String>();
	}
	
	// Default function to get the system health
	public String getSystemHealth() {
		
		String sysStatus = AMSupport.HEALTHY_STATUS;
		// run through the metrics
		for (Metric metric: this.getMetrics())
		{
			
			String metStatus = AMSupport.getStatusForState(metric.getState());
			if (metStatus.equals(AMSupport.UNHEALTHY_STATUS))
			{
				sysStatus = AMSupport.getStatusForState(metric.getState());
			}
			else if (metStatus.equals(AMSupport.UNKNOWN_STATUS) || metStatus.equals(AMSupport.RESTART_STATUS))
			{
				return AMSupport.getStatusForState(metric.getState());				
			}
			
		}
		
		if (!sysStatus.equals(AMSupport.HEALTHY_STATUS))
		{
			return sysStatus;
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
	
	
	// searches the systems metrics for the metric with the passed in name
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
	
	// Make these functions abstract so all system classes have to implement it.
	public abstract String generateSystemStats();
	public abstract void generateMetrics();

	// generate the state of the system based on the states of its metrics
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
				addStateToStates(state);
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
		
		// add the most recent state to the list of states
		addStateToStates(state);
	}
	
	protected void addStateToStates(String state)
	{
		// add the value to the values list
		states.add(state);
		
		// we only want to keep a history of the last 500 entries
		if (states.size() > AMSupport.HISTORY_LIMIT)
		{
			// this will remove the oldest value
			states.remove(0);
		}
	}
	
	public void removePreviousState()
	{
		if (states.size() > 0)
		{
			states.remove(states.size()-1);			
		}
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
	
	// Creates a nicer looking output in the console
	public void niceOutput()
	{
		//String niceOutput = "";
		java.lang.System.out.println(" _____________________________________");	
		java.lang.System.out.printf("/ %-35s \\ \n", id);		
		java.lang.System.out.println("|_____________________________________|______________________________________________________________________");
		java.lang.System.out.printf("|  Type: %34s |%54s|%8s|\n", type, "","");
		java.lang.System.out.printf("| Account: %32s |  %22s: %10s  %15s |%8s|\n", account, "Metric", "Value", "State","Average");
		java.lang.System.out.printf("| State: %34s |%54s|%8s|\n", state, "","");
		printStringWithMetricIndex("| # of Metrics: %27d |", metrics.size(),0);
		printStringWithMetricIndex("|    # Error: %29d |", countMetricsForState(AMSupport.ERROR_STATE), 1 );
		printStringWithMetricIndex("|    # Warning: %27d |", countMetricsForState(AMSupport.WARNING_STATE), 2 );
		printStringWithMetricIndex("|    # Ok: %32d |", countMetricsForState(AMSupport.OK_STATE), 3 );
		
		
	}
	
	// This function will check to see if there is a metric in the system list's index
	// and will print it in the second 'box' of the nice output
	// and will also print the average value for that metric
	public void printStringWithMetricIndex(String string, int firstParam, int mIndex)
	{
		if(metrics.size() > mIndex)
		{
			java.lang.System.out.printf(string + "  %22s: %3d %-10s - %10s |  %5.2f |\n", firstParam, metrics.get(mIndex).getName(), metrics.get(mIndex).getValue(), metrics.get(mIndex).getUnit(), metrics.get(mIndex).getState(), metrics.get(mIndex).getAverageValue() );
		}
		else
		{
			java.lang.System.out.printf(string + "%54s|%8s|\n" , firstParam, "","");
		}
	}
	
	// The bottomsection of the nice output
	public void niceOutputBottom() {

		java.lang.System.out.println("\\___________________________________________/\\_____________________________________________________/\\_______/");
	}
	
	// Run through all the metrics and count the number that have the given state
	public int countMetricsForState(String state)
	{
		int count = 0;
		for (Metric metric: metrics) {
			if (metric.getState().contentEquals(state))
			{
				count++;
			}
		}
		
		return count;
	}
	
	// calculates the percentage the metric was in the gven state
	public Double calcPerInState(String state)
	{
		
		return  AMSupport.calcPerInStateForStates(state, states);
	}
	
	// This function assigns a random value to the metric.
	// This simulates having a live system that constantly has the metric updating
	public void simulateUpdatingMetrics()
	{
		Random rand = new Random();
		// loop through all the metrics and update their values
		for (Metric metric: metrics)
		{
			metric.setValue(rand.nextInt(AMSupport.MAX_RAND_VALUE));
		}
	}

	// Search through the system metrics for ones with the passed in status
	public List<Metric> getMetricsForHealthStatus(String status)
	{
		List<Metric> metricsToGo = new ArrayList<Metric>();
		
		for(Metric metric : metrics)
		{
			if (AMSupport.getStatusForState(metric.getState()).equals(status))
			{
				metricsToGo.add(metric);
			}
		}
		
		// returns an empty list if there are no metrics
		return metricsToGo;
	}
	
	public void printProblemMetrics()
	{
		// we only want to append the 'title' if a metric with the problem status was found and only the first time
		boolean first = true;
		for (Metric metric: metrics)
		{
			if(metric.calcPerInState(AMSupport.RESTART_STATUS) > AMSupport.METRIC_ERROR_PER)
			{
				
				if (first) 
				{	
					// Print the title
					AMSupport.appendToLogFile("Metrics that have had errors over " + AMSupport.METRIC_ERROR_PER + "% of the time:  ");
					first = false;
				}
				
				metric.printHistoricalStateAnalysis();

			}
		}
		
		// if first is false then at least one error metric was found.
		if (!first)
		{
			// add a new line to the log for separation
			AMSupport.appendToLogFile("\n");			
		}
	}

}
