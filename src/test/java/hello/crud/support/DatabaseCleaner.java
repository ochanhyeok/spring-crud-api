package hello.crud.support;

import org.springframework.stereotype.Component;

import hello.crud.comment.CommentRepository;
import hello.crud.commentlike.CommentLikeRepository;
import hello.crud.member.MemberRepository;
import hello.crud.post.PostRepository;
import hello.crud.postlike.PostLikeRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseCleaner {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;
	private final CommentLikeRepository commentLikeRepository;

	public void clean() {
		postLikeRepository.deleteAll();
		commentLikeRepository.deleteAll();
		commentRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

}
