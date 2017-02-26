package exceptions;

/**
 * Exception created for errors regarding the creation of an already existing DiskUnit. 
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class ExistingDiskException extends RuntimeException {
	
	/**
	 * Constructor for ExistingDiskException
	 * 
	 */
	public ExistingDiskException() {
	}
	
	/**
	 * Constructor for ExistingDiskException with a message
	 * 
	 * @param message Message that you want to show when the exception is thrown
	 */
	public ExistingDiskException(String message) {
		super(message);
	}

}
