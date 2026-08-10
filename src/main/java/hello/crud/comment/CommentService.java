package hello.crud.comment;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.member.MemberService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	// private final CommentRepository commentRepository;
	private final MyBatisCommentRepository commentRepository;
	private final MemberService memberService;

	public CommentResponse create(CommentCreateRequest request) {
		Comment comment = new Comment(request.getContent(), request.getPostId(), request.getMemberId());
		commentRepository.save(comment);
		return CommentResponse.of(comment, getAuthorName(comment));
	}

	public CommentResponse findOne(Long id) {
		Comment comment = commentRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("댓글이 없습니다. id=" + id));
		return CommentResponse.of(comment, getAuthorName(comment));
	}

	public List<CommentResponse> findAll() {
		return commentRepository.findAll().stream()
			.map(comment -> CommentResponse.of(comment, getAuthorName(comment)))
			.toList();
	}

	private String getAuthorName(Comment comment) {
		return memberService.findMemberById(comment.getMemberId()).getName();
	}
}
