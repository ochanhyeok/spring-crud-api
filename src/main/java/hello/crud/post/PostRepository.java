package hello.crud.post;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("select p from Post p join fetch p.member")
	List<Post> findAllWithMember();

	@EntityGraph(attributePaths = "member")
	List<Post> findAllBy();
}
