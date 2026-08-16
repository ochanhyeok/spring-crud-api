package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.support.ServiceTestSupport;

class PostServiceTest extends ServiceTestSupport {

	@Autowired
	private PostService postService;

	@Test
	void save() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");

		// when
		PostResponse response = postService.create(createPostRequest("제목", memberId));

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
		PostResponse response = postService.create(createPostRequest("제목", memberId));

		// when
		PostResponse savedPost = postService.findOne(response.getId());

		// then
		assertThat(savedPost.getId()).isNotNull();
		assertThat(savedPost.getTitle()).isEqualTo("제목");
		assertThat(savedPost.getAuthorName()).isEqualTo("chanhyeok");
	}

	@Test
	void findAll() {
		// given
		Long memberId = testDataFactory.createMember("ohchanhyeok123");
		Long memberId2 = testDataFactory.createMember("ochhs0821");
		PostResponse response1 = postService.create(createPostRequest("제목1", memberId));
		PostResponse response2 = postService.create(createPostRequest("제목2", memberId2));

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
		postService.create(createPostRequest("제목", memberId));

		// when & then
		assertThatThrownBy(() -> postService.findOne(999L))
			.isInstanceOf(NoSuchElementException.class);
	}

	private PostCreateRequest createPostRequest(String title, Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setTitle(title);
		request.setContent("내용입니다.");
		request.setMemberId(memberId);
		return request;
	}
}