package hello.crud.postlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import hello.crud.member.MemberRepository;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.post.PostRepository;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.postlike.dto.PostLikeCreateRequest;
import hello.crud.postlike.dto.PostLikeResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostLikeApiTest {

	@LocalServerPort
	int port;

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostLikeRepository postLikeRepository;
	@Autowired
	PostRepository postRepository;

	RestClient restClient;

	@BeforeEach
	void beforeEach() {
		restClient = RestClient.create("http://localhost:" + port);
	}

	@AfterEach
	void afterEach() {
		postLikeRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void 좋아요_저장() {
		// given
		Long memberId = createMember("ohchanhyeok123", "호날두").getId();
		Long postId = createPost("제목", memberId).getId();

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
		Long memberId = createMember("ohchanhyeok123", "호날두").getId();
		Long postId = createPost("제목", memberId).getId();
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
		Long memberId1 = createMember("ohchanhyeok123", "손흥민").getId();
		Long memberId2 = createMember("ohchanhyeok1334241", "호날두").getId();
		Long postId = createPost("제목", memberId1).getId();

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
		Long memberId = createMember("ohchanhyeok123", "SON").getId();
		Long postId = createPost("제목", memberId).getId();
		createPostLike(memberId, postId);

		// when & then
		assertThatThrownBy(() -> {
			createPostLike(memberId, postId);
		}).isInstanceOf(HttpServerErrorException.InternalServerError.class);
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

	private PostLikeResponse createPostLike(Long memberId, Long postId) {
		PostLikeCreateRequest request = new PostLikeCreateRequest();
		request.setMemberId(memberId);

		return restClient.post().uri("/api/posts/" + postId + "/likes")
			.body(request)
			.retrieve()
			.body(PostLikeResponse.class);
	}
}