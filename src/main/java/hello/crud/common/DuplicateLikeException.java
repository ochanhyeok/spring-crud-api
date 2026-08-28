package hello.crud.common;

import lombok.Getter;

@Getter
public class DuplicateLikeException extends RuntimeException {

	private final ErrorCode errorCode;

	public DuplicateLikeException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
