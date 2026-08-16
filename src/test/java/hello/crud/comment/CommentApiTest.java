package hello.crud.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.support.ApiTestSupport;

class CommentApiTest extends ApiTestSupport {

	@Test
	void 댓글_저장() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		Long postId = apiTestDataFactory.createPost("hello world", memberId).getId();

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
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		Long postId = apiTestDataFactory.createPost("hello world", memberId).getId();
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
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();
		Long postId = apiTestDataFactory.createPost("hello world", memberId).getId();
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