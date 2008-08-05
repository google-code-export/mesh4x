package org.mesh4j.sync.ui;

public enum SmsExecutionMode {

	EMULATE,
	SYNCHRONIZE,
	SEND;

	public boolean isEmulate() {
		return EMULATE.equals(this);
	}

	public boolean isSynchronize() {
		return SYNCHRONIZE.equals(this);
	}
}
