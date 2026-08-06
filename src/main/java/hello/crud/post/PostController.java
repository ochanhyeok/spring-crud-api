package hello.crud.post;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping
	public PostResponse createPost(@RequestBody PostCreateRequest request) {
		return postService.create(request);
	}

	@GetMapping("/{postId}")
	public PostResponse getPost(@PathVariable Long postId) {
		return postService.findOne(postId);
	}

	@GetMapping
	public List<PostResponse> getPosts() {
		return postService.findAll();
	}
}
