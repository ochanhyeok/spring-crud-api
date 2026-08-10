package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.crud.member.MemberRepository;
import hello.crud.member.MemberService;
import hello.crud.member.MyBatisMemberRepository;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;

@SpringBootTest
class PostServiceTest {

	@Autowired
	PostService postService;
	@Autowired
	MemberService memberService;
	@Autowired
	MyBatisPostRepository postRepository;
	// PostRepository postRepository;
	@Autowired
	MyBatisMemberRepository memberRepository;
	// MemberRepository memberRepository;

	@AfterEach
	void afterEach() {
		postRepository.clearStore();
		memberRepository.clearStore();
	}

	@Test
	void save() {
		// given
		Long memberId = createMember("ochhs0829");

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
		Long memberId = createMember("ochhs0829");
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
		Long memberId = createMember("ochhs0829");
		Long memberId2 = createMember("ochhs0821");
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
		Long memberId = createMember("ochhs0829");
		postService.create(createPostRequest("제목", memberId));

		// when & then
		assertThatThrownBy(() -> postService.findOne(999L))
			.isInstanceOf(NoSuchElementException.class);
	}

	private Long createMember(String loginId) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName("chanhyeok");
		request.setPassword("password");
		return memberService.create(request).getId();
	}

	private PostCreateRequest createPostRequest(String title, Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setTitle(title);
		request.setContent("내용입니다.");
		request.setMemberId(memberId);
		return request;
	}
}