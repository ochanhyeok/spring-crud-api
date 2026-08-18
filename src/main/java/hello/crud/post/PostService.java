package hello.crud.post;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.member.Member;
import hello.crud.member.MemberService;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final MemberService memberService;

	@Transactional
	public PostResponse create(PostCreateRequest request) {
		Member member = memberService.findMemberById(request.getMemberId());
		Post post = Post.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.member(member)
			.build();
		postRepository.save(post);
		return PostResponse.of(post, getAuthorName(post));
	}

	public List<PostResponse> findAll() {
		return postRepository.findAllBy().stream()
			.map(post -> PostResponse.of(post, getAuthorName(post)))
			.toList();
	}

	@Transactional
	public PostResponse findOne(Long id) {
		postRepository.increaseViewCount(id);
		Post post = findPostById(id);
		return PostResponse.of(post, getAuthorName(post));
	}

	public Post findPostById(Long id) {
		return postRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("게시글이 없습니다. id=" + id));
	}

	private String getAuthorName(Post post) {
		return post.getMember().getName();
	}
}
