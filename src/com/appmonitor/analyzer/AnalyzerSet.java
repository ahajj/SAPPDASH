package com.appmonitor.analyzer;

import com.appmonitor.systems.System;

public abstract class AnalyzerSet {

	// include an abstract function for it's subclasses
	public abstract void analyzeSystem(System system);
	protected abstract void writeDetailsToLogForSysHealth(System system);
}
