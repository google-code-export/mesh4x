package org.mesh4j.sync.message;

public class InOutStatistics {

	// MODEL VARIABLES
	private int in = 0;
	private int out = 0;
	private int inPendingToArrive = 0;
	private int outPendingAcks = 0;
	
	
	// BUSINESS METHODS
	
	public InOutStatistics(int in, int inPendingToArrive, int out, int outPendingAcks) {
		super();
		
		this.in = in;
		this.out = out;
		this.inPendingToArrive = inPendingToArrive;
		this.outPendingAcks = outPendingAcks;
	}

	public int getNumberInMessages() {
		return this.in;
	}

	public int getNumberOutMessages() {
		return this.out;
	}
	
	public int getNumberInPendingToArriveMessages() {
		return this.inPendingToArrive;
	}

	public int getNumberOutPendingAckMessages() {
		return this.outPendingAcks;
	}
}
