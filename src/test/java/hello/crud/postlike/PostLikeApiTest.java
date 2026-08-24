package hello.crud.postlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.postlike.dto.PostLikeResponse;
import hello.crud.support.ApiTestSupport;

class PostLikeApiTest extends ApiTestSupport {

	@Test
	void 좋아요_저장() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();

		// when
		PostLikeResponse postLikeResponse = createPostLike(postId);

		// then
		assertThat(postLikeResponse).isNotNull();
		assertThat(postLikeResponse.getPostId()).isEqualTo(postId);
		assertThat(postLikeResponse.getLikeCount()).isEqualTo(1);
		assertThat(postLikeResponse.isLiked()).isTrue();
	}

	@Test
	void 좋아요_취소() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "호날두").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		createPostLike(postId);

		// when
		PostLikeResponse response = restClient.method(HttpMethod.DELETE).uri("/api/posts/" + postId + "/likes")
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
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민").getId();
		apiTestDataFactory.createMember("ohchanhyeok1334241", "호날두").getId();

		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		createPostLike(postId);

		login("ohchanhyeok1334241");
		createPostLike(postId);

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
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		createPostLike(postId);

		// when & then
		assertThatThrownBy(() -> {
			createPostLike(postId);
		}).isInstanceOf(HttpClientErrorException.Conflict.class);
	}

	@Test
	void 삭제된_게시글에는_좋아요를_누를_수_없다() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();

		restClient.delete().uri("/api/posts/" + postId)
			.retrieve()
			.toBodilessEntity();

		// when & then
		assertThatThrownBy(() -> createPostLike(postId))
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	@Test
	void 없는_게시글에_좋아요() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");

		// when & then
		assertThatThrownBy(() -> createPostLike(999L))
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	private PostLikeResponse createPostLike(Long postId) {
		return restClient.post().uri("/api/posts/" + postId + "/likes")
			.retrieve()
			.body(PostLikeResponse.class);
	}
}