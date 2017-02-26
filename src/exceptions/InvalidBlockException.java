package exceptions;

/**
 * Exception created for invalid VirtualDiskBlock instances. 
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class InvalidBlockException extends RuntimeException {
	
	/**
	 * Constructor for InvalidBlockException
	 * 
	 */
	public InvalidBlockException() {
	}
	
	/**
	 * Constructor for InvalidBlockException with a message
	 * 
	 * @param message Message that you want to show when the exception is thrown
	 */
	public InvalidBlockException(String message) {
		super(message);
	}

}
