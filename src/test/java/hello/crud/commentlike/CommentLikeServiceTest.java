package hello.crud.commentlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hello.crud.commentlike.dto.CommentLikeResponse;
import hello.crud.common.DuplicateLikeException;
import hello.crud.support.ServiceTestSupport;

class CommentLikeServiceTest extends ServiceTestSupport {

	@Autowired
	private CommentLikeService commentLikeService;

	@Test
	void like_성공() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		Long commentId = testDataFactory.createComment(postId, memberId);

		// when
		CommentLikeResponse response = commentLikeService.like(commentId, memberId);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(1);
	}

	@Test
	void unLike_성공() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		Long commentId = testDataFactory.createComment(postId, memberId);
		commentLikeService.like(commentId, memberId);

		// when
		CommentLikeResponse response = commentLikeService.unLike(commentId, memberId);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(0);
	}

	@Test
	void like_중복_예외() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		Long commentId = testDataFactory.createComment(postId, memberId);
		commentLikeService.like(commentId, memberId);

		// when & then
		assertThatThrownBy(() -> commentLikeService.like(commentId, memberId))
			.isInstanceOf(DuplicateLikeException.class);
	}

	@Test
	void getLikeCount() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ohchanhyeok123133");
		Long postId = testDataFactory.createPost(memberId);
		Long commentId = testDataFactory.createComment(postId, memberId);

		commentLikeService.like(commentId, memberId);
		commentLikeService.like(commentId, memberId2);

		// when
		long likeCount = commentLikeService.getLikeCount(commentId);

		// then
		assertThat(likeCount).isEqualTo(2);
	}

	@Test
	void hasLiked() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ohchanhyeok123133");
		Long postId = testDataFactory.createPost(memberId);
		Long commentId = testDataFactory.createComment(postId, memberId);

		commentLikeService.like(commentId, memberId);
		commentLikeService.like(commentId, memberId2);
		commentLikeService.unLike(commentId, memberId);

		// when
		boolean hasLikedMember = commentLikeService.hasLiked(commentId, memberId);
		boolean hasLikedMember2 = commentLikeService.hasLiked(commentId, memberId2);

		// then
		assertThat(hasLikedMember).isFalse();
		assertThat(hasLikedMember2).isTrue();
	}

}