package com.appmonitor.systems.metrics;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.appmonitor.support.AMSupport;

public class Metric {

	private String name;
	private String state;
	private long value;
	private String unit;
	private int warningThreshold;
	private int errorThreshold;
	private long timestamp;
	private String metricType;
	private int min;
	private int max;
	
	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	public long getValue() {
		return value;
	}
	
	public Metric(String name, String state, long value, String unit, int warningThreshold, int errorThreshold,
			long timestamp, String metricType, int min, int max) {
		super();
		this.name = name;
		this.state = state;
		this.value = value;
		this.unit = unit;
		this.warningThreshold = warningThreshold;
		this.errorThreshold = errorThreshold;
		this.timestamp = timestamp;
		this.metricType = metricType;
		this.min = min;
		this.max = max;
	}
	
	// since this application is text based
	// include a nice function to stringify the Metric object
	@Override
	public String toString()
	{
		return "	Metric " + name + ": "
				+ "\n		State: " + state
				+ "\n		Value: " + value
				+ "\n		Unit: " + unit
				+ "\n		Warning Threshold: " + warningThreshold
				+ "\n		Error Threshold: " + errorThreshold
				+ "\n		Timestamp: " + convertTime(timestamp)
				+ "\n		Type: " + metricType
				+ "\n		Min: " + min
				+ "\n		Max: " + max
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
				+ ((name.equals(AMSupport.REQ_PER_MIN) || name.equals(AMSupport.AVG_RESP_TIME)) 
						? value + " " + unit + " " + name 
						: value + " " + unit);
		
		// Analyze the metric to see the value is under Error and Warning levels
		output += "\n	**** Analysis: " + ((warningThreshold == 0 && errorThreshold == 0) ? (value + " " + unit + " " + name) : AMSupport.getMessageForValueWithThresholds(value, warningThreshold, errorThreshold)) + "	****\n\n";
		
		return output;
	}
}