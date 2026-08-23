package hello.crud.post;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.auth.LoginMember;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.post.dto.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping
	public PostResponse createPost(
		@RequestBody @Valid PostCreateRequest request,
		@AuthenticationPrincipal LoginMember loginMember
	) {
		return postService.create(request, loginMember.id());
	}

	@GetMapping("/{postId}")
	public PostResponse getPost(@PathVariable Long postId) {
		return postService.findOne(postId);
	}

	@GetMapping
	public List<PostResponse> getPosts() {
		return postService.findAll();
	}

	@PutMapping("/{postId}")
	public PostResponse updatePost(
		@PathVariable Long postId,
		@RequestBody @Valid PostUpdateRequest request,
		@AuthenticationPrincipal LoginMember loginMember
	) {
		return postService.update(postId, request, loginMember.id());
	}

	@DeleteMapping("/{postId}")
	public void deletePost(
		@PathVariable Long postId,
		@AuthenticationPrincipal LoginMember loginMember
	) {
		postService.delete(postId, loginMember.id());
	}
}
