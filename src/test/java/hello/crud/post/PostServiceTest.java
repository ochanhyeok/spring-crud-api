package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hello.crud.comment.CommentService;
import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.support.ServiceTestSupport;

class PostServiceTest extends ServiceTestSupport {

	@Autowired
	private PostService postService;
	@Autowired
	private CommentService commentService;

	@Test
	void save() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");

		// when
		PostResponse response = postService.create(createPostRequest("제목"), memberId);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getTitle()).isEqualTo("제목");
		assertThat(response.getContent()).isEqualTo("내용입니다.");
		assertThat(response.getMemberId()).isEqualTo(memberId);
		assertThat(response.getAuthorName()).isEqualTo("chanhyeok");
	}

	@Test
	void findOne() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		PostResponse response = postService.create(createPostRequest("제목"), memberId);

		// when
		PostResponse savedPost = postService.findOne(response.getId());

		// then
		assertThat(savedPost.getId()).isNotNull();
		assertThat(savedPost.getTitle()).isEqualTo("제목");
		assertThat(savedPost.getAuthorName()).isEqualTo("chanhyeok");
		assertThat(savedPost.getViewCount()).isEqualTo(1);
	}

	@Test
	void findAll() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ochhs0821");
		PostResponse response1 = postService.create(createPostRequest("제목1"), memberId);
		PostResponse response2 = postService.create(createPostRequest("제목2"), memberId2);

		// when
		List<PostResponse> postResponses = postService.findAll();

		// then
		assertThat(postResponses.size()).isEqualTo(2);
		assertThat(postResponses).extracting(PostResponse::getTitle)
				.containsExactlyInAnyOrder("제목1", "제목2");
		assertThat(postResponses).extracting(PostResponse::getAuthorName)
			.containsExactlyInAnyOrder("chanhyeok", "chanhyeok");
	}

	@Test
	void findOne_없는_id_예외() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		postService.create(createPostRequest("제목"), memberId);

		// when & then
		assertThatThrownBy(() -> postService.findOne(999L))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void 조회수_증가() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);

		// when
		postService.findOne(postId);
		PostResponse response = postService.findOne(postId);

		// then
		assertThat(response.getViewCount()).isEqualTo(2);
	}

	@Test
	void 내부_조회로는_조회수_증가x() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long postId = testDataFactory.createPost(memberId);

		// when
		commentService.create(createCommentRequest("내용", postId), memberId);

		// then
		assertThat(postService.findPostById(postId).getViewCount()).isEqualTo(0);
	}

	private PostCreateRequest createPostRequest(String title) {
		PostCreateRequest request = new PostCreateRequest();
		request.setTitle(title);
		request.setContent("내용입니다.");
		return request;
	}

	private CommentCreateRequest createCommentRequest(String content, Long postId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setContent(content);
		request.setPostId(postId);
		return request;
	}
}