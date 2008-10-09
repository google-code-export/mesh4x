package org.mesh4j.sync.ui;

public enum SmsExecutionMode {

	EMULATE,
	SYNCHRONIZE,
	SEND, 
	CANCEL_SYNC;

	public boolean isEmulate() {
		return EMULATE.equals(this);
	}

	public boolean isSynchronize() {
		return SYNCHRONIZE.equals(this);
	}

	public boolean isCancelSync() {
		return CANCEL_SYNC.equals(this);
	}
}
