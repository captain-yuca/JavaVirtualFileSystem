package exceptions;

public class EmptyStackException extends RuntimeException {

	public EmptyStackException() {
	}

	public EmptyStackException(String arg0) {
		super(arg0);
	}

	public EmptyStackException(Throwable arg0) {
		super(arg0);
	}

	public EmptyStackException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
