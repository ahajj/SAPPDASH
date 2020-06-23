package com.appmonitor.systems;

import java.util.List;
import java.util.Random;

import com.appmonitor.support.AMSupport;
import com.appmonitor.systems.metrics.Metric;

public class HTML5System extends System {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String application;
	
	public HTML5System(String type, String account, String application) {
		super(type, account);
		this.setId(type+account+application);
		this.application = application;
		// TODO Auto-generated constructor stub
	}
	public HTML5System(String id, String type, String account, String application, String state) {
		super(type, account);
		this.setId(id);
		this.application = application;
		this.setState(state);
		
	}
	
	public HTML5System(String type, String account, List<Metric> metrics, String application) {
		super(type, account, metrics);
		this.setId(type+account+application);
		this.application = application;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String generateSystemStats() {
		
		return "HTML5 " + super.toString();
		
	}

	@Override
	public void generateMetrics() {
		
		Random rand = new Random();
		addMetric(new Metric(AMSupport.HTML5_METRIC1,  rand.nextInt(AMSupport.MAX_RAND_VALUE_HTML5), "%", AMSupport.HTML5_WARNING_THRESHOLD, AMSupport.HTML5_ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.HTML5_WARNING_THRESHOLD, AMSupport.HTML5_ERROR_THRESHOLD));
		addMetric(new Metric(AMSupport.HTML5_METRIC2, rand.nextInt(AMSupport.MAX_RAND_VALUE_HTML5), "%", AMSupport.HTML5_WARNING_THRESHOLD, AMSupport.HTML5_ERROR_THRESHOLD, AMSupport.MIN_SEC_EPOCH + rand.nextInt(AMSupport.MAX_MS), AMSupport.RATE, AMSupport.HTML5_WARNING_THRESHOLD, AMSupport.HTML5_ERROR_THRESHOLD));
		
	}
	
	@Override
	public void niceOutput() {
		super.niceOutput();
		super.niceOutputBottom();
	}

	public String getApplication() {
		return application;
	}
	
}
