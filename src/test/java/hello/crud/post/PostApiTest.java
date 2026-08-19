package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.support.ApiTestSupport;

class PostApiTest extends ApiTestSupport {

	@Test
	void 게시글_저장() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		login("ohchanhyeok123");

		// when
		PostResponse postResponse = createPost("오늘은 뭐 먹지??");

		// then
		assertThat(postResponse.getId()).isNotNull();
		assertThat(postResponse.getAuthorName()).isEqualTo("오찬혁");
		assertThat(postResponse.getTitle()).isEqualTo("오늘은 뭐 먹지??");
	}

	@Test
	void 게시글_조회() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		login("ohchanhyeok123");
		Long postId = createPost("hello world").getId();

		// when
		PostResponse response = restClient.get()
			.uri("/api/posts/" + postId)
			.retrieve()
			.body(PostResponse.class);

		// then
		assertThat(response.getId()).isEqualTo(postId);
		assertThat(response.getTitle()).isEqualTo("hello world");
		assertThat(response.getAuthorName()).isEqualTo("오찬혁");
		assertThat(response.getContent()).isEqualTo("내용");
	}

	@Test
	void 게시글_목록() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		login("ohchanhyeok123");
		createPost("테스트코드 어렵다~");
		createPost("hello world~");

		// when
		List<PostResponse> responses = restClient.get()
			.uri("/api/posts")
			.retrieve()
			.body(new ParameterizedTypeReference<List<PostResponse>>() {
			});

		// then
		assertThat(responses.size()).isEqualTo(2);
		assertThat(responses).extracting(PostResponse::getTitle)
			.containsExactlyInAnyOrder("테스트코드 어렵다~", "hello world~");
		assertThat(responses).extracting(PostResponse::getAuthorName)
			.containsExactlyInAnyOrder("오찬혁", "오찬혁");
		assertThat(responses).extracting(PostResponse::getMemberId)
			.containsExactlyInAnyOrder(memberId, memberId);
	}

	@Test
	void 없는_게시글_조회() {
		// when & then
		assertThatThrownBy(() -> {
				restClient.get().uri("/api/posts/999")
					.retrieve()
					.body(PostResponse.class);
			}
		).isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	@Test
	void 조회수_두번_조회() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "손흥민").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();

		// when
		restClient.get().uri("/api/posts/" + postId)
			.retrieve()
			.body(PostResponse.class);
		PostResponse response = restClient.get().uri("/api/posts/" + postId)
			.retrieve()
			.body(PostResponse.class);

		// then
		assertThat(response.getId()).isEqualTo(postId);
		assertThat(response.getViewCount()).isEqualTo(2);
	}

	@Test
	void 인증_없이_게시글_작성_401() {
		// given - login()을 부르지 않음

		PostCreateRequest request = new PostCreateRequest();
		request.setTitle("제목");
		request.setContent("내용");

		// when & then
		assertThatThrownBy(() -> restClient.post().uri("/api/posts")
			.body(request)
			.retrieve()
			.body(PostResponse.class))
			.isInstanceOf(HttpClientErrorException.Unauthorized.class);
	}

	@Test
	void 조회는_인증_없이_가능() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();

		// 세션을 버림
		setUpRestClient();

		// when
		PostResponse response = restClient.get().uri("/api/posts/" + postId)
			.retrieve()
			.body(PostResponse.class);

		// then
		assertThat(response.getTitle()).isEqualTo("제목");
	}

	private PostResponse createPost(String title) {
		PostCreateRequest request = new PostCreateRequest();
		request.setTitle(title);
		request.setContent("내용");

		return restClient.post().uri("/api/posts")
			.body(request)
			.retrieve()
			.body(PostResponse.class);
	}
}