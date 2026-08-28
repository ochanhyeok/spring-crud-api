package hello.crud.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.comment.dto.CommentUpdateRequest;
import hello.crud.common.AccessDeniedException;
import hello.crud.common.NotFoundException;
import hello.crud.support.ServiceTestSupport;

class CommentServiceTest extends ServiceTestSupport {

	@Autowired
	private CommentService commentService;

	@Test
	void save() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);

		// when
		CommentResponse response = commentService.create(createCommentRequest("코딩은 재밌다.", postId), memberId);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getContent()).isEqualTo("코딩은 재밌다.");
		assertThat(response.getMemberId()).isEqualTo(memberId);
		assertThat(response.getAuthorName()).isEqualTo("chanhyeok");
		assertThat(response.getPostId()).isEqualTo(postId);
	}

	@Test
	void findOne() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		CommentResponse response = commentService.create(createCommentRequest("hello world", postId), memberId);

		// when
		CommentResponse savedComment = commentService.findOne(response.getId());

		// then
		assertThat(savedComment.getId()).isNotNull();
		assertThat(savedComment.getContent()).isEqualTo("hello world");
		assertThat(savedComment.getMemberId()).isEqualTo(memberId);
		assertThat(savedComment.getAuthorName()).isEqualTo("chanhyeok");
	}

	@Test
	void findAll() {
		// given
		Long memberId1 = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ochhs0321");
		Long postId1 = testDataFactory.createPost(memberId1);
		Long postId2 = testDataFactory.createPost(memberId2);
		commentService.create(createCommentRequest("hello world1", postId1), memberId1);
		commentService.create(createCommentRequest("hello world2", postId2), memberId2);

		// when
		List<CommentResponse> commentResponses = commentService.findAll();

		// then
		assertThat(commentResponses.size()).isEqualTo(2);
		assertThat(commentResponses).extracting(CommentResponse::getContent)
			.containsExactlyInAnyOrder("hello world1", "hello world2");
		assertThat(commentResponses).extracting(CommentResponse::getAuthorName)
			.containsExactlyInAnyOrder("chanhyeok", "chanhyeok");
		assertThat(commentResponses).extracting(CommentResponse::getPostId)
			.containsExactlyInAnyOrder(postId1, postId2);
	}

	@Test
	void findOne_없는_id_예외() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);
		commentService.create(createCommentRequest("hello world", postId), memberId);

		// when & then
		assertThatThrownBy(() -> commentService.findOne(99L))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 다른회원이_댓글수정() {
		// given
		Long memberId = testDataFactory.createMember("ochanhyeok123");
		Long otherId = testDataFactory.createMember("other123");
		Long postId = testDataFactory.createPost(memberId);
		Long commentId = testDataFactory.createComment(postId, memberId);

		CommentUpdateRequest request = new CommentUpdateRequest();
		request.setContent("수정된 댓글 내용");

		// when & then
		assertThatThrownBy(() -> commentService.update(commentId, request, otherId))
			.isInstanceOf(AccessDeniedException.class);
	}

	private CommentCreateRequest createCommentRequest(String content, Long postId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setContent(content);
		request.setPostId(postId);
		return request;
	}
}