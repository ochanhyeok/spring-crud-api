package hello.crud.commentlike;

import hello.crud.comment.Comment;
import hello.crud.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
	name = "comment_like",
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {
				"comment_id", "member_id"
			}
		)
	}
)
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public CommentLike(Comment comment, Member member) {
		this.comment = comment;
		this.member = member;
	}
}
