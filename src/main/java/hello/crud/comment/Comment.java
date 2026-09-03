package hello.crud.comment;

import java.time.LocalDateTime;

import hello.crud.common.BaseTimeEntity;
import hello.crud.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 500)
	private String content;

	private Long postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private LocalDateTime deletedAt;

	@Builder
	public Comment(String content, Long postId, Member member) {
		this.content = content;
		this.postId = postId;
		this.member = member;
	}

	public void update(String content) {
		this.content = content;
	}

	public boolean isWrittenBy(Long memberId) {
		return this.member.getId().equals(memberId);
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}
}
