package hello.crud.comment;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.auth.LoginMember;
import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public CommentResponse createComment(
		@RequestBody @Valid CommentCreateRequest request,
		@AuthenticationPrincipal LoginMember loginMember
		) {
		return commentService.create(request, loginMember.id());
	}

	@GetMapping("/{commentId}")
	public CommentResponse getComment(@PathVariable Long commentId) {
		return commentService.findOne(commentId);
	}

	@GetMapping
	public List<CommentResponse> getComments() {
		return commentService.findAll();
	}
}
