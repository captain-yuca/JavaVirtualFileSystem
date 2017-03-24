package exceptions;

public class FullStackException extends RuntimeException {

	public FullStackException() {
		super();
	}

	public FullStackException(String message) {
		super(message);
	}

	public FullStackException(String message, Throwable cause) {
		super(message, cause);
	}

	public FullStackException(Throwable cause) {
		super(cause);
	}

}
