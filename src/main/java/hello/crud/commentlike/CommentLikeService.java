package hello.crud.commentlike;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.comment.Comment;
import hello.crud.comment.CommentRepository;
import hello.crud.commentlike.dto.CommentLikeResponse;
import hello.crud.common.DuplicateException;
import hello.crud.common.ErrorCode;
import hello.crud.common.NotFoundException;
import hello.crud.member.Member;
import hello.crud.member.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentLikeService {

	private final CommentLikeRepository commentLikeRepository;
	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public CommentLikeResponse like(Long commentId, Long memberId) {
		if (!commentRepository.existsByIdAndDeletedAtIsNull(commentId)) {
			throw new NotFoundException(ErrorCode.COMMENT_NOT_FOUND);
		}
		if (commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_COMMENT_LIKE);
		}

		Comment comment = commentRepository.getReferenceById(commentId);
		Member member = memberRepository.getReferenceById(memberId);
		CommentLike commentLike = CommentLike.builder()
			.comment(comment)
			.member(member)
			.build();

		try {
			commentLikeRepository.save(commentLike);
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateException(ErrorCode.DUPLICATE_COMMENT_LIKE);
		}

		long likeCount = commentLikeRepository.countByCommentId(commentId);
		return CommentLikeResponse.of(commentId, likeCount, true);
	}

	@Transactional
	public CommentLikeResponse unLike(Long commentId, Long memberId) {
		commentLikeRepository.deleteByCommentIdAndMemberId(commentId, memberId);
		long likeCount = commentLikeRepository.countByCommentId(commentId);
		return CommentLikeResponse.of(commentId, likeCount, false);
	}

	public long getLikeCount(Long commentId) {
		return commentLikeRepository.countByCommentId(commentId);
	}

	public boolean hasLiked(Long commentId, Long memberId) {
		return commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId);
	}
}
