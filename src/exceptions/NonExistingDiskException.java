package exceptions;

import java.io.FileNotFoundException;

public class NonExistingDiskException extends FileNotFoundException {

	public NonExistingDiskException() {
		// TODO Auto-generated constructor stub
	}

	public NonExistingDiskException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
