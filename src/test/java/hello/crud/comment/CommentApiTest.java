package hello.crud.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.comment.dto.CommentUpdateRequest;
import hello.crud.support.ApiTestSupport;

class CommentApiTest extends ApiTestSupport {

	@Test
	void 댓글_저장() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("hello world").getId();

		// when
		CommentResponse response = createComment("코딩은 재밌다!!", postId);

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
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("hello world").getId();
		Long commentId = createComment("java spring 마스터하기", postId).getId();

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
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("hello world").getId();
		createComment("코딩은 재밌다!!", postId);
		createComment("코딩은 재밌다2!!", postId);
		createComment("코딩은 재밌다3!!", postId);

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

	@Test
	void 본인이_수정한_댓글() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글 내용").getId();

		CommentUpdateRequest request = new CommentUpdateRequest();
		request.setContent("수정된 댓글");

		// when
		CommentResponse response = restClient.put().uri("/api/comments/" + commentId)
			.body(request)
			.retrieve()
			.body(CommentResponse.class);

		// then
		assertThat(response.getId()).isEqualTo(commentId);
		assertThat(response.getContent()).isEqualTo("수정된 댓글");
	}

	@Test
	void 다른회원이_댓글_수정() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글").getId();

		apiTestDataFactory.createMember("another123", "호날두");
		login("another123");

		CommentUpdateRequest request = new CommentUpdateRequest();
		request.setContent("수정된 댓글");

		// when & then
		assertThatThrownBy(() -> restClient.put().uri("/api/comments/" + commentId)
			.body(request)
			.retrieve()
			.body(CommentResponse.class))
			.isInstanceOf(HttpClientErrorException.Forbidden.class);
	}

	@Test
	void 인증없이_댓글수정_401() {
		// given - login()을 부르지 않는다
		CommentUpdateRequest request = new CommentUpdateRequest();
		request.setContent("수정된 댓글");

		// when & then
		assertThatThrownBy(() -> restClient.put().uri("/api/comments/1")
			.body(request)
			.retrieve()
			.body(CommentResponse.class))
			.isInstanceOf(HttpClientErrorException.Unauthorized.class);
	}

	private CommentResponse createComment(String content, Long postId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setPostId(postId);
		request.setContent(content);

		return restClient.post().uri("/api/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
	}

}