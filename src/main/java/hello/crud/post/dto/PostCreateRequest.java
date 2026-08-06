package hello.crud.post.dto;

import lombok.Data;

@Data
public class PostCreateRequest {

	private String title;
	private String content;
	private Long memberId;

}
