package hello.crud.commentlike.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeResponse {

	private Long commentId;
	private long likeCount;
	private boolean liked;

	public static CommentLikeResponse of(Long commentId, long likeCount, boolean liked) {
		return CommentLikeResponse.builder()
			.commentId(commentId)
			.likeCount(likeCount)
			.liked(liked)
			.build();
	}
}
