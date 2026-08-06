package hello.crud.comment.dto;

import lombok.Data;

@Data
public class CommentCreateRequest {

	private String content;
	private Long postId;
	private Long memberId;

}
