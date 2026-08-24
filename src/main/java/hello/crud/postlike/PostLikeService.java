package hello.crud.postlike;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.common.DuplicateLikeException;
import hello.crud.member.Member;
import hello.crud.member.MemberRepository;
import hello.crud.post.Post;
import hello.crud.post.PostRepository;
import hello.crud.postlike.dto.PostLikeResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeRepository postLikeRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public PostLikeResponse like(Long postId, Long memberId) {
		if (!postRepository.existsByIdAndDeletedAtIsNull(postId)) {
			throw new NoSuchElementException("게시글이 없습니다. id=" + postId);
		}
		if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
			throw new DuplicateLikeException("이미 좋아요를 누른 게시글입니다.");
		}

		Post post = postRepository.getReferenceById(postId);
		Member member = memberRepository.getReferenceById(memberId);
		PostLike postLike = PostLike.builder()
			.post(post)
			.member(member)
			.build();

		try {
			postLikeRepository.save(postLike);
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateLikeException("이미 좋아요를 누른 게시글입니다.");
		}
		Long likeCount = postLikeRepository.countByPostId(postId);

		return PostLikeResponse.of(postId, likeCount, true);
	}

	@Transactional
	public PostLikeResponse unLike(Long postId, Long memberId) {
		postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
		Long likeCount = postLikeRepository.countByPostId(postId);
		return PostLikeResponse.of(postId, likeCount, false);
	}

	public long getLikeCount(Long postId) {
		return postLikeRepository.countByPostId(postId);
	}

	public boolean hasLiked(Long postId, Long memberId) {
		return postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
	}
}
