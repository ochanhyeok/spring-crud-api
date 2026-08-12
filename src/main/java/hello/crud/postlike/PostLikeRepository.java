package hello.crud.postlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	Long countByPostId(Long postId);

	boolean existsByPostIdAndMemberId(Long postId, Long memberId);

	void deleteByPostIdAndMemberId(Long postId, Long memberId);
}
