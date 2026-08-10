package hello.crud.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.member.MemberRepository;
import hello.crud.member.MemberService;
import hello.crud.member.MyBatisMemberRepository;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.post.MyBatisPostRepository;
import hello.crud.post.PostRepository;
import hello.crud.post.PostService;
import hello.crud.post.dto.PostCreateRequest;

@SpringBootTest
class CommentServiceTest {

	@Autowired
	CommentService commentService;
	@Autowired
	MyBatisCommentRepository commentRepository;
	// CommentRepository commentRepository;
	@Autowired
	MemberService memberService;
	@Autowired
	MyBatisMemberRepository memberRepository;
	// MemberRepository memberRepository;
	@Autowired
	PostService postService;
	@Autowired
	MyBatisPostRepository postRepository;
	// PostRepository postRepository;

	@AfterEach
	void afterEach() {
		commentRepository.clearStore();
		postRepository.clearStore();
		memberRepository.clearStore();
	}

	@Test
	void save() {
		// given
		Long memberId = createMember("ochhs0829");
		Long postId = createPost(memberId);

		// when
		CommentResponse response = commentService.create(createCommentRequest("코딩은 재밌다.", postId, memberId));

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
		Long memberId = createMember("ochhs0829");
		Long postId = createPost(memberId);
		CommentResponse response = commentService.create(createCommentRequest("hello world", postId, memberId));

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
		Long memberId1 = createMember("ochhs0829");
		Long memberId2 = createMember("ochhs0321");
		Long postId1 = createPost(memberId1);
		Long postId2 = createPost(memberId2);
		commentService.create(createCommentRequest("hello world1", postId1, memberId1));
		commentService.create(createCommentRequest("hello world2", postId2, memberId2));

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
		Long memberId = createMember("ochhs0829");
		Long postId = createPost(memberId);
		commentService.create(createCommentRequest("hello world", postId, memberId));

		// when & then
		assertThatThrownBy(() -> commentService.findOne(99L))
			.isInstanceOf(NoSuchElementException.class);
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

	private CommentCreateRequest createCommentRequest(String content, Long postId, Long memberId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setContent(content);
		request.setPostId(postId);
		request.setMemberId(memberId);
		return request;
	}
}