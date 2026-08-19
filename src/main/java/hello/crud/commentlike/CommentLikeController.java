package hello.crud.commentlike;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.auth.LoginMember;
import hello.crud.commentlike.dto.CommentLikeResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class
CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PostMapping("/{commentId}/likes")
	public CommentLikeResponse like(@PathVariable Long commentId, @AuthenticationPrincipal LoginMember loginMember) {
		return commentLikeService.like(commentId, loginMember.id());
	}

	@DeleteMapping("/{commentId}/likes")
	public CommentLikeResponse unLike(@PathVariable Long commentId, @AuthenticationPrincipal LoginMember loginMember) {
		return commentLikeService.unLike(commentId, loginMember.id());
	}

	@GetMapping("/{commentId}/likes/count")
	public long count(@PathVariable Long commentId) {
		return commentLikeService.getLikeCount(commentId);
	}
}
