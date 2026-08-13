package hello.crud.commentlike;

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

import hello.crud.comment.CommentRepository;
import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.commentlike.dto.CommentLikeCreateRequest;
import hello.crud.commentlike.dto.CommentLikeResponse;
import hello.crud.member.MemberRepository;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.post.PostRepository;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentLikeApiTest {

	@LocalServerPort
	int port;

	RestClient restClient;

	@Autowired
	CommentRepository commentRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	CommentLikeRepository commentLikeRepository;
	@Autowired
	PostRepository postRepository;

	@BeforeEach
	void beforeEach() {
		restClient = RestClient.create("http://localhost:" + port);
	}

	@AfterEach
	void afterEach() {
		commentLikeRepository.deleteAll();
		commentRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void 좋아요_저장() {
		// given
		Long memberId = createMember("ohchanhyeok123", "호날두").getId();
		Long postId = createPost("제목", memberId).getId();
		Long commentId = createComment(memberId, postId, "내용").getId();

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
		Long memberId = createMember("ohchanhyeok123", "호날두").getId();
		Long postId = createPost("제목", memberId).getId();
		Long commentId = createComment(memberId, postId, "내용").getId();

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
		Long memberId = createMember("ohchanhyeok123", "호날두").getId();
		Long memberId2 = createMember("ohchanhyeok12121313", "손흥민").getId();
		Long postId = createPost("제목", memberId).getId();
		Long commentId = createComment(memberId, postId, "내용").getId();

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
		Long memberId = createMember("ohchanhyeok123", "호날두").getId();
		Long postId = createPost("제목", memberId).getId();
		Long commentId = createComment(memberId, postId, "내용").getId();
		createCommentLike(memberId, commentId);

		// when & then
		assertThatThrownBy(() -> createCommentLike(memberId, commentId))
			.isInstanceOf(HttpServerErrorException.InternalServerError.class);
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

	private CommentResponse createComment(Long memberId, Long postId, String content) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setPostId(postId);
		request.setMemberId(memberId);
		request.setContent(content);

		return restClient.post().uri("/api/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
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