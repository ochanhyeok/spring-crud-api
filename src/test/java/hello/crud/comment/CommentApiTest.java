package hello.crud.comment;

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

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.member.MemberRepository;
import hello.crud.member.MyBatisMemberRepository;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.post.MyBatisPostRepository;
import hello.crud.post.PostRepository;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentApiTest {

	@LocalServerPort
	int port;

	@Autowired
	MyBatisMemberRepository memberRepository;
	// MemberRepository memberRepository;
	@Autowired
	MyBatisPostRepository postRepository;
	// PostRepository postRepository;
	@Autowired
	MyBatisCommentRepository commentRepository;
	// CommentRepository commentRepository;

	RestClient restClient;

	@BeforeEach
	void beforeEach() {
		restClient = RestClient.create("http://localhost:" + port);
	}

	@AfterEach
	void afterEach() {
		memberRepository.clearStore();
		postRepository.clearStore();
		commentRepository.clearStore();
	}

	@Test
	void 댓글_저장() {
		// given
		Long memberId = createMember("ochhs0829", "오찬혁").getId();
		Long postId = createPost("hello world", memberId).getId();

		// when
		CommentResponse response = createComment("코딩은 재밌다!!", postId, memberId);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getPostId()).isEqualTo(postId);
		assertThat(response.getMemberId()).isEqualTo(memberId);
		assertThat(response.getAuthorName()).isEqualTo("오찬혁");
		assertThat(response.getContent()).isEqualTo("코딩은 재밌다!!");
	}

	@Test
	void 댓글_조회() {
		// given
		Long memberId = createMember("ochhs0829", "오찬혁").getId();
		Long postId = createPost("hello world", memberId).getId();
		Long commentId = createComment("java spring 마스터하기", postId, memberId).getId();

		// when
		CommentResponse response = restClient.get()
			.uri("/api/comments/" + commentId)
			.retrieve()
			.body(CommentResponse.class);

		// then
		assertThat(response.getId()).isEqualTo(commentId);
		assertThat(response.getPostId()).isEqualTo(postId);
		assertThat(response.getMemberId()).isEqualTo(memberId);
		assertThat(response.getContent()).isEqualTo("java spring 마스터하기");
	}

	@Test
	void 댓글_목록() {
		// given
		Long memberId = createMember("ochhs0829", "오찬혁").getId();
		Long postId = createPost("hello world", memberId).getId();
		createComment("코딩은 재밌다!!", postId, memberId);
		createComment("코딩은 재밌다2!!", postId, memberId);
		createComment("코딩은 재밌다3!!", postId, memberId);

		// when
		List<CommentResponse> responses = restClient.get()
			.uri("/api/comments")
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		// then
		assertThat(responses.size()).isEqualTo(3);
		assertThat(responses).extracting(CommentResponse::getContent)
			.containsExactlyInAnyOrder("코딩은 재밌다!!", "코딩은 재밌다2!!", "코딩은 재밌다3!!");
		assertThat(responses).extracting(CommentResponse::getMemberId)
			.containsExactlyInAnyOrder(memberId, memberId, memberId);
		assertThat(responses).extracting(CommentResponse::getAuthorName)
			.containsExactlyInAnyOrder("오찬혁", "오찬혁", "오찬혁");
	}

	@Test
	void 없는_댓글_조회() {
		assertThatThrownBy(() -> {
			restClient.get().uri("/api/comments/999")
				.retrieve()
				.body(CommentResponse.class);
		}).isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	private MemberResponse createMember(String loginId, String name) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName(name);
		request.setPassword("12345");

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

	private CommentResponse createComment(String content, Long postId, Long memberId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setPostId(postId);
		request.setMemberId(memberId);
		request.setContent(content);

		return restClient.post().uri("/api/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
	}

}