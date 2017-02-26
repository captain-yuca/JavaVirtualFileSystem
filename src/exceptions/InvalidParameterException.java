package exceptions;

/**
 * Exception created for errors regarding whenever the values for capacity
 * or blockSize are not valid according to the specifications 
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class InvalidParameterException extends RuntimeException {
	
	/**
	 * Constructor for InvalidParameterException
	 * 
	 */
	public InvalidParameterException() {
	}
	
	/**
	 * Constructor for InvalidParameterException with a message
	 * 
	 * @param message Message that you want to show when the exception is thrown
	 */
	public InvalidParameterException(String message) {
		super(message);
	}


}
