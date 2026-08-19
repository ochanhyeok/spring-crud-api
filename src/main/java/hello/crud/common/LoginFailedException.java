package hello.crud.common;

public class LoginFailedException extends RuntimeException {
	public LoginFailedException(String message) {
		super(message);
	}
}
