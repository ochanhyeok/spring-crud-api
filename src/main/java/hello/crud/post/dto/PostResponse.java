package hello.crud.post.dto;

import hello.crud.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {

	private Long id;
	private String title;
	private String content;
	private Long memberId;
	private String authorName;


	public static PostResponse of(Post post, String authorName) {
		return PostResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.memberId(post.getMemberId())
			.authorName(authorName)
			.build();
	}
}
