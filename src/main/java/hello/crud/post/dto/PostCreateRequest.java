package hello.crud.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostCreateRequest {

	@NotBlank(message = "제목은 필수입니다")
	@Size(min = 2, max = 50)
	private String title;

	@NotBlank(message = "내용은 필수입니다")
	@Size(min = 2, max = 100)
	private String content;

	private Long memberId;

}
