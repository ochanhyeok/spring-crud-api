package hello.crud.common;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {

	private final ErrorCode errorCode;

	public DuplicateException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
