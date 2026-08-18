package hello.crud.post;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("select p from Post p join fetch p.member")
	List<Post> findAllWithMember();

	@EntityGraph(attributePaths = "member")
	List<Post> findAllBy();

	@Modifying(clearAutomatically = true)
	@Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :id")
	void increaseViewCount(@Param("id") Long id);
}
