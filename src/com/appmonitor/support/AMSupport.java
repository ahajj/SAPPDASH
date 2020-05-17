package com.appmonitor.support;

public abstract class AMSupport {
	
	
	// The following states and system statuses are public so that they can be used anywhere
	// and are final as they can not be changed
	
	// Various system states
	public static final String OK_STATE = "Ok";
	public static final String WARNING_STATE = "Warning";
	public static final String CRITICAL_STATE = "Critical";
	public static final String ERROR_STATE = "Error";
	public static final String UNKNOWN_STATE = "Unknown";
	
	// Various system health statuses
	public static final String HEALTHY_STATUS = "Healthy";
	public static final String UNHEALTHY_STATUS = "Unhealthy";
	public static final String RESTART_STATUS = "Restart Recommended";
	public static final String UNKNOWN_STATUS = "Unable to determine";
	
	// Different types of Metrics
	public static final String USED_DISC_SPACE = "Used Disc Space";
	public static final String REQ_PER_MIN = "Requests per Minute";
	public static final String CPU_LOAD = "CPU Load";
	public static final String DISK_IO = "Disk I/O";
	public static final String OS_MEM_USAGE = "OS Memory Usage";
	public static final String HEAP_MEM_USAGE = "Heap Memory Usage";
	public static final String AVG_RESP_TIME = "Average Response Time";
	public static final String BUSY_THREADS = "Busy Threads";
	public static final String HTML5_METRIC1 = "HTML5 Metric 1";
	public static final String HTML5_METRIC2 = "HTML5 Metric 2";
	
	// More types of Metrics for Processes
	public static final String MEMORY_USAGE = "Memory Usage";
	public static final String CPU_UTILIZATION = "CPU Utilization";
	
	// Different messages about metrics
	public static final String WITHIN_RANGE = "Metric Value in Acceptable Range.";
	public static final String WARNING_RANGE = "Warning! Metric Value is slightly above the Acceptable Range!";
	public static final String ERROR_RANGE = "Error! Metric Value is greatly above the Acceptable Range!";
	
	// Different type values
	public static final String RATE = "rate";
	public static final String PERFORMANCE = "performance";
	public static final String requests = "requests";
	
	// The Warning and Error Thresholds
	public static final int WARNING_THRESHOLD = 90;
	public static final int ERROR_THRESHOLD = 95;
	public static final int HTML5_WARNING_THRESHOLD = 20;
	public static final int HTML5_ERROR_THRESHOLD = 30;
	
	// The max possible value in the random function for metrics
	public static final int MAX_RAND_VALUE = 101;
	public static final int MAX_RAND_VALUE_HTML5 = 40;
	
	public static String getStatusForState(String state) {
		switch (state) {
		case OK_STATE: 
			return HEALTHY_STATUS;
		case WARNING_STATE: 
			return UNHEALTHY_STATUS;
		case CRITICAL_STATE:
		case ERROR_STATE: 
			return RESTART_STATUS;
		default: 
			return UNKNOWN_STATUS;
			
		}
	}
	
	public static String getMessageForValueWithThresholds(double value, int warningThreshold, int errorThreshold)
	{
		if (value >= errorThreshold)
		{
			return ERROR_RANGE;
		}
		else if (value >= warningThreshold)
		{
			return WARNING_RANGE;
		}
		return WITHIN_RANGE;
	}
}