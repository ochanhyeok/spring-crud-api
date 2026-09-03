package hello.crud.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
	UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
	NOT_AUTHOR(HttpStatus.FORBIDDEN, "작성자만 수정하거나 삭제할 수 있습니다."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 없습니다."),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 없습니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 없습니다."),
	DUPLICATE_POST_LIKE(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다."),
	DUPLICATE_COMMENT_LIKE(HttpStatus.CONFLICT, "이미 좋아요를 누른 댓글입니다."),
	DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
	;

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
