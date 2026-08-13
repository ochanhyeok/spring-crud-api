package hello.crud.commentlike;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.commentlike.dto.CommentLikeCreateRequest;
import hello.crud.commentlike.dto.CommentLikeResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class
CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PostMapping("/{commentId}/likes")
	public CommentLikeResponse like(@PathVariable Long commentId, @RequestBody CommentLikeCreateRequest request) {
		return commentLikeService.like(commentId, request.getMemberId());
	}

	@DeleteMapping("/{commentId}/likes")
	public CommentLikeResponse unLike(@PathVariable Long commentId, @RequestBody CommentLikeCreateRequest request) {
		return commentLikeService.unLike(commentId, request.getMemberId());
	}

	@GetMapping("/{commentId}/likes/count")
	public long count(@PathVariable Long commentId) {
		return commentLikeService.getLikeCount(commentId);
	}
}
