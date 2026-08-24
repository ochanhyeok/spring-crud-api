package hello.crud.commentlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.commentlike.dto.CommentLikeResponse;
import hello.crud.support.ApiTestSupport;

class CommentLikeApiTest extends ApiTestSupport {

	@Test
	void 좋아요_저장() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "내용").getId();

		// when
		CommentLikeResponse response = createCommentLike(commentId);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(1);
		assertThat(response.isLiked()).isTrue();
	}

	@Test
	void 좋아요_취소() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "내용").getId();

		createCommentLike(commentId);

		// when
		CommentLikeResponse response = restClient.method(HttpMethod.DELETE).uri("/api/comments/" + commentId + "/likes")
			.retrieve()
			.body(CommentLikeResponse.class);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(0);
		assertThat(response.isLiked()).isFalse();
	}

	@Test
	void 좋아요_개수() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		apiTestDataFactory.createMember("ohchanhyeok12121313", "손흥민").getId();

		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "내용").getId();
		createCommentLike(commentId);

		login("ohchanhyeok12121313");
		createCommentLike(commentId);

		// when
		Long likeCount = restClient.get().uri("/api/comments/" + commentId + "/likes/count")
			.retrieve()
			.body(Long.class);

		// then
		assertThat(likeCount).isEqualTo(2);
	}

	@Test
	void 중복_좋아요() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "내용").getId();
		createCommentLike(commentId);

		// when & then
		assertThatThrownBy(() -> createCommentLike(commentId))
			.isInstanceOf(HttpClientErrorException.Conflict.class);
	}

	@Test
	void 삭제된_댓글에는_좋아요를_누를_수_없다() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글내용").getId();

		restClient.delete().uri("/api/comments/" + commentId)
			.retrieve()
			.toBodilessEntity();

		// when & then
		assertThatThrownBy(() -> createCommentLike(commentId))
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	@Test
	void 없는_댓글에_좋아요() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");

		// when & then
		assertThatThrownBy(() -> createCommentLike(999L))
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	private CommentLikeResponse createCommentLike(Long commentId) {
		return restClient.post().uri("/api/comments/" + commentId + "/likes")
			.retrieve()
			.body(CommentLikeResponse.class);
	}

}