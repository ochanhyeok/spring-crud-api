package hello.crud.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Modifying(clearAutomatically = true)
	@Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :id and p.deletedAt is null")
	void increaseViewCount(@Param("id") Long id);

	@EntityGraph(attributePaths = "member")
	List<Post> findAllByDeletedAtIsNull();

	Optional<Post> findByIdAndDeletedAtIsNull(Long id);
}
