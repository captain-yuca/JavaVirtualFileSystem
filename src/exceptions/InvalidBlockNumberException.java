package exceptions;

public class InvalidBlockNumberException extends RuntimeException {
	
	public InvalidBlockNumberException() {
	}

	public InvalidBlockNumberException(String message) {
		super(message);
	}

	public InvalidBlockNumberException(Throwable cause) {
		super(cause);
	}

	public InvalidBlockNumberException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
