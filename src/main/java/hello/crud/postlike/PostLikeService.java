package hello.crud.postlike;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.common.DuplicateLikeException;
import hello.crud.common.ErrorCode;
import hello.crud.common.NotFoundException;
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
			throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
		}
		if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
			throw new DuplicateLikeException(ErrorCode.DUPLICATE_POST_LIKE);
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
			throw new DuplicateLikeException(ErrorCode.DUPLICATE_POST_LIKE);
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
