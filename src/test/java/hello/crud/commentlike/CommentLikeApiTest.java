package hello.crud.commentlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.commentlike.dto.CommentLikeCreateRequest;
import hello.crud.commentlike.dto.CommentLikeResponse;
import hello.crud.support.ApiTestSupport;

class CommentLikeApiTest extends ApiTestSupport {

	@Test
	void 좋아요_저장() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();
		Long commentId = apiTestDataFactory.createComment(postId, memberId, "내용").getId();

		// when
		CommentLikeResponse response = createCommentLike(memberId, commentId);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(1);
		assertThat(response.isLiked()).isTrue();
	}

	@Test
	void 좋아요_취소() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();
		Long commentId = apiTestDataFactory.createComment(postId, memberId, "내용").getId();

		createCommentLike(memberId, commentId);

		CommentLikeCreateRequest request = new CommentLikeCreateRequest();
		request.setMemberId(memberId);

		// when
		CommentLikeResponse response = restClient.method(HttpMethod.DELETE).uri("/api/comments/" + commentId + "/likes")
			.body(request)
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
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		Long memberId2 = apiTestDataFactory.createMember("ohchanhyeok12121313", "손흥민").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();
		Long commentId = apiTestDataFactory.createComment(memberId, postId, "내용").getId();

		createCommentLike(memberId, commentId);
		createCommentLike(memberId2, commentId);

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
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();
		Long commentId = apiTestDataFactory.createComment(memberId, postId, "내용").getId();
		createCommentLike(memberId, commentId);

		// when & then
		assertThatThrownBy(() -> createCommentLike(memberId, commentId))
			.isInstanceOf(HttpClientErrorException.Conflict.class);
	}

	private CommentLikeResponse createCommentLike(Long memberId, Long commentId) {
		CommentLikeCreateRequest request = new CommentLikeCreateRequest();
		request.setMemberId(memberId);

		return restClient.post().uri("/api/comments/" + commentId + "/likes")
			.body(request)
			.retrieve()
			.body(CommentLikeResponse.class);
	}

}