package hello.crud.common;

public class DuplicateLikeException extends RuntimeException {
	public DuplicateLikeException(String message) {
		super(message);
	}
}
