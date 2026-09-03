package hello.crud.postlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hello.crud.common.DuplicateException;
import hello.crud.postlike.dto.PostLikeResponse;
import hello.crud.support.ServiceTestSupport;

class PostLikeServiceTest extends ServiceTestSupport {

	@Autowired
	private PostLikeService postLikeService;

	@Test
	void like_성공() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);

		// when
		PostLikeResponse response = postLikeService.like(postId, memberId);

		// then
		assertThat(response.getLikeCount()).isEqualTo(1);
		assertThat(response.getPostId()).isEqualTo(postId);
		assertThat(response.isLiked()).isTrue();
	}

	@Test
	void unLike_성공() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		postLikeService.like(postId, memberId);

		// when
		PostLikeResponse response = postLikeService.unLike(postId, memberId);

		// then
		assertThat(response.getLikeCount()).isEqualTo(0);
		assertThat(response.getPostId()).isEqualTo(postId);
		assertThat(response.isLiked()).isFalse();
	}

	@Test
	void like_중복_예외() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		postLikeService.like(postId, memberId);

		// when & then
		assertThatThrownBy(() -> postLikeService.like(postId, memberId))
			.isInstanceOf(DuplicateException.class);
	}

	@Test
	void getLikeCount() {
		// given
		Long memberId1 = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ohchanhyeok123345");
		Long postId = testDataFactory.createPost(memberId1);

		postLikeService.like(postId, memberId1);
		postLikeService.like(postId, memberId2);

		// when
		long likeCount = postLikeService.getLikeCount(postId);

		// then
		assertThat(likeCount).isEqualTo(2);
	}

	@Test
	void hasLiked() {
		// given
		Long memberId1 = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ohchanhyeok123345");
		Long postId = testDataFactory.createPost(memberId1);

		postLikeService.like(postId, memberId1);

		// when
		boolean member1liked = postLikeService.hasLiked(postId, memberId1);
		boolean member2liked = postLikeService.hasLiked(postId, memberId2);

		// then
		assertThat(member1liked).isTrue();
		assertThat(member2liked).isFalse();
	}

}