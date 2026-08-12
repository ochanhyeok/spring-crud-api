package hello.crud.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponse {

	private Long postId;
	private Long likeCount;
	private boolean liked;

	public static PostLikeResponse of(Long postId, long likeCount, boolean liked) {
		return PostLikeResponse.builder()
			.postId(postId)
			.likeCount(likeCount)
			.liked(liked)
			.build();
	}

}
