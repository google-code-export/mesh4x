package org.mesh4j.sync.message.channel.sms.batch;

public class DiscardedBatchException extends RuntimeException {

	private static final long serialVersionUID = 7331874607404805475L;

	public DiscardedBatchException() {
		super();
	}

	public DiscardedBatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public DiscardedBatchException(String message) {
		super(message);
	}

	public DiscardedBatchException(Throwable cause) {
		super(cause);
	}

}
