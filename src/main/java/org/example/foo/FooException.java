package org.example.foo;

public class FooException extends RuntimeException {

	private static final long serialVersionUID = 1L;


	public FooException(String message) {
		super(message);
	}

	public FooException(String message, Throwable cause) {
		super(message, cause);
	}

}
