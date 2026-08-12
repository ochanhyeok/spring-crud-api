package hello.crud.postlike;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.postlike.dto.PostLikeCreateRequest;
import hello.crud.postlike.dto.PostLikeResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("/{postId}/likes")
	public PostLikeResponse like(@PathVariable Long postId, @RequestBody PostLikeCreateRequest request) {
		return postLikeService.like(postId, request.getMemberId());
	}

	@DeleteMapping("/{postId}/likes")
	public PostLikeResponse unLike(@PathVariable Long postId, @RequestBody PostLikeCreateRequest request) {
		return postLikeService.unLike(postId, request.getMemberId());
	}

	@GetMapping("/{postId}/likes/count")
	public long count(@PathVariable Long postId) {
		return postLikeService.getLikeCount(postId);
	}
}
