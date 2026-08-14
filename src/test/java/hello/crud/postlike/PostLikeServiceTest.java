package hello.crud.postlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.crud.common.DuplicateLikeException;
import hello.crud.member.MemberRepository;
import hello.crud.member.MemberService;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.post.PostRepository;
import hello.crud.post.PostService;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.postlike.dto.PostLikeResponse;

@SpringBootTest
class PostLikeServiceTest {

	@Autowired
	PostLikeService postLikeService;
	@Autowired
	PostService postService;
	@Autowired
	MemberService memberService;
	@Autowired
	PostLikeRepository postLikeRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	MemberRepository memberRepository;

	@AfterEach
	void afterEach() {
		postLikeRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void like_성공() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long postId = createPost(memberId);

		// when
		PostLikeResponse response = postLikeService.like(postId, memberId);

		// then
		assertThat(response.getLikeCount()).isEqualTo(1);
		assertThat(response.getPostId()).isEqualTo(postId);
		assertThat(response.isLiked()).isTrue();
	}

	@Test
	void unLike_성공() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long postId = createPost(memberId);
		postLikeService.like(postId, memberId);

		// when
		PostLikeResponse response = postLikeService.unLike(postId, memberId);

		// then
		assertThat(response.getLikeCount()).isEqualTo(0);
		assertThat(response.getPostId()).isEqualTo(postId);
		assertThat(response.isLiked()).isFalse();
	}

	@Test
	void like_중복_예외() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long postId = createPost(memberId);
		postLikeService.like(postId, memberId);

		// when & then
		assertThatThrownBy(() -> postLikeService.like(postId, memberId))
			.isInstanceOf(DuplicateLikeException.class);
	}

	@Test
	void getLikeCount() {
		// given
		Long memberId1 = createMember("ohchanhyeok123");
		Long memberId2 = createMember("ohchanhyeok123345");
		Long postId = createPost(memberId1);

		postLikeService.like(postId, memberId1);
		postLikeService.like(postId, memberId2);

		// when
		long likeCount = postLikeService.getLikeCount(postId);

		// then
		assertThat(likeCount).isEqualTo(2);
	}

	@Test
	void hasLiked() {
		// given
		Long memberId1 = createMember("ohchanhyeok123");
		Long memberId2 = createMember("ohchanhyeok123345");
		Long postId = createPost(memberId1);

		postLikeService.like(postId, memberId1);

		// when
		boolean member1liked = postLikeService.hasLiked(postId, memberId1);
		boolean member2liked = postLikeService.hasLiked(postId, memberId2);

		// then
		assertThat(member1liked).isTrue();
		assertThat(member2liked).isFalse();
	}

	private Long createMember(String loginId) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName("chanhyeok");
		request.setPassword("password");
		return memberService.create(request).getId();
	}

	private Long createPost(Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setTitle("제목");
		request.setContent("내용");
		request.setMemberId(memberId);
		return postService.create(request).getId();
	}

}