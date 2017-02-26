package exceptions;

/**
 * Exception created for invalid block positions on DiskUnit. 
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class InvalidBlockNumberException extends RuntimeException {
	
	/**
	 * Constructor for InvalidBlockNumberException
	 * 
	 */
	public InvalidBlockNumberException() {
	}
	
	/**
	 * Constructor for InvalidBlockNumberException with a message
	 * 
	 * @param message Message that you want to show when the exception is thrown
	 */
	public InvalidBlockNumberException(String message) {
		super(message);
	}
	
}
