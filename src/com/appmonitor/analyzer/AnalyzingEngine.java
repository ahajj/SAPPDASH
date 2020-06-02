package com.appmonitor.analyzer;

import com.appmonitor.systems.System;

public class AnalyzingEngine<aAnalyzerSet extends AnalyzerSet> {

	// The AnalyzerSet this AnalyzingEngine is utiilizing
	private aAnalyzerSet analyzerSet;
	
	// Setters & Getters
	public aAnalyzerSet getAnalyzerSet() {
		return analyzerSet;
	}

	public void setAnalyzerSet(aAnalyzerSet analyzerSet) {
		this.analyzerSet = analyzerSet;
	
	}
	
	// Analyze the passed in system using the AnalyzerSet assigned to this AnalyzingEngine
	public void analyze(System system) {
		// analyze the system
		analyzerSet.analyzeSystem(system);
	}
	
}
