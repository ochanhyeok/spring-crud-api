package hello.crud.post;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.common.AccessDeniedException;
import hello.crud.member.Member;
import hello.crud.member.MemberService;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.post.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final MemberService memberService;

	@Transactional
	public PostResponse create(PostCreateRequest request, Long memberId) {
		Member member = memberService.findMemberById(memberId);
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

	@Transactional
	public PostResponse update(Long id, PostUpdateRequest request, Long memberId) {
		Post post = findPostById(id);
		if (!post.isWrittenBy(memberId)) {
			throw new AccessDeniedException("작성자만 수정할 수 있습니다");
		}
		post.update(request.getTitle(), request.getContent());
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
