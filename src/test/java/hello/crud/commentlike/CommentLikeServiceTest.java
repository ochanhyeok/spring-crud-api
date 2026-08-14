package hello.crud.commentlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.crud.comment.CommentRepository;
import hello.crud.comment.CommentService;
import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.commentlike.dto.CommentLikeResponse;
import hello.crud.common.DuplicateLikeException;
import hello.crud.member.MemberRepository;
import hello.crud.member.MemberService;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.post.PostRepository;
import hello.crud.post.PostService;
import hello.crud.post.dto.PostCreateRequest;

@SpringBootTest
class CommentLikeServiceTest {

	@Autowired
	CommentLikeRepository commentLikeRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	CommentLikeService commentLikeService;
	@Autowired
	MemberService memberService;
	@Autowired
	PostService postService;
	@Autowired
	CommentService commentService;

	@AfterEach
	void afterEach() {
		commentLikeRepository.deleteAll();
		commentRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void like_성공() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long postId = createPost(memberId);
		Long commentId = createComment(postId, memberId);

		// when
		CommentLikeResponse response = commentLikeService.like(commentId, memberId);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(1);
	}

	@Test
	void unLike_성공() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long postId = createPost(memberId);
		Long commentId = createComment(postId, memberId);
		commentLikeService.like(commentId, memberId);

		// when
		CommentLikeResponse response = commentLikeService.unLike(commentId, memberId);

		// then
		assertThat(response.getCommentId()).isEqualTo(commentId);
		assertThat(response.getLikeCount()).isEqualTo(0);
	}

	@Test
	void like_중복_예외() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long postId = createPost(memberId);
		Long commentId = createComment(postId, memberId);
		commentLikeService.like(commentId, memberId);

		// when & then
		assertThatThrownBy(() -> commentLikeService.like(commentId, memberId))
			.isInstanceOf(DuplicateLikeException.class);
	}

	@Test
	void getLikeCount() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long memberId2 = createMember("ohchanhyeok123133");
		Long postId = createPost(memberId);
		Long commentId = createComment(postId, memberId);

		commentLikeService.like(commentId, memberId);
		commentLikeService.like(commentId, memberId2);

		// when
		long likeCount = commentLikeService.getLikeCount(commentId);

		// then
		assertThat(likeCount).isEqualTo(2);
	}

	@Test
	void hasLiked() {
		// given
		Long memberId = createMember("ohchanhyeok123");
		Long memberId2 = createMember("ohchanhyeok123133");
		Long postId = createPost(memberId);
		Long commentId = createComment(postId, memberId);

		commentLikeService.like(commentId, memberId);
		commentLikeService.like(commentId, memberId2);
		commentLikeService.unLike(commentId, memberId);

		// when
		boolean hasLikedMember = commentLikeService.hasLiked(commentId, memberId);
		boolean hasLikedMember2 = commentLikeService.hasLiked(commentId, memberId2);

		// then
		assertThat(hasLikedMember).isFalse();
		assertThat(hasLikedMember2).isTrue();
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

	private Long createComment(Long postId, Long memberId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setContent("댓글");
		request.setPostId(postId);
		request.setMemberId(memberId);
		return commentService.create(request).getId();
	}

}