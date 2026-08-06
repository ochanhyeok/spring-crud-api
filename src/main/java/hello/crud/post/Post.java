package hello.crud.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

	private Long id;
	private String title;
	private String content;
	private Long memberId;

	public Post(String title, String content, Long memberId) {
		this.title = title;
		this.content = content;
		this.memberId = memberId;
	}

	public void assignId(Long id) {
		this.id = id;
	}
}
