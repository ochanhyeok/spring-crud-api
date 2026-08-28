package hello.crud.common.dto;

import hello.crud.common.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

	private String code;
	private int status;
	private String message;

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(
			errorCode.name(),
			errorCode.getStatus().value(),
			errorCode.getMessage()
		);
	}

	public static ErrorResponse of(ErrorCode errorCode, String message) {
		return new ErrorResponse(
			errorCode.name(),
			errorCode.getStatus().value(),
			message
		);
	}
}
