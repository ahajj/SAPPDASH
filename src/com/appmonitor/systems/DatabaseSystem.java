package com.appmonitor.systems;

import java.util.List;
import java.util.Random;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;

public class DatabaseSystem extends System {

	private String dbsystem;
	
	public DatabaseSystem(String type, String account, String state, List<Metric> metrics, String dbsystem) {
		super(type, account, state, metrics);
		this.setId(type+account+dbsystem);
		// TODO Auto-generated constructor stubf
		this.dbsystem = dbsystem;
	}

	public DatabaseSystem(String type, String account, String state, String dbsystem) {
		super(type, account, state);
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
		addMetric(new Metric(AMSupport.CPU_LOAD, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.DISK_IO, rand.nextInt(AMSupport.MAX_RAND_VALUE), "%", AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD, rand.nextLong(), AMSupport.RATE, AMSupport.WARNING_THRESHOLD, AMSupport.ERROR_THRESHOLD));
	}
}
