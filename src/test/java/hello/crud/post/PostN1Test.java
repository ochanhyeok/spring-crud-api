package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import hello.crud.comment.CommentRepository;
import hello.crud.comment.CommentService;
import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.member.MemberRepository;
import hello.crud.member.MemberService;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import jakarta.persistence.EntityManagerFactory;

@SpringBootTest
public class PostN1Test {

	@Autowired
	MemberService memberService;
	@Autowired
	PostService postService;
	@Autowired
	PostRepository postRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	EntityManagerFactory emf;
	@Autowired
	TransactionTemplate transactionTemplate;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CommentService commentService;

	@AfterEach
	void afterEach() {
		commentRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void N1fetchJoinTest() {
		// given
		for (int i = 0; i < 5; i++) {
			Long memberId = createMember("user" + i);
			postService.create(createPostRequest("제목" + i, memberId));
		}

		Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
		stats.clear();

		// when
		List<PostResponse> postResponses = postService.findAll();

		// then
		long queryCount = stats.getPrepareStatementCount();
		System.out.println("=== 실행 쿼리 수 = " + queryCount + " ===");
		assertThat(postResponses).hasSize(5);
		assertThat(queryCount).isEqualTo(1);
	}

	@Test
	void N1batchSize() {
		// given
		Long memberId = createMember("user");
		for (int i = 0; i < 5; i++) {
			Long postId = createPost(memberId);
			commentService.create(createCommentRequest("댓글 1", postId, memberId));
			commentService.create(createCommentRequest("댓글 2", postId, memberId));
		}

		Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
		stats.clear();

		// when
		transactionTemplate.executeWithoutResult(status -> {
			List<Post> posts = postRepository.findAll();
			for (Post post : posts) {
				post.getComments().size(); // 이 순간 그 글의 댓글 조회 쿼리 발생
			}
		});

		// then
		long queryCount = stats.getPrepareStatementCount();
		System.out.println("=== 실행 쿼리 수 = " + queryCount + " ===");
		assertThat(queryCount).isEqualTo(2); // 게시글 목록 1 + 댓글 컬렉션 5
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
