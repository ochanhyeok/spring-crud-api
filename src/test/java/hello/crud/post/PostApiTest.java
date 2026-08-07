package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import hello.crud.member.MemberRepository;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostApiTest {

	@LocalServerPort
	int port;

	@Autowired
	PostRepository postRepository;
	@Autowired
	MemberRepository memberRepository;

	RestClient restClient;

	@BeforeEach
	void beforeEach() {
		restClient = RestClient.create("http://localhost:" + port);
	}

	@AfterEach
	void afterEach() {
		postRepository.clearStore();
		memberRepository.clearStore();
	}

	@Test
	void 게시글_저장() {
		// given
		Long memberId = createMember("ochhs0829", "오찬혁").getId();

		// when
		PostResponse postResponse = createPost("오늘은 뭐 먹지??", memberId);

		// then
		assertThat(postResponse.getId()).isNotNull();
		assertThat(postResponse.getAuthorName()).isEqualTo("오찬혁");
		assertThat(postResponse.getTitle()).isEqualTo("오늘은 뭐 먹지??");
	}

	@Test
	void 게시글_조회() {
		// given
		Long memberId = createMember("ochhs0829", "오찬혁").getId();
		Long postId = createPost("hello world", memberId).getId();

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
		Long memberId = createMember("ochhs0829", "오찬혁").getId();
		createPost("테스트코드 어렵다~", memberId);
		createPost("hello world~", memberId);

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
		assertThatThrownBy(() -> {
				restClient.get().uri("/api/posts/999")
					.retrieve()
					.body(PostResponse.class);
			}
		).isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	private MemberResponse createMember(String loginId, String name) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName(name);

		request.setPassword("1234");
		return restClient.post().uri("/api/members")
			.body(request)
			.retrieve()
			.body(MemberResponse.class);
	}

	private PostResponse createPost(String title, Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setTitle(title);
		request.setContent("내용");
		request.setMemberId(memberId);

		return restClient.post().uri("/api/posts")
			.body(request)
			.retrieve()
			.body(PostResponse.class);
	}
}