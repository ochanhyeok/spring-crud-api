package hello.crud.post;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import hello.crud.member.MemberService;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final MemberService memberService;

	public PostResponse create(PostCreateRequest request) {
		Post post = new Post(request.getTitle(), request.getContent(), request.getMemberId());
		postRepository.save(post);
		return PostResponse.of(post, getAuthorName(post));
	}

	public List<PostResponse> findAll() {
		return postRepository.findAll().stream()
			.map(post -> PostResponse.of(post, getAuthorName(post)))
			.toList();
	}

	public PostResponse findOne(Long id) {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("게시글이 없습니다. id=" + id));
		return PostResponse.of(post, getAuthorName(post));
	}

	private String getAuthorName(Post post) {
		return memberService.findMemberById(post.getMemberId()).getName();
	}
}
