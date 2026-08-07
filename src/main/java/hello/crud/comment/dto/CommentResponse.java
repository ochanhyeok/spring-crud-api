package hello.crud.comment.dto;

import hello.crud.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

	private Long id;
	private String content;
	private Long postId;
	private Long memberId;
	private String authorName;

	public static CommentResponse of(Comment comment, String authorName) {
		return CommentResponse.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.postId(comment.getPostId())
			.memberId(comment.getMemberId())
			.authorName(authorName)
			.build();
	}
}
