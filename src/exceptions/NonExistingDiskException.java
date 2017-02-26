package exceptions;

/**
 * Exception created for errors regarding missing DiskUnits
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
import java.io.FileNotFoundException;

public class NonExistingDiskException extends FileNotFoundException {
	
	/**
	 * Constructor for NonExistingDiskException
	 * 
	 */
	public NonExistingDiskException() {
	}
	
	/**
	 * Constructor for NonExistingDiskException with a message
	 * 
	 * @param message Message that you want to show when the exception is thrown
	 */
	public NonExistingDiskException(String message) {
		super(message);
	}

}
