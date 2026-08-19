package hello.crud.postlike;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.auth.LoginMember;
import hello.crud.postlike.dto.PostLikeResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("/{postId}/likes")
	public PostLikeResponse like(@PathVariable Long postId, @AuthenticationPrincipal LoginMember loginMember) {
		return postLikeService.like(postId, loginMember.id());
	}

	@DeleteMapping("/{postId}/likes")
	public PostLikeResponse unLike(@PathVariable Long postId, @AuthenticationPrincipal LoginMember loginMember) {
		return postLikeService.unLike(postId, loginMember.id());
	}

	@GetMapping("/{postId}/likes/count")
	public long count(@PathVariable Long postId) {
		return postLikeService.getLikeCount(postId);
	}
}
