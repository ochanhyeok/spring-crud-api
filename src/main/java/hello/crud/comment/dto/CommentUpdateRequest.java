package hello.crud.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateRequest {

	@NotBlank(message = "댓글내용은 필수입니다")
	@Size(max = 500)
	private String content;
}
