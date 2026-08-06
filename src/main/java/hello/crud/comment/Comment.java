package hello.crud.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

	private Long id;
	private String content;
	private Long postId;
	private Long memberId;

	public Comment(String content, Long postId, Long memberId) {
		this.content = content;
		this.postId = postId;
		this.memberId = memberId;
	}

	public void assignId(Long id) {
		this.id = id;
	}
}
