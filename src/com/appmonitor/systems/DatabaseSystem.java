package com.appmonitor.systems;

import java.util.List;
import java.util.Random;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;

public class DatabaseSystem extends System {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String dbsystem;
	
	public DatabaseSystem(String type, String account, List<Metric> metrics, String dbsystem) {
		super(type, account, metrics);
		this.setId(type+account+dbsystem);
		// TODO Auto-generated constructor stubf
		this.dbsystem = dbsystem;
	}

	public DatabaseSystem(String type, String account, String dbsystem) {
		super(type, account);
		this.setId(type+account+dbsystem);
		// TODO Auto-generated constructor stub
		this.dbsystem = dbsystem;
	}
	
	@Override
	public String generateSystemStats() {
		return "Database " + super.toString();
	}

	@Override
	public void generateMetrics() {
		Random rand = new Random();
		addMetric(new Metric(AMSupport.CPU_LOAD, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.DISK_IO, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
	}
	
	@Override
	public void niceOutput() {
		super.niceOutput();
		super.niceOutputBottom();
	}
}
