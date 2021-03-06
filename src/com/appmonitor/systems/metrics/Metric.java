package com.appmonitor.systems.metrics;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.appmonitor.support.AMSupport;

public class Metric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String name;
	private String state;
	private long value;
	private List<Long> values;
	private String unit;
	private int warningThreshold;
	private int errorThreshold;
	private long timestamp;
	private String metricType;
	private int min;
	private int max;
	
	private List<String> states;
	
	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	public long getValue() {
		return value;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public int getWarningThreshold() {
		return warningThreshold;
	}

	public void setWarningThreshold(int warningThreshold) {
		this.warningThreshold = warningThreshold;
	}

	public int getErrorThreshold() {
		return errorThreshold;
	}

	public void setErrorThreshold(int errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public List<String> getStates() {
		return states;
	}

	public void setStates(List<String> states) {
		this.states = states;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setValues(List<Long> values) {
		this.values = values;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setValue(long value) {
		
		// add the value to the values list
		values.add(value);
		
		this.value = value;
		
		// we only want to keep a history of the last 500 entries
		if (values.size() > AMSupport.HISTORY_LIMIT)
		{
			// this will remove the oldest value
			values.remove(0);
		}
		
		// update the timestamp on the metric
		this.timestamp = timestamp + AMSupport.MS_PER_REFRESH;
		
		this.generateStateBasedOnValueAndThresholds();
	}
	
	public Metric(String name, long value, String unit, int warningThreshold, int errorThreshold,
			long timestamp, String metricType, int min, int max) {
		super();
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.warningThreshold = warningThreshold;
		this.errorThreshold = errorThreshold;
		this.timestamp = timestamp;
		this.metricType = metricType;
		this.min = min;
		this.max = max;
		
		this.values = new ArrayList<Long>();
		this.states = new ArrayList<String>();
		this.values.add(value);
		this.generateStateBasedOnValueAndThresholds();
	}
	
	protected void generateStateBasedOnValueAndThresholds()
	{
		
		// only generate a non ok_state if not Average Response Time or Requests per Minute
		// for now those do not have error or warning thresholds
		
		if (name.equals(AMSupport.AVG_RESP_TIME) || name.equals(AMSupport.REQ_PER_MIN))
		{
			state = AMSupport.OK_STATE;
			states.add(state);
			return;
		}
		
		// other metric types should look at the error and warning thresholds
		if (value >= errorThreshold)
		{
			state = AMSupport.ERROR_STATE;
		}
		else if (value >= warningThreshold)
		{
			state = AMSupport.WARNING_STATE;
		}
		else {
			state = AMSupport.OK_STATE;
		}
		states.add(state);
	}
	
	// since this application is text based
	// include a nice function to stringify the Metric object
	@Override
	public String toString()
	{
		return "	Metric '" + name + "': "
				+ "\n		State: " + state + "...Current Value: " + value + " " + unit
				+ "\n		Min Value: " + min + " " + unit + "...Max Value: " + max + " " + unit
				+ "\n		Warning Threshold: " + warningThreshold + " " + unit + "...Error Threshold: " + errorThreshold + " " + unit
				+ "\n		Timestamp: " + convertTime(timestamp)
				+ "\n		Output: " + generateOutput();
	}
	
	
	private String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
	    return format.format(date);
	}
	
	// the 'out put' is a system generated analysis of the metric
	// 
	private String generateOutput() {
		String output = "";
		
		// generate a little status blurb
		output += AMSupport.getStatusForState(state) + " - " 	
				+ "'" + name + "' at " + value + " " + unit +"\n";	
		return output;
	}
	
	public String generateNiceSummary() {
		// Analyze the metric to see the value is under Error and Warning levels
		return " * " + "'" + name + "' at " + value + " " + unit + " (W:" + warningThreshold + unit + "|E:" + errorThreshold + unit + ")";
				
	}

	public String getUnit() {
		// TODO Auto-generated method stub
		return unit;
	}
	
	public Double getAverageValue()
	{
		Double toGo = 0d;
		
		if (values.size() == 0)
		{
			return toGo;
		}
		
		for (Long value: values)
		{
			toGo += value;
		}
		
		return toGo/values.size();
	}
	
	// this function is only used in the unit tests
	protected void clearHistoricalValues()
	{
		this.values.clear();
	}
	
	public List<Long> getValues()
	{
		return values;
	}
	
	// calculates the percentage the metric was in the gven state
	public Double calcPerInState(String state)
	{
		
		return  AMSupport.calcPerInStateForStates(state, states);
	}
	
	public void printHistoricalStateAnalysis()
	{
		AMSupport.appendToLogFile(" * " + name + ": " + AMSupport.HEALTHY_STATUS + " " + String.format("%.02f", calcPerInState(AMSupport.HEALTHY_STATUS)) + "% "
				+ " | " + AMSupport.UNHEALTHY_STATUS + " " + String.format("%.02f", calcPerInState(AMSupport.UNHEALTHY_STATUS)) + "% "
				+ " | " + AMSupport.RESTART_STATUS + " " + String.format("%.02f", calcPerInState(AMSupport.RESTART_STATUS)) + "% ");
	}
}
