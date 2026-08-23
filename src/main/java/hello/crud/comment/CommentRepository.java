package hello.crud.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@EntityGraph(attributePaths = "member")
	List<Comment> findAllByDeletedAtIsNull();

	Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update Comment c set c.deletedAt = :now where c.postId = :postId and c.deletedAt is null")
	void softDeleteByPostId(@Param("postId") Long postId, @Param("now")LocalDateTime now);
}
