package hello.crud.postlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.postlike.dto.PostLikeCreateRequest;
import hello.crud.postlike.dto.PostLikeResponse;
import hello.crud.support.ApiTestSupport;

class PostLikeApiTest extends ApiTestSupport {

	@Test
	void 좋아요_저장() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();

		// when
		PostLikeResponse postLikeResponse = createPostLike(memberId, postId);

		// then
		assertThat(postLikeResponse).isNotNull();
		assertThat(postLikeResponse.getPostId()).isEqualTo(postId);
		assertThat(postLikeResponse.getLikeCount()).isEqualTo(1);
		assertThat(postLikeResponse.isLiked()).isTrue();
	}

	@Test
	void 좋아요_취소() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();
		createPostLike(memberId, postId);

		PostLikeCreateRequest request = new PostLikeCreateRequest();
		request.setMemberId(memberId);

		// when
		PostLikeResponse response = restClient.method(HttpMethod.DELETE).uri("/api/posts/" + postId + "/likes")
			.body(request)
			.retrieve()
			.body(PostLikeResponse.class);

		// then
		assertThat(response.getLikeCount()).isEqualTo(0);
		assertThat(response.isLiked()).isFalse();
		assertThat(response.getPostId()).isEqualTo(postId);
	}

	@Test
	void 좋아요_개수() {
		// given
		Long memberId1 = apiTestDataFactory.createMember("ohchanhyeok123", "손흥민").getId();
		Long memberId2 = apiTestDataFactory.createMember("ohchanhyeok1334241", "호날두").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId1).getId();

		createPostLike(memberId1, postId);
		createPostLike(memberId2, postId);

		// when
		Long likeCount = restClient.get().uri("/api/posts/" + postId + "/likes/count")
			.retrieve()
			.body(Long.class);

		// then
		assertThat(likeCount).isEqualTo(2);
	}

	@Test
	void 중복_좋아요() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "SON").getId();
		Long postId = apiTestDataFactory.createPost("제목", memberId).getId();
		createPostLike(memberId, postId);

		// when & then
		assertThatThrownBy(() -> {
			createPostLike(memberId, postId);
		}).isInstanceOf(HttpClientErrorException.Conflict.class);
	}

	private PostLikeResponse createPostLike(Long memberId, Long postId) {
		PostLikeCreateRequest request = new PostLikeCreateRequest();
		request.setMemberId(memberId);

		return restClient.post().uri("/api/posts/" + postId + "/likes")
			.body(request)
			.retrieve()
			.body(PostLikeResponse.class);
	}
}