package org.mesh4j.sync.validations;

public class MeshException extends RuntimeException {

	private static final long serialVersionUID = -7137921619885853703L;

	public MeshException() {
		super();
	}

	public MeshException(String message, Throwable cause) {
		super(message, cause);
	}

	public MeshException(String message) {
		super(message);
	}

	public MeshException(Throwable cause) {
		super(cause);
	}
}
