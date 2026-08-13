package hello.crud.commentlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	long countByCommentId(Long commentId);

	boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);

	void deleteByCommentIdAndMemberId(Long commentId, Long memberId);
}
