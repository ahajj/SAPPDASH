package com.appmonitor.support;

public class SystemsRecoveryException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "Unable to recover systems from backup or backup was not found...Generating new list of Systems.";

	public void messageToConsole()
	{
		System.out.println(MESSAGE);
	}
}
