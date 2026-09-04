package hello.crud.post;

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
public class Post extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long boardId;

	@Column(length = 100)
	private String title;

	@Column(length = 3000)
	private String content;
	private long viewCount = 0L;
	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Post(Long boardId, String title, String content, Member member) {
		this.boardId = boardId;
		this.title = title;
		this.content = content;
		this.member = member;
	}

	public void update(String title, String content) {
		this.title = title;
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
