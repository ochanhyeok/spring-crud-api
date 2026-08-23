package hello.crud.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.comment.dto.CommentUpdateRequest;
import hello.crud.common.AccessDeniedException;
import hello.crud.member.Member;
import hello.crud.member.MemberService;
import hello.crud.post.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final MemberService memberService;
	private final PostRepository postRepository;

	@Transactional
	public CommentResponse create(CommentCreateRequest request, Long memberId) {
		Member member = memberService.findMemberById(memberId);
		if (!postRepository.existsById(request.getPostId())) {
			throw new NoSuchElementException("게시글이 없습니다. id=" + request.getPostId());
		}
		Comment comment = Comment.builder()
			.content(request.getContent())
			.member(member)
			.postId(request.getPostId())
			.build();
		commentRepository.save(comment);
		return CommentResponse.of(comment, getAuthorName(comment));
	}

	public CommentResponse findOne(Long id) {
		Comment comment = commentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new NoSuchElementException("댓글이 없습니다. id=" + id));
		return CommentResponse.of(comment, getAuthorName(comment));
	}

	public List<CommentResponse> findAll() {
		return commentRepository.findAllByDeletedAtIsNull().stream()
			.map(comment -> CommentResponse.of(comment, getAuthorName(comment)))
			.toList();
	}

	@Transactional
	public CommentResponse update(Long id, CommentUpdateRequest request, Long memberId) {
		Comment comment = findCommentById(id);
		if (!comment.isWrittenBy(memberId)) {
			throw new AccessDeniedException("작성자만 수정할 수 있습니다");
		}
		comment.update(request.getContent());
		return CommentResponse.of(comment, getAuthorName(comment));
	}

	@Transactional
	public void delete(Long id, Long memberId) {
		Comment comment = findCommentById(id);
		if (!comment.isWrittenBy(memberId)) {
			throw new AccessDeniedException("작성자만 삭제할 수 있습니다");
		}
		comment.delete();
	}

	@Transactional
	public void deleteByPostId(Long postId) {
		commentRepository.softDeleteByPostId(postId, LocalDateTime.now());
	}

	private String getAuthorName(Comment comment) {
		return comment.getMember().getName();
	}

	public Comment findCommentById(Long id) {
		return commentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new NoSuchElementException("댓글이 없습니다. id=" + id));
	}
}
