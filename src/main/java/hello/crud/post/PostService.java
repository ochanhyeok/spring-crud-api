package hello.crud.post;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.comment.CommentService;
import hello.crud.common.AccessDeniedException;
import hello.crud.common.ErrorCode;
import hello.crud.common.NotFoundException;
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
	private final CommentService commentService;

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
		return postRepository.findAllByDeletedAtIsNull().stream()
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
			throw new AccessDeniedException(ErrorCode.NOT_AUTHOR);
		}
		post.update(request.getTitle(), request.getContent());
		return PostResponse.of(post, getAuthorName(post));
	}

	@Transactional
	public void delete(Long postId, Long memberId) {
		Post post = findPostById(postId);
		if (!post.isWrittenBy(memberId)) {
			throw new AccessDeniedException(ErrorCode.NOT_AUTHOR);
		}
		post.delete();
		commentService.deleteByPostId(postId);
	}

	public Post findPostById(Long id) {
		return postRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
	}

	private String getAuthorName(Post post) {
		return post.getMember().getName();
	}
}
