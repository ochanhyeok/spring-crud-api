package hello.crud.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import hello.crud.support.ServiceTestSupport;
import jakarta.persistence.EntityManagerFactory;

class PostN1Test extends ServiceTestSupport {

	@Autowired
	private PostService postService;

	@Autowired
	EntityManagerFactory emf;
	@Autowired
	TransactionTemplate transactionTemplate;

	@Test
	void N1fetchJoinTest() {
		// given
		for (int i = 0; i < 5; i++) {
			Long memberId = testDataFactory.createMember("user" + i);
			postService.create(createPostRequest("제목" + i), memberId);
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
