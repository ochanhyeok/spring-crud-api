package hello.crud.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

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
		CommentResponse response = apiTestDataFactory.createComment(postId, "코딩은 재밌다!!");

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
		Long commentId = apiTestDataFactory.createComment(postId, "java spring 마스터하기").getId();

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
		apiTestDataFactory.createComment(postId, "코딩은 재밌다!!");
		apiTestDataFactory.createComment(postId, "코딩은 재밌다2!!");
		apiTestDataFactory.createComment(postId, "코딩은 재밌다3!!");

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

	@Test
	void 본인이_댓글_삭제() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글내용").getId();

		// when
		ResponseEntity<Void> response = restClient.delete().uri("/api/comments/" + commentId)
			.retrieve()
			.toBodilessEntity();

		// then
		assertThat(response.getStatusCode().value()).isEqualTo(200);
	}

	@Test
	void 다른회원이_댓글_삭제() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글내용").getId();

		apiTestDataFactory.createMember("other123", "호날두");
		login("other123");

		// when & then
		assertThatThrownBy(() -> restClient.delete().uri("/api/comments/" + commentId)
			.retrieve()
			.toBodilessEntity())
			.isInstanceOf(HttpClientErrorException.Forbidden.class);
	}

	@Test
	void 인증없이_댓글삭제_401() {
		// given - login 없음

		// when & then
		assertThatThrownBy(() -> restClient.delete().uri("/api/comments/1")
			.retrieve()
			.toBodilessEntity())
			.isInstanceOf(HttpClientErrorException.Unauthorized.class);
	}

	@Test
	void 삭제된_댓글_조회_404() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글내용").getId();

		restClient.delete().uri("/api/comments/" + commentId)
			.retrieve()
			.toBodilessEntity();

		// when & then
		assertThatThrownBy(() -> restClient.get().uri("/api/comments/" + commentId)
			.retrieve()
			.toBodilessEntity())
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	@Test
	void 게시글_삭제하면_댓글도_조회_안됨() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();
		Long commentId = apiTestDataFactory.createComment(postId, "댓글내용").getId();

		// when - 게시글만 삭제
		restClient.delete().uri("/api/posts/" + postId)
			.retrieve()
			.toBodilessEntity();

		// then - 댓글도 조회되지 않는다
		assertThatThrownBy(() -> restClient.get().uri("/api/comments/" + commentId)
			.retrieve()
			.toBodilessEntity())
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	@Test
	void 삭제된_게시글에는_댓글을_달_수_없다() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");
		login("ohchanhyeok123");
		Long postId = apiTestDataFactory.createPost("제목").getId();

		restClient.delete().uri("/api/posts/" + postId)
			.retrieve()
			.toBodilessEntity();

		// when & then
		assertThatThrownBy(() -> apiTestDataFactory.createComment(postId, "댓글내용"))
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

}