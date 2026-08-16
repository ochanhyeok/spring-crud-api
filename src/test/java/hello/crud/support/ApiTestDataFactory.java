package hello.crud.support;

import org.springframework.web.client.RestClient;

import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.comment.dto.CommentResponse;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.post.dto.PostCreateRequest;
import hello.crud.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiTestDataFactory {

	private final RestClient restClient;

	public MemberResponse createMember(String loginId, String name) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName(name);
		request.setPassword("1234");
		return restClient.post().uri("/api/members")
			.body(request)
			.retrieve()
			.body(MemberResponse.class);
	}

	public PostResponse createPost(String title, Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setMemberId(memberId);
		request.setTitle(title);
		request.setContent("내용");

		return restClient.post().uri("/api/posts")
			.body(request)
			.retrieve()
			.body(PostResponse.class);
	}

	public CommentResponse createComment(Long postId, Long memberId, String content) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setMemberId(memberId);
		request.setPostId(postId);
		request.setContent(content);

		return restClient.post().uri("/api/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
	}
}
