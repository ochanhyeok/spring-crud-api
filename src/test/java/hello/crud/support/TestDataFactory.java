package hello.crud.support;

import org.springframework.stereotype.Component;

import hello.crud.comment.CommentService;
import hello.crud.comment.dto.CommentCreateRequest;
import hello.crud.member.MemberService;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.post.PostService;
import hello.crud.post.dto.PostCreateRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDataFactory {

	private final MemberService memberService;
	private final PostService postService;
	private final CommentService commentService;

	public Long createMember(String loginId) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName("chanhyeok");
		request.setPassword("1234");
		return memberService.create(request).getId();
	}

	public Long createPost(Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setBoardId(1L);
		request.setTitle("제목");
		request.setContent("내용");
		return postService.create(request, memberId).getId();
	}

	public Long createPost(String title, Long memberId) {
		PostCreateRequest request = new PostCreateRequest();
		request.setBoardId(1L);
		request.setTitle(title);
		request.setContent("내용");
		return postService.create(request, memberId).getId();
	}

	public Long createComment(Long postId, Long memberId) {
		CommentCreateRequest request = new CommentCreateRequest();
		request.setContent("내용");
		request.setPostId(postId);
		return commentService.create(request, memberId).getId();
	}
}
